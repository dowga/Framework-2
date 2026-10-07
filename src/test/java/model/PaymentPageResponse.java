package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentPageResponse {

    private List<PaymentResponse> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;

    public PaymentPageResponse() {
    }

    public List<PaymentResponse> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }
}
