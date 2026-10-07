package config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

public class UiConfig {

    public static void configure() {
        Configuration.browser = TestConfig.getBrowser();
        Configuration.baseUrl = TestConfig.getUiUrl();
        Configuration.headless = TestConfig.isHeadless();
        Configuration.browserSize = "1920x1080";

        SelenideLogger.addListener(
                "allure",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
                        .includeSelenideSteps(false)
        );
    }
}
