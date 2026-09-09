package quickshow.dbms.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPaymentDTO {

    private Integer paymentId;

    private String paymentMethod;

    private Long paymentAmount;

    private String transactionId;

    private LocalDateTime paymentDateTime;

    private String paymentStatus;
}