package quickshow.dbms.project.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import quickshow.dbms.project.model.PaymentStatus;

@Getter
@AllArgsConstructor
public class PaymentResult {

    private String transactionId;

    private PaymentStatus paymentStatus;
}