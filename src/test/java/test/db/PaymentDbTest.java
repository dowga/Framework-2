package test.db;

import builder.PaymentBuilder;
import client.PaymentApiClient;
import config.ApiConfig;
import db.model.PaymentDb;
import db.repository.PaymentDbRepository;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import model.PaymentRequest;
import model.PaymentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import step.PaymentSteps;
import support.PaymentCleanup;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Payments")
@Feature("Database")
public class PaymentDbTest {

    private final PaymentApiClient paymentApi =
            new PaymentApiClient();

    private final PaymentDbRepository paymentDb =
            new PaymentDbRepository();

    private final PaymentSteps paymentSteps =
            new PaymentSteps();

    private final PaymentCleanup cleanup =
            new PaymentCleanup();

    @BeforeAll
    static void setUp() {
        ApiConfig.configure();
    }

    @AfterEach
    void cleanUp() {
        cleanup.clean();
    }

    @Test
    void shouldSavePaymentToDatabase() {

        PaymentRequest request =
                PaymentBuilder.aPayment()
                        .withAmount(new BigDecimal("7777.00"))
                        .withCurrency("EUR")
                        .withRecipient("DB Test Company")
                        .build();

        Response response =
                paymentApi.createPayment(request);

        assertThat(response.statusCode())
                .isEqualTo(200);

        PaymentResponse createdPayment =
                response.as(PaymentResponse.class);

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);


        PaymentDb payment =
                paymentDb.findById(paymentId)
                        .orElseThrow();


        assertThat(payment.getId())
                .isEqualTo(paymentId);

        assertThat(payment.getAmount())
                .isEqualByComparingTo(request.getAmount());

        assertThat(payment.getCurrency())
                .isEqualTo(request.getCurrency());

        assertThat(payment.getRecipient())
                .isEqualTo(request.getRecipient());

        assertThat(payment.getInn())
                .isEqualTo(request.getInn());

        assertThat(payment.getPurpose())
                .isEqualTo(request.getPurpose());

        assertThat(payment.getDescription())
                .isEqualTo(request.getDescription());

        assertThat(payment.getStatus())
                .isEqualTo("created");

        assertThat(payment.getCreatedAt())
                .isNotNull();

        assertThat(payment.getExecutionDate())
                .isNull();
    }


    @Test
    void shouldUpdatePaymentStatusInDatabaseAfterExecution() {

        PaymentResponse createdPayment =
                paymentSteps.createValidPayment();

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);


        Response executeResponse =
                paymentApi.executePayment(paymentId);

        assertThat(executeResponse.statusCode())
                .isEqualTo(200);


        PaymentDb paymentFromDb =
                paymentDb.findById(paymentId)
                        .orElseThrow();


        assertThat(paymentFromDb.getStatus())
                .isEqualTo("executed");

        assertThat(paymentFromDb.getExecutionDate())
                .isNotNull();
    }
}
