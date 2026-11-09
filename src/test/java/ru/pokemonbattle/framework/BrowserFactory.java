package ru.pokemonbattle.framework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Фабрика для создания экземпляров WebDriver.
 * <p>
 * Управление через настройки (файлы properties или аргументы JVM):
 * <ul>
 *  <li>{@code browser} — браузер (chrome/firefox), по умолчанию chrome</li>
 *  <li>{@code headless} — режим без окна (true/false), по умолчанию false</li>
 * </ul>
 * <p>
 * Пример запуска:
 * {@code mvn test -Dbrowser=chrome -Dheadless=true}
 */
public class BrowserFactory {

    /**
     * Создает и настраивает WebDriver на основе текущей конфигурации.
     *
     * @return настроенный экземпляр WebDriver
     * @throws IllegalArgumentException если браузер не поддерживается
     */
    public static WebDriver createDriver() {
        String browserName = Config.get("browser", "chrome").trim().toLowerCase();
        boolean isHeadless = Boolean.parseBoolean(Config.get("headless", "false"));

        return switch (browserName) {
            case "chrome" -> createChromeDriver(isHeadless);
            case "firefox" -> createFirefoxDriver(isHeadless);
            case "edge" -> createEdgeDriver(isHeadless);
            default -> throw new IllegalArgumentException("Браузер " + browserName + " не поддерживается");
        };
    }

    private static WebDriver createChromeDriver(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");

        if (isHeadless) {
            options.addArguments("--headless=new");
        }

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean isHeadless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        if (isHeadless) {
            options.addArguments("--headless");
        }

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean isHeadless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--window-size=1920,1080");

        if (isHeadless) {
            options.addArguments("--headless=new");
        }

        return new EdgeDriver(options);
    }
}
