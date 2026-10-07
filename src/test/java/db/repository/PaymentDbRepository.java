package db.repository;

import config.TestConfig;
import db.model.PaymentDb;
import io.qameta.allure.Step;

import java.sql.*;
import java.util.Optional;

public class PaymentDbRepository {

    @Step("Получить платеж {id} из БД")
    public Optional<PaymentDb> findById(Long id) {

        String sql = """
                SELECT id,
                       amount,
                       currency,
                       recipient,
                       inn,
                       purpose,
                       status,
                       description,
                       created_at,
                       execution_date
                FROM payment
                WHERE id = ?
                """;

        System.out.println("DB QUERY: find payment by id = " + id);

        try (
                Connection connection = DriverManager.getConnection(
                        TestConfig.getDbUrl(),
                        TestConfig.getDbUser(),
                        TestConfig.getDbPassword()
                );

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            System.out.println(
                    "DB CONNECTED: " +
                            connection.getMetaData().getURL()
            );

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next()) {
                    System.out.println("DB RESULT: payment not found");
                    return Optional.empty();
                }

                System.out.println(
                        "DB RESULT: payment found, id = " +
                                resultSet.getLong("id")
                );

                PaymentDb payment = new PaymentDb(
                        resultSet.getLong("id"),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getString("currency"),
                        resultSet.getString("recipient"),
                        resultSet.getString("inn"),
                        resultSet.getString("purpose"),
                        resultSet.getString("status"),
                        resultSet.getString("description"),
                        resultSet.getObject("created_at", java.time.LocalDate.class),
                        resultSet.getObject("execution_date", java.time.LocalDate.class)
                );

                return Optional.of(payment);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find payment by id: " + id,
                    e
            );
        }
    }
}