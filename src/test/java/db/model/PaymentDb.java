package db.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentDb {

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

    public PaymentDb(
            Long id,
            BigDecimal amount,
            String currency,
            String recipient,
            String inn,
            String purpose,
            String status,
            String description,
            LocalDate createdAt,
            LocalDate executionDate) {

        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.recipient = recipient;
        this.inn = inn;
        this.purpose = purpose;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.executionDate = executionDate;
    }

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