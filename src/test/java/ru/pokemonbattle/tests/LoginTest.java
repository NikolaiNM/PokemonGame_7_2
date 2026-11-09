package ru.pokemonbattle.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.pokemonbattle.TestData;
import ru.pokemonbattle.framework.BaseTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseTest {
    private static final By EMAIL_INPUT = By.cssSelector("input[name='email']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[name='password']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    private static final By TRAINER_ID = By.cssSelector(".header_card_trainer_id_num");

    @Test
    @DisplayName("Успешная авторизация и проверка отображения ID тренера")
    public void testSuccessfulLogin() {
        try {
            open("/login");

            $(EMAIL_INPUT).sendKeys(TestData.LOGIN);
            $(PASSWORD_INPUT).sendKeys(TestData.PASSWORD);
            $(SUBMIT_BUTTON).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(TRAINER_ID));

            assertThat(driver.getCurrentUrl())
                    .as("Редирект на главную страницу")
                    .isEqualTo(TestData.BASE_URL);

            String trainerIdText = $(TRAINER_ID).getText();
            assertThat(trainerIdText)
                    .as("ID тренера")
                    .isEqualTo(TestData.EXPECTED_TRAINER_ID);

        } catch (Throwable e) {
            // ЕСЛИ ТЕСТ УПАЛ: делаем скриншот прямо здесь
            try {
                File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = Paths.get("target", "screenshots", "FAILED_LoginTest.png");
                Files.createDirectories(destination.getParent());
                Files.copy(src.toPath(), destination);
                System.out.println("📸 Скриншот ошибки сохранен: " + destination.toAbsolutePath());

                // Для верности выведем текущий URL и заголовок страницы в консоль
                System.out.println("🔗 URL в момент падения: " + driver.getCurrentUrl());
                System.out.println("📄 Title в момент падения: " + driver.getTitle());
            } catch (Exception screenshotError) {
                System.err.println("Не удалось сделать скриншот: " + screenshotError.getMessage());
            }
            // Пробрасываем оригинальную ошибку, чтобы тест в CI все равно подсветился красным
            throw e;
        }
    }
}
