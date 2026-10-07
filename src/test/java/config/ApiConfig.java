package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.qameta.allure.restassured.AllureRestAssured;

public class ApiConfig {

    public static void configure() {
        RestAssured.baseURI = TestConfig.getBaseUrl();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RestAssured.config = RestAssured.config()
                .objectMapperConfig(
                        ObjectMapperConfig.objectMapperConfig()
                                .jackson2ObjectMapperFactory(
                                        (type, charset) -> objectMapper
                                )
                );

        RestAssured.replaceFiltersWith(
                new AllureRestAssured()
        );
    }
}
