package quickshow.dbms.project.repository.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentData {

    private Integer paymentId;
    private String paymentMethod;
    private Long paymentAmount;
    private String paymentStatus;
}