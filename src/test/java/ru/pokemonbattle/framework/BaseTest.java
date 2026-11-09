package ru.pokemonbattle.framework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Базовый класс для тестов.
 * Управляет жизненным циклом WebDriver и предоставляет вспомогательные методы.
 */
public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = BrowserFactory.createDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Открывает страницу по относительному пути, автоматически подставляя базовый URL из конфига.
     *
     * @param path относительный путь (например "/login" или "login")
     */
    protected void open(String path) {
        driver.get(Config.getUrl(path));
    }

    /**
     * Короткий алиас для driver.findElement().
     */
    protected WebElement $(By locator) {
        return driver.findElement(locator);
    }
}
