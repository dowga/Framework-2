package builder;

import model.PaymentRequest;

import java.math.BigDecimal;

public class PaymentBuilder {

    private BigDecimal amount = new BigDecimal("2000.00");
    private String currency = "RUB";
    private String recipient = "ООО Тесты";
    private String inn = "1234512345";
    private String purpose = "Тестовый платеж";
    private String description = "Тест";

    public static PaymentBuilder aPayment() {
        return new PaymentBuilder();
    }

    public PaymentBuilder withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public PaymentBuilder withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public PaymentBuilder withRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public PaymentBuilder withInn(String inn) {
        this.inn = inn;
        return this;
    }

    public PaymentBuilder withPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public PaymentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public PaymentRequest build() {
        return new PaymentRequest(
                amount,
                currency,
                recipient,
                inn,
                purpose,
                description
        );
    }
}
