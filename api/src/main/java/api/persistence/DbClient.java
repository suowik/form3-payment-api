package api.persistence;

import api.model.Payment;
import io.reactivex.Single;
import io.vavr.control.Either;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

public interface DbClient {
    Single<Either<Throwable, String>> create(Payment payment);

    Single<Either<Throwable, Boolean>> delete(String paymentId);

    Single<Either<Throwable, Payment>> getOne(String paymentId);

    Single<Either<Throwable, Payment>> update(UpdateRequest updateRequest);

    Single<Either<Throwable, List<Payment>>> search(SearchCriteria searchCriteria);

    @AllArgsConstructor
    @Value
    class SearchCriteria {
        private final Integer offset;
        private final Integer limit;
    }

    @AllArgsConstructor
    @Value
    class UpdateRequest {
        private final String paymentId;
        private final JsonObject updateData;
    }

}
