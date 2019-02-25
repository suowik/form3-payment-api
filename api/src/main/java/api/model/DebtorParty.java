package api.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class DebtorParty {
    private final String accountName;
    private final String accountNumber;
}
