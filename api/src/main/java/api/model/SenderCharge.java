package api.model;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class SenderCharge {
    private final BigDecimal amount;
    private final Currency currency;
}
