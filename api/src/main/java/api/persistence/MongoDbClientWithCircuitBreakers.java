package api.persistence;

import api.model.Payment;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vavr.control.Either;
import io.vertx.reactivex.circuitbreaker.CircuitBreaker;

import java.util.List;
import java.util.function.Function;

public class MongoDbClientWithCircuitBreakers implements DbClient {

    private final DbClient dbClient;

    private final CircuitBreaker dbCircuitBreaker;

    public MongoDbClientWithCircuitBreakers(DbClient dbClient, CircuitBreaker circuitBreaker) {
        this.dbClient = dbClient;
        dbCircuitBreaker = circuitBreaker;
    }

    @Override
    public Single<Either<Throwable, String>> create(Payment payment) {
        return invokeWithCircuitBreaker(payment, dbClient::create);
    }

    @Override
    public Single<Either<Throwable, Boolean>> delete(String paymentId) {
        return invokeWithCircuitBreaker(paymentId, dbClient::delete);
    }

    @Override
    public Single<Either<Throwable, Payment>> getOne(String paymentId) {
        return invokeWithCircuitBreaker(paymentId, dbClient::getOne);
    }

    @Override
    public Single<Either<Throwable, Payment>> update(UpdateRequest updateRequest) {
        return invokeWithCircuitBreaker(updateRequest, dbClient::update);
    }

    @Override
    public Single<Either<Throwable, List<Payment>>> search(SearchCriteria searchCriteria) {
        return invokeWithCircuitBreaker(searchCriteria, dbClient::search);
    }

    private <I, R> Single<Either<Throwable, R>> invokeWithCircuitBreaker(I input, Function<I, Single<Either<Throwable, R>>> mapper) {
        return dbCircuitBreaker
                .rxExecuteCommandWithFallback(event -> {
                            var res = mapper.apply(input)
                                    .onErrorReturn(Either::left)
                                    .observeOn(Schedulers.computation())
                                    .blockingGet();
                            event.complete(res);
                        },
                        Either::left);
    }
}
