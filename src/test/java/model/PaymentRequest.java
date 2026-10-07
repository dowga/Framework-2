package model;

import java.math.BigDecimal;

public class PaymentRequest {

    private BigDecimal amount;
    private String currency;
    private String recipient;
    private String inn;
    private String purpose;
    private String description;

    public PaymentRequest(
            BigDecimal amount,
            String currency,
            String recipient,
            String inn,
            String purpose,
            String description
    ) {
        this.amount = amount;
        this.currency = currency;
        this.recipient = recipient;
        this.inn = inn;
        this.purpose = purpose;
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getInn() {
        return inn;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDescription() {
        return description;
    }
}
