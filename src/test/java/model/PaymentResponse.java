package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentResponse {

    private Long id;
    private BigDecimal amount;
    private String currency;
    private String recipient;
    private String inn;
    private String purpose;
    private String status;
    private String description;
    private LocalDate createdAt;
    private LocalDate executionDate;

    public PaymentResponse() {}

    public Long getId() {
        return id;
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

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }
}
