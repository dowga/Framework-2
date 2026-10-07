package support;

import client.PaymentApiClient;

public class PaymentCleanup {

    private final PaymentApiClient paymentApi =
            new PaymentApiClient();

    private Long paymentId;

    public void register(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void unregister() {
        paymentId = null;
    }

    public void clean() {
        if (paymentId != null) {
            paymentApi.deletePayment(paymentId);
            paymentId = null;
        }
    }
}
