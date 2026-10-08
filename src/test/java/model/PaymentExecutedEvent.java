package model;

public record PaymentExecutedEvent(
        Long paymentId,
        String status
) {
}
