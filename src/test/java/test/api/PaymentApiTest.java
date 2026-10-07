package test.api;

import client.PaymentApiClient;
import config.ApiConfig;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import model.*;
import builder.PaymentBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import step.PaymentSteps;
import support.PaymentCleanup;


import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Payments")
@Feature("API")
public class PaymentApiTest {

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
    void shouldCreatePayment() {

        PaymentRequest request = PaymentBuilder.aPayment().build();

        Response response = paymentApi.createPayment(request);

        assertThat(response.statusCode()).isEqualTo(200);

        PaymentResponse payment = response.as(PaymentResponse.class);

        Long paymentId = payment.getId();
        cleanup.register(paymentId);

        assertThat(payment.getId()).isNotNull();

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
    void shouldNotCreatePaymentWithNegativeAmount() {

        PaymentRequest request = PaymentBuilder.aPayment()
                .withAmount(new BigDecimal(-100))
                .build();

        Response response = paymentApi.createPayment(request);

        assertThat(response.statusCode()).isEqualTo(400);

        ValidationErrorResponse error = response.as(ValidationErrorResponse.class);

        assertThat(error.getStatus()).isEqualTo(400);

        assertThat(error.getMessage()).isEqualTo("Ошибка валидации");

        assertThat(error.getErrors()).containsKey("amount");
    }


    @Test
    void shouldGetPaymentById() {

        PaymentResponse createdPayment = paymentSteps.createValidPayment();

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        Response getResponse = paymentApi.getPayment(paymentId);

        assertThat(getResponse.statusCode())
                .isEqualTo(200);

        PaymentResponse receivedPayment = getResponse.as(PaymentResponse.class);

        assertThat(receivedPayment.getId())
                .isEqualTo(createdPayment.getId());

        assertThat(receivedPayment.getAmount())
                .isEqualByComparingTo(createdPayment.getAmount());

        assertThat(receivedPayment.getCurrency())
                .isEqualTo(createdPayment.getCurrency());

        assertThat(receivedPayment.getRecipient())
                .isEqualTo(createdPayment.getRecipient());

        assertThat(receivedPayment.getStatus())
                .isEqualTo("created");
    }


    @Test
    void shouldReturn404WhenPaymentNotFound() {

        PaymentRequest request = PaymentBuilder.aPayment().build();

        Response createResponse = paymentApi.createPayment(request);

        assertThat(createResponse.statusCode())
                .isEqualTo(200);

        PaymentResponse createdPayment =
                createResponse.as(PaymentResponse.class);

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        Response deleteResponse =
                paymentApi.deletePayment(paymentId);

        assertThat(deleteResponse.statusCode())
                .isEqualTo(200);

        cleanup.unregister();

        Response getResponse =
                paymentApi.getPayment(paymentId);

        assertThat(getResponse.statusCode())
                .isEqualTo(404);

        NotFoundErrorResponse error =
                getResponse.as(NotFoundErrorResponse.class);

        assertThat(error.getStatus())
                .isEqualTo(404);

        assertThat(error.getError())
                .isEqualTo("Not Found");

        assertThat(error.getMessage())
                .isEqualTo("Платеж с id = " + paymentId + " не найден");

        assertThat(error.getPath())
                .isEqualTo("/api/payments/" + paymentId);

        assertThat(error.getTimestamp())
                .isNotNull();
    }


    @Test
    void shouldUpdatePayment(){
        PaymentResponse createdPayment =
                paymentSteps.createValidPayment();

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        PaymentRequest updateRequest =
                PaymentBuilder.aPayment()
                        .withAmount(new BigDecimal("3500.00"))
                        .withCurrency("USD")
                        .withRecipient("Updated Recipient")
                        .withInn("9876543210")
                        .withPurpose("Updated purpose")
                        .withDescription("Updated description")
                        .build();

        Response updateResponse =
                paymentApi.updatePayment(paymentId, updateRequest);

        assertThat(updateResponse.statusCode())
                .isEqualTo(200);

        PaymentResponse updatedPayment =
                updateResponse.as(PaymentResponse.class);

        assertThat(updatedPayment.getId())
                .isEqualTo(paymentId);

        assertThat(updatedPayment.getAmount())
                .isEqualByComparingTo(updateRequest.getAmount());

        assertThat(updatedPayment.getCurrency())
                .isEqualTo(updateRequest.getCurrency());

        assertThat(updatedPayment.getRecipient())
                .isEqualTo(updateRequest.getRecipient());

        assertThat(updatedPayment.getInn())
                .isEqualTo(updateRequest.getInn());

        assertThat(updatedPayment.getPurpose())
                .isEqualTo(updateRequest.getPurpose());

        assertThat(updatedPayment.getDescription())
                .isEqualTo(updateRequest.getDescription());

        assertThat(updatedPayment.getStatus())
                .isEqualTo("created");

        Response getResponse =
                paymentApi.getPayment(paymentId);

        assertThat(getResponse.statusCode())
                .isEqualTo(200);

        PaymentResponse savedPayment =
                getResponse.as(PaymentResponse.class);

        assertThat(savedPayment.getAmount())
                .isEqualByComparingTo(updateRequest.getAmount());

        assertThat(savedPayment.getCurrency())
                .isEqualTo(updateRequest.getCurrency());

        assertThat(savedPayment.getRecipient())
                .isEqualTo(updateRequest.getRecipient());

        assertThat(savedPayment.getInn())
                .isEqualTo(updateRequest.getInn());

        assertThat(savedPayment.getPurpose())
                .isEqualTo(updateRequest.getPurpose());

        assertThat(savedPayment.getDescription())
                .isEqualTo(updateRequest.getDescription());

        assertThat(savedPayment.getStatus())
                .isEqualTo("created");
    }


    @Test
    void shouldNotUpdatePaymentWithNegativeAmount() {

        PaymentRequest createRequest =
                PaymentBuilder.aPayment().build();

        Response createResponse =
                paymentApi.createPayment(createRequest);

        assertThat(createResponse.statusCode())
                .isEqualTo(200);

        PaymentResponse createdPayment =
                createResponse.as(PaymentResponse.class);

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        PaymentRequest invalidUpdateRequest =
                PaymentBuilder.aPayment()
                        .withAmount(new BigDecimal("-100.00"))
                        .build();

        Response updateResponse =
                paymentApi.updatePayment(
                        paymentId,
                        invalidUpdateRequest
                );

        assertThat(updateResponse.statusCode())
                .isEqualTo(400);

        ValidationErrorResponse error =
                updateResponse.as(ValidationErrorResponse.class);

        assertThat(error.getStatus())
                .isEqualTo(400);

        assertThat(error.getMessage())
                .isEqualTo("Ошибка валидации");

        assertThat(error.getErrors())
                .containsKey("amount");


        Response getResponse =
                paymentApi.getPayment(paymentId);

        assertThat(getResponse.statusCode())
                .isEqualTo(200);

        PaymentResponse paymentAfterFailedUpdate =
                getResponse.as(PaymentResponse.class);

        assertThat(paymentAfterFailedUpdate.getAmount())
                .isEqualByComparingTo(createRequest.getAmount());

        assertThat(paymentAfterFailedUpdate.getCurrency())
                .isEqualTo(createRequest.getCurrency());

        assertThat(paymentAfterFailedUpdate.getRecipient())
                .isEqualTo(createRequest.getRecipient());

        assertThat(paymentAfterFailedUpdate.getInn())
                .isEqualTo(createRequest.getInn());

        assertThat(paymentAfterFailedUpdate.getPurpose())
                .isEqualTo(createRequest.getPurpose());

        assertThat(paymentAfterFailedUpdate.getDescription())
                .isEqualTo(createRequest.getDescription());

        assertThat(paymentAfterFailedUpdate.getStatus())
                .isEqualTo("created");
    }


    @Test
    void shouldExecutePaymentAndReturn409WhenExecutedAgain() {

        PaymentResponse createdPayment =
                paymentSteps.createValidPayment();

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        Response executeResponse =
                paymentApi.executePayment(paymentId);

        assertThat(executeResponse.statusCode())
                .isEqualTo(200);

        PaymentResponse executedPayment =
                executeResponse.as(PaymentResponse.class);

        assertThat(executedPayment.getId())
                .isEqualTo(paymentId);

        assertThat(executedPayment.getStatus())
                .isEqualTo("executed");

        assertThat(executedPayment.getExecutionDate())
                .isNotNull();


        Response secondExecuteResponse =
                paymentApi.executePayment(paymentId);

        assertThat(secondExecuteResponse.statusCode())
                .isEqualTo(409);

        ConflictErrorResponse error =
                secondExecuteResponse.as(ConflictErrorResponse.class);

        assertThat(error.getStatus())
                .isEqualTo(409);

        assertThat(error.getMessage())
                .isEqualTo("Платёж уже выполнен");
    }


    @Test
    void shouldDeletePayment() {

        PaymentResponse createdPayment =
                paymentSteps.createValidPayment();

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        Response deleteResponse =
                paymentApi.deletePayment(paymentId);

        assertThat(deleteResponse.statusCode())
                .isEqualTo(200);

        cleanup.unregister();

        Response getResponse =
                paymentApi.getPayment(paymentId);

        assertThat(getResponse.statusCode())
                .isEqualTo(404);

        NotFoundErrorResponse error =
                getResponse.as(NotFoundErrorResponse.class);

        assertThat(error.getStatus())
                .isEqualTo(404);

        assertThat(error.getMessage())
                .isEqualTo("Платеж с id = " + paymentId + " не найден");
    }


    @Test
    void shouldFilterPayments() {

        PaymentRequest request =
                PaymentBuilder.aPayment()
                        .withCurrency("USD")
                        .withRecipient("Filter Test Company")
                        .build();

        Response createResponse =
                paymentApi.createPayment(request);

        assertThat(createResponse.statusCode())
                .isEqualTo(200);

        PaymentResponse createdPayment =
                createResponse.as(PaymentResponse.class);

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);


        Response response = paymentApi.getPayments(
                "created",
                "USD",
                "Filter Test",
                0,
                10
        );

        assertThat(response.statusCode())
                .isEqualTo(200);

        PaymentPageResponse page =
                response.as(PaymentPageResponse.class);

        assertThat(page.getNumber())
                .isEqualTo(0);

        assertThat(page.getSize())
                .isEqualTo(10);

        assertThat(page.getContent())
                .isNotEmpty();

        assertThat(page.getContent())
                .anyMatch(payment ->
                        payment.getId().equals(paymentId)
                );
    }
}
