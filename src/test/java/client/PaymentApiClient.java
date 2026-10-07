package client;

import io.restassured.response.Response;
import model.PaymentRequest;

import static io.restassured.RestAssured.given;

public class PaymentApiClient {

    public Response createPayment(PaymentRequest request) {
        return given()
                .log().all()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/payments")
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response deletePayment(Long id) {
        return given()
                .log().all()
                .when()
                .delete("/api/payments/" + id)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response getPayment(Long id) {
        return given()
                .log().all()
                .when()
                .get("/api/payments/" + id)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response updatePayment(Long id, PaymentRequest request) {
        return given()
                .log().all()
                .contentType("application/json")
                .body(request)
                .when()
                .put("/api/payments/" + id)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response executePayment(Long id) {
        return given()
                .log().all()
                .when()
                .post("/api/payments/" + id + "/execute")
                .then()
                .log().all()
                .extract()
                .response();
    }


    public Response getPayments(
            String status,
            String currency,
            String recipient,
            int page,
            int size) {

        return given()
                .log().all()
                .queryParam("status", status)
                .queryParam("currency", currency)
                .queryParam("recipient", recipient)
                .queryParam("page", page)
                .queryParam("size", size)
                .when()
                .get("/api/payments")
                .then()
                .log().all()
                .extract()
                .response();
    }
}
