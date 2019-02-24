package api.model;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class Payment {
    private final String type;
    private final UUID id;
    private final Integer version;
    private final UUID organisationId;
    private final PaymentAttributes attributes;
}
