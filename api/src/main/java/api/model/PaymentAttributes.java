package api.model;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@AllArgsConstructor
public class PaymentAttributes {
    private final BigDecimal amount;
    private final ChargesInformation chargesInformation;
    private final Currency currency;
    private final LocalDate entToEndReference;
    private final DebtorParty debtorParty;
}
