package kafka;

import config.TestConfig;
import io.qameta.allure.Step;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class PaymentKafkaConsumer implements AutoCloseable {

    private static final String TOPIC = "payment-events";

    private final KafkaConsumer<String, String> consumer;

    public PaymentKafkaConsumer() {

        Properties properties = new Properties();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                TestConfig.getKafkaBootstrapServers()
        );

        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "qa-payment-" + UUID.randomUUID()
        );

        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "latest"
        );

        properties.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                "false"
        );

        consumer = new KafkaConsumer<>(properties);
    }

    @Step("Подписаться на Kafka topic с текущего конца")
    public void startListening() {

        consumer.subscribe(List.of(TOPIC));

        long deadline =
                System.nanoTime() + Duration.ofSeconds(15).toNanos();

        while (consumer.assignment().isEmpty()
                && System.nanoTime() < deadline) {

            consumer.poll(Duration.ofMillis(200));
        }

        if (consumer.assignment().isEmpty()) {
            throw new IllegalStateException(
                    "Не удалось получить partitions Kafka topic: " + TOPIC
            );
        }

        consumer.seekToEnd(consumer.assignment());

        for (TopicPartition partition : consumer.assignment()) {
            consumer.position(partition);
        }
    }

    @Step("Дождаться Kafka-события для платежа {paymentId}")
    public ConsumerRecord<String, String> waitForPaymentId(
            Long paymentId,
            Duration timeout) {

        long deadline = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {

            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> record : records) {

                if (paymentId.toString().equals(record.key())) {
                    return record;
                }
            }
        }

        throw new AssertionError(
                "Не получили Kafka-событие для paymentId=" + paymentId
        );
    }

    @Override
    public void close() {
        consumer.close();
    }

    @Step("Проверить отсутствие Kafka-события для платежа {paymentId}")
    public void assertNoEventForPaymentId(
            Long paymentId,
            Duration timeout) {

        long deadline =
                System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {

            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, String> record : records) {

                if (paymentId.toString().equals(record.key())) {
                    throw new AssertionError(
                            "Обнаружено неожиданное Kafka-событие "
                                    + "для paymentId=" + paymentId
                                    + ", offset=" + record.offset()
                                    + ", partition=" + record.partition()
                    );
                }
            }
        }
    }
}