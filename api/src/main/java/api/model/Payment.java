package api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class Payment {
    private final String type;
    @JsonAlias({"id", "_id"})
    private final String id;
    private final Integer version;
    private final String organisationId;
    private final PaymentAttributes attributes;
}
