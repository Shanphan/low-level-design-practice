package entity;

import enums.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Payment {

    String id;
    PaymentMode paymentMode;
    int amount;
}
