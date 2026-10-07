package step;

import builder.PaymentBuilder;
import client.PaymentApiClient;
import io.restassured.response.Response;
import model.PaymentRequest;
import model.PaymentResponse;
import io.qameta.allure.Step;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentSteps {

    private final PaymentApiClient paymentApi = new PaymentApiClient();

    @Step("Создать валидный платеж через API")
    public PaymentResponse createValidPayment() {

        PaymentRequest request =
                PaymentBuilder.aPayment().build();

        Response response =
                paymentApi.createPayment(request);

        assertThat(response.statusCode())
                .isEqualTo(200);

        return response.as(PaymentResponse.class);
    }
}
