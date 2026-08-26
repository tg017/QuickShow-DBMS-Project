package quickshow.dbms.project.payment;

import quickshow.dbms.project.model.PaymentMethod;

public interface PaymentGateway {

    PaymentResult processPayment(
            Long amount,
            PaymentMethod paymentMethod
    );
}