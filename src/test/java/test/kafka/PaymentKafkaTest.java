package test.kafka;

import client.PaymentApiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiConfig;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import kafka.PaymentKafkaConsumer;
import model.PaymentExecutedEvent;
import model.PaymentResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import step.PaymentSteps;
import support.PaymentCleanup;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Payments")
@Feature("Kafka")
public class PaymentKafkaTest {

    private final PaymentApiClient paymentApi = new PaymentApiClient();
    private final PaymentSteps paymentSteps = new PaymentSteps();
    private final PaymentCleanup cleanup = new PaymentCleanup();

    @BeforeAll
    static void setUp() {
        ApiConfig.configure();
    }

    @AfterEach
    void cleanUp() {
        cleanup.clean();
    }

    @Test
    @DisplayName("Выполнение платежа публикует событие в Kafka")
    void shouldPublishPaymentExecutedEvent()
            throws JsonProcessingException {

        // ARRANGE
        PaymentResponse payment =
                paymentSteps.createValidPayment();

        Long paymentId = payment.getId();
        cleanup.register(paymentId);

        try (PaymentKafkaConsumer kafka = new PaymentKafkaConsumer()) {

            kafka.startListening();

            // ACT
            Response response =
                    paymentApi.executePayment(paymentId);

            assertThat(response.statusCode())
                    .isEqualTo(200);

            // READ
            ConsumerRecord<String, String> record =
                    kafka.waitForPaymentId(
                            paymentId,
                            Duration.ofSeconds(10)
                    );

            PaymentExecutedEvent event =
                    new ObjectMapper().readValue(
                            record.value(),
                            PaymentExecutedEvent.class
                    );

            // ASSERT
            assertThat(record.key())
                    .isEqualTo(paymentId.toString());

            assertThat(event.paymentId())
                    .isEqualTo(paymentId);

            assertThat(event.status())
                    .isEqualTo("executed");
        }
    }


    @Test
    @DisplayName("Повторное выполнение платежа не публикует Kafka-событие")
    void shouldNotPublishEventWhenPaymentAlreadyExecuted() {

        // ARRANGE
        PaymentResponse payment =
                paymentSteps.createValidPayment();

        Long paymentId = payment.getId();
        cleanup.register(paymentId);

        try (PaymentKafkaConsumer kafka = new PaymentKafkaConsumer()) {

            kafka.startListening();

            // Первое выполнение — успешно
            Response firstResponse =
                    paymentApi.executePayment(paymentId);

            assertThat(firstResponse.statusCode())
                    .isEqualTo(200);

            // Убеждаемся, что первое событие действительно пришло
            kafka.waitForPaymentId(
                    paymentId,
                    Duration.ofSeconds(10)
            );

            // ACT — пытаемся выполнить платёж повторно
            Response secondResponse =
                    paymentApi.executePayment(paymentId);

            // ASSERT — API должен вернуть конфликт
            assertThat(secondResponse.statusCode())
                    .isEqualTo(409);

            // ASSERT — нового Kafka-события быть не должно
            kafka.assertNoEventForPaymentId(
                    paymentId,
                    Duration.ofSeconds(3)
            );
        }
    }

}
