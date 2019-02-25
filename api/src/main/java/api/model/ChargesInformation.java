package api.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@AllArgsConstructor
@Value
public class ChargesInformation {
    private final String bearerCode;
    private final List<SenderCharge> senderCharges;
}
