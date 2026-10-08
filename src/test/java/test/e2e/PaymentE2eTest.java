package test.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiConfig;
import config.UiConfig;
import db.model.PaymentDb;
import db.repository.PaymentDbRepository;
import io.qameta.allure.*;
import kafka.PaymentKafkaConsumer;
import model.PaymentExecutedEvent;
import model.PaymentResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import page.PaymentsPage;
import step.PaymentSteps;
import support.PaymentCleanup;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Payments")
@Feature("E2E")
public class PaymentE2eTest {

    private final PaymentSteps paymentSteps = new PaymentSteps();
    private final PaymentsPage paymentsPage = new PaymentsPage();
    private final PaymentDbRepository paymentDb = new PaymentDbRepository();
    private final PaymentCleanup cleanup = new PaymentCleanup();

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        ApiConfig.configure();
        UiConfig.configure();
    }

    @AfterEach
    void cleanUp() {
        cleanup.clean();
    }

    @Test
    @DisplayName("Выполнение платежа через UI обновляет БД и публикует Kafka-событие")
    void shouldExecutePaymentThroughUiAndPublishKafkaEvent() throws Exception {

        PaymentResponse payment =
                paymentSteps.createValidPayment();

        Long paymentId = payment.getId();
        cleanup.register(paymentId);

        try (PaymentKafkaConsumer kafka = new PaymentKafkaConsumer()) {

            kafka.startListening();

            paymentsPage
                    .openPage()
                    .shouldHaveStatus(paymentId, "created")
                    .executePayment(paymentId)
                    .shouldHaveStatus(paymentId, "executed");

            ConsumerRecord<String, String> record =
                    kafka.waitForPaymentId(
                            paymentId,
                            Duration.ofSeconds(10)
                    );

            PaymentExecutedEvent event =
                    mapper.readValue(
                            record.value(),
                            PaymentExecutedEvent.class
                    );

            Allure.step("Проверить kafka-событие", () -> {

                assertThat(record.key())
                        .isEqualTo(paymentId.toString());

                assertThat(event.status())
                        .isEqualTo("executed");
            });

            PaymentDb paymentFromDb =
                    paymentDb.findById(paymentId)
                            .orElseThrow();

            Allure.step("Проверить состояние платежа в БД", () -> {

                assertThat(paymentFromDb.getStatus())
                        .isEqualTo("executed");

                assertThat(paymentFromDb.getExecutionDate())
                        .isNotNull();
            });
        }
    }
}
