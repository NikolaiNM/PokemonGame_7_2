package ru.pokemonbattle.framework;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Перечисление доступных окружений и их базовых URL.
 */
public enum Environment {
    PROD("https://pokemonbattle.ru"),
    STAGE("https://pokemonbattle-stage.ru");

    private final String baseUrl;

    Environment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Преобразует стоку в enum окружения.
     *
     * @param envName название окружения (например "PROD" или "STAGE")
     * @return соответствующее окружение (PROD, если строка пустая или null)
     * @throws IllegalArgumentException если переданно неизветсное значение
     */
    public static Environment fromString(String envName) {
        if (envName == null || envName.trim().isEmpty()) {
            return PROD;
        }

        String cleanName = envName.trim().toUpperCase();
        for (Environment env : Environment.values()) {
            if (env.name().equals(cleanName)) {
                return env;
            }
        }

        String availableEnvs = Arrays.stream(Environment.values())
                .map(Environment::name)
                .collect(Collectors.joining(","));

        throw new IllegalArgumentException(
                "Неизвестное окружение: '" + envName + "'. Допустимые значения: " + availableEnvs
        );
    }
}
