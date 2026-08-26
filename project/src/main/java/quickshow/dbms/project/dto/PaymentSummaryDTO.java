package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentSummaryDTO {

    private Integer paymentId;
    private String paymentMethod;
    private Long paymentAmount;
    private String paymentStatus;
}