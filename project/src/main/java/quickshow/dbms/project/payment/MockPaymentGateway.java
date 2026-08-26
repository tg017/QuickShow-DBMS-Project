package quickshow.dbms.project.payment;

import org.springframework.stereotype.Component;
import quickshow.dbms.project.model.PaymentMethod;
import quickshow.dbms.project.model.PaymentStatus;

import java.util.UUID;

@Component
public class MockPaymentGateway
        implements PaymentGateway {

    @Override
    public PaymentResult processPayment(
            Long amount,
            PaymentMethod paymentMethod
    ) {

        String transactionId =
                "MOCK-" + UUID.randomUUID();

        return new PaymentResult(
                transactionId,
                PaymentStatus.SUCCESS
        );
    }
}