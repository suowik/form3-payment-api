package api.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@AllArgsConstructor
@Value
public class SenderCharge {
    private final BigDecimal amount;
    private final Currency currency;
}
