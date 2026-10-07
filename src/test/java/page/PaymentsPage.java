package page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class PaymentsPage {

    private final SelenideElement pageTitle = $("h1");

    @Step("Открыть страницу платежей")
    public PaymentsPage openPage() {
        open("/");
        return this;
    }

    @Step("Проверить открывается ли сайт")
    public PaymentsPage shouldBeOpened() {
        pageTitle
                .shouldBe(visible)
                .shouldHave(exactText("Управление платежами"));

        return this;
    }

    private SelenideElement paymentRow(Long paymentId) {
            return $x("//tbody/tr[td[1][normalize-space()='" + paymentId + "']]");
    }


    @Step("Проверить наличие платежа {paymentId}")
    public PaymentsPage shouldHavePayment(
            Long paymentId,
            String recipient,
            String currency)
    {

        SelenideElement row = paymentRow(paymentId);

        row.shouldBe(visible);

        row.$("td:nth-child(3)")
                .shouldHave(exactText(currency));

        row.$("td:nth-child(4)")
                .shouldHave(exactText(recipient));

        row.$("td:nth-child(7)")
                .shouldHave(exactText("created"));

        return this;
    }

    @Step("Проверить статус платежа {paymentId}: {expectedStatus}")
    public PaymentsPage shouldHaveStatus(
            Long paymentId,
            String expectedStatus) {

        paymentRow(paymentId)
                .$("td:nth-child(7)")
                .shouldHave(exactText(expectedStatus));

        return this;
    }

    @Step("Выполнить платеж {paymentId} через UI")
    public PaymentsPage executePayment(Long paymentId) {

        SelenideElement row = paymentRow(paymentId);

        row.$x(".//button[normalize-space()='Выполнить']")
                .click();

        confirm();

        return this;
    }


}