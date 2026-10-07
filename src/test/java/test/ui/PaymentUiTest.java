package test.ui;

import config.ApiConfig;
import config.UiConfig;
import db.model.PaymentDb;
import db.repository.PaymentDbRepository;
import io.qameta.allure.Allure;
import model.PaymentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import page.PaymentsPage;
import step.PaymentSteps;
import support.PaymentCleanup;
import org.junit.jupiter.api.DisplayName;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Payments")
@Feature("UI")
public class PaymentUiTest {

    private final PaymentsPage paymentsPage = new PaymentsPage();
    private final PaymentSteps paymentSteps = new PaymentSteps();
    private final PaymentCleanup cleanup = new PaymentCleanup();
    private final PaymentDbRepository paymentDb = new PaymentDbRepository();

    @BeforeAll
    static void setUp() {
        ApiConfig.configure();
        UiConfig.configure();
    }

    @AfterEach
    void cleanUp() {
        cleanup.clean();
    }

    @Test
    void shouldOpenPaymentsPage() {

        paymentsPage
                .openPage()
                .shouldBeOpened();
    }

    @Test
    void shouldDisplayCreatedPayment() {

        PaymentResponse createdPayment =
                paymentSteps.createValidPayment();

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        paymentsPage
                .openPage()
                .shouldHavePayment(
                        createdPayment.getId(),
                        createdPayment.getRecipient(),
                        createdPayment.getCurrency()
                );
    }

    @Test
    void shouldExecutePaymentFromUi() {

        PaymentResponse createdPayment =
                paymentSteps.createValidPayment();

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        paymentsPage
                .openPage()
                .shouldHaveStatus(paymentId, "created")
                .executePayment(paymentId)
                .shouldHaveStatus(paymentId, "executed");
    }

    @Test
    @Story("Выполнение платежа")
    @DisplayName("Выполнение платежа через UI сохраняет статус в БД")
    void shouldExecutePaymentFromUiAndSaveStatusToDatabase() {

        PaymentResponse createdPayment =
                paymentSteps.createValidPayment();

        Long paymentId = createdPayment.getId();
        cleanup.register(paymentId);

        paymentsPage
                .openPage()
                .shouldHaveStatus(paymentId, "created")
                .executePayment(paymentId)
                .shouldHaveStatus(paymentId, "executed");

        PaymentDb paymentFromDb =
                paymentDb.findById(paymentId)
                        .orElseThrow();

        Allure.step("Проверить состояние платежа в БД", () -> {
            assertThat(paymentFromDb.getStatus())
                    .isEqualTo("executed");

            assertThat(paymentFromDb.getExecutionDate())
                    .isNotNull();
        });
    }
}
