package ru.pokemonbattle.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.pokemonbattle.TestData;
import ru.pokemonbattle.framework.BaseTest;


import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseTest {
    private static final By EMAIL_INPUT = By.cssSelector("input[name='email']");
    private static final By PASSWORD_INPUT = By.cssSelector("input[name='password']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    private static final By TRAINER_ID = By.cssSelector(".header_card_trainer_id_num");


    @Test
    @DisplayName("Успешная авторизация и проверка отображения ID тренера")
    public void testSuccessfulLogin() {
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
    }
}
