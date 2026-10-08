package config;

public class TestConfig {
    public static String getBaseUrl() {
        return System.getProperty("baseUrl", "http://localhost:8080");
    }

    public static String getDbUrl() {
        return System.getProperty(
                "dbUrl",
                "jdbc:postgresql://localhost:5432/paymentsdb"
        );
    }

    public static String getDbUser() {
        return System.getProperty(
                "dbUser",
                "postgres"
        );
    }

    public static String getDbPassword() {
        return System.getProperty(
                "dbPassword",
                "postgres"
        );
    }

    public static String getUiUrl() {
        return System.getProperty(
                "uiUrl",
                "http://localhost:8080"
        );
    }

    public static String getBrowser() {
        return System.getProperty(
                "browser",
                "chrome"
        );
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(
                System.getProperty(
                        "headless",
                        "false"
                )
        );
    }

    public static String getKafkaBootstrapServers() {
        return System.getProperty(
                "kafkaBootstrapServers",
                "localhost:9092"
        );
    }
}
