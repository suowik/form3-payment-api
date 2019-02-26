package api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String type;
    @JsonAlias({"id", "_id"})
    private String id;
    private Integer version;
    private String organisationId;
    private PaymentAttributes attributes;
}
