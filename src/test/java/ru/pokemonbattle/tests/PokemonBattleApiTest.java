package ru.pokemonbattle.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import ru.pokemonbattle.TestData;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class PokemonBattleApiTest {

    private String trainerToken;
    private String trainerId;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "https://api.pokemonbattle.ru/v2";

        Response authResponse = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", TestData.LOGIN,
                        "password", TestData.PASSWORD
                ))
                .when().post("/auth")
                .then().statusCode(200).extract().response();

        trainerToken = authResponse.jsonPath().getString("trainer_token");
        trainerId = authResponse.jsonPath().getString("id");

        assertThat(trainerToken).isNotEmpty().as("Токен тренера не должен быть пустым");
    }

    @RepeatedTest(20)
    @DisplayName("Умная битва: переиспользовать покемона или создать нового, и проверить победителя")
    public void testSmartPokemonBattleFlow() {

        // ШАГ 1: Проверяем, есть ли у нас УЖЕ готовый к бою покемон
        Response myPokemonsResponse = given()
                .header("trainer_token", trainerToken)
                .queryParam("trainer_id", trainerId)
                .when().get("/pokemons")
                .then().statusCode(200).extract().response();

        List<Map<String, Object>> myPokemons = myPokemonsResponse.jsonPath().getList("data");
        String myPokemonId = null;

        // Ищем нашего покемона, который: 1. Наш, 2. В покеболе, 3. Активен (не в нокауте!)
        for (Map<String, Object> pokemon : myPokemons) {
            if (pokemon.get("name").toString().startsWith("TestPika")) {
                int status = (int) pokemon.get("status");          // 1 = активен, 0 = в нокауте
                int inPokeball = (int) pokemon.get("in_pokeball"); // 1 = в покеболе

                if (status == 1 && inPokeball == 1) {
                    myPokemonId = pokemon.get("id").toString();
                    System.out.println("✅ Найден существующий живой покемон для битвы (ID: " + myPokemonId + ")");
                    break; // Нашли живого и готового к бою! Выходим из цикла.
                }
            }
        }

        // ШАГ 2: Если живого покемона нет, пытаемся создать нового
        if (myPokemonId == null) {
            System.out.println("⚠️ Готовых к бою покемонов не найдено. Пробуем создать нового...");

            // Сначала просто пробуем создать. Если лимит не достигнут — всё пройдет отлично!
            String newName = "TestPika" + (int)(Math.random() * 100);
            Response createResponse = given()
                    .header("trainer_token", trainerToken)
                    .contentType(ContentType.JSON)
                    .body(Map.of("name", newName, "photo_id", 1))
                    .when().post("/pokemons")
                    .then().extract().response(); // Не проверяем статус сразу, чтобы прочитать тело ответа

            if (createResponse.statusCode() == 201) {
                myPokemonId = createResponse.jsonPath().getString("id");
                System.out.println("🆕 Создан новый покемон (ID: " + myPokemonId + ", Имя: " + newName + ")");

                // Ловим в покебол
                given()
                        .header("trainer_token", trainerToken)
                        .contentType(ContentType.JSON)
                        .body(Map.of("pokemon_id", myPokemonId))
                        .when().post("/trainers/add_pokeball")
                        .then().statusCode(200);
                System.out.println("🔵 Новый покемон успешно пойман в покебол");

            } else if (createResponse.statusCode() == 400) {
                // АГА! Вот он, лимит в 5 покемонов.
                // Проверяем, можем ли мы автоматически "освободить" место, отправив старых в нокаут
                String errorMessage = createResponse.jsonPath().getString("message");
                System.out.println("❌ Не удалось создать: " + errorMessage);

                // Ищем среди наших покемонов тех, кто в нокауте (status == 0), чтобы их "утилизировать"
                // В API PokemonBattle для этого можно использовать эндпоинт /pokemons/knockout или просто сообщить пользователю
                System.out.println("💡 РЕШЕНИЕ: У вас достигнут лимит в 5 покемонов, и все они, вероятно, в нокауте.");
                System.out.println("💡 Пожалуйста, удалите старых покемонов через UI или API, чтобы тест мог создать нового.");

                // Падаем с понятной ошибкой, а не с абстрактным "size < 5"
                throw new AssertionError("Лимит покемонов (5) достигнут. Невозможно создать нового для теста. Очистите аккаунт.");
            } else {
                // Если какая-то другая ошибка (например, 401 или 500), просто падаем
                createResponse.then().statusCode(201);
            }
        }

        // (ШАГ 3 УДАЛЕН: он был избыточным, так как покемон уже гарантированно в покеболе)

        // ШАГ 4: Ищем слабого противника (чужого, в покеболе, активного)
        Response opponentsResponse = given()
                .header("trainer_token", trainerToken)
                .queryParam("in_pokeball", 1)
                .queryParam("status", 1)
                .queryParam("stage", 1)
                .queryParam("sort", "asc_attack")
                .queryParam("per_page", 10)
                .when().get("/pokemons")
                .then().statusCode(200).extract().response();

        List<Map<String, Object>> opponents = opponentsResponse.jsonPath().getList("data");
        String opponentPokemonId = null;

        for (Map<String, Object> pokemon : opponents) {
            if (!pokemon.get("trainer_id").toString().equals(trainerId)) {
                opponentPokemonId = pokemon.get("id").toString();
                break;
            }
        }

        assertThat(opponentPokemonId).isNotNull().as("Не удалось найти противника для битвы");

        // ШАГ 5: Атакуем!
        Response battleResponse = given()
                .header("trainer_token", trainerToken)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "attacking_pokemon", myPokemonId,
                        "defending_pokemon", opponentPokemonId
                ))
                .when().post("/battle")
                .then().statusCode(200).extract().response();

        // ШАГ 6: Выводим и проверяем, кто победил
        String battleResult = battleResponse.jsonPath().getString("result");
        String battleMessage = battleResponse.jsonPath().getString("message");

        System.out.println("=========================================");
        System.out.println("🏆 РЕЗУЛЬТАТ БИТВЫ: " + battleResult);
        System.out.println("📝 Сообщение API: " + battleMessage);
        System.out.println("=========================================");

        assertThat(battleMessage).isEqualTo("Битва проведена");

        assertThat(battleResult).matches(".*(победил|проиграл).*")
                .as("В результате битвы должно быть указано, кто победил или проиграл");
    }
}
