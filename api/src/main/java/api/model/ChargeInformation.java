package api.model;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ChargeInformation {
    private final String bearerCode;
    private final List<SenderCharge> senderCharges;
}
