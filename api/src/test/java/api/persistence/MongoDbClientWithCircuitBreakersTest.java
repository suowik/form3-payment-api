package api.persistence;

import api.model.Payment;
import io.reactivex.Single;
import io.vavr.control.Either;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.reactivex.circuitbreaker.CircuitBreaker;
import io.vertx.reactivex.core.Vertx;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

//I'm testing only one method on purpose
//the rest would be similar
public class MongoDbClientWithCircuitBreakersTest {

    @Test
    public void getOne() {
        DbClient fakeDbClient = new DbClient() {
            @Override
            public Single<Either<Throwable, String>> create(Payment payment) {
                throw new RuntimeException();
            }

            //methods below are not tested
            @Override
            public Single<Either<Throwable, Boolean>> delete(String paymentId) {
                return null;
            }

            @Override
            public Single<Either<Throwable, Payment>> getOne(String paymentId) {
                return null;
            }

            @Override
            public Single<Either<Throwable, Payment>> update(UpdateRequest updateRequest) {
                return null;
            }

            @Override
            public Single<Either<Throwable, List<Payment>>> search(SearchCriteria searchCriteria) {
                return null;
            }
        };

        CircuitBreaker circuitBreaker = spy(CircuitBreaker.create("fake", Vertx.vertx(), new CircuitBreakerOptions().setMaxFailures(5).setFallbackOnFailure(true)));
        var client = new MongoDbClientWithCircuitBreakers(fakeDbClient, circuitBreaker);
        client.create(new Payment("", "", 0, "", null)).blockingGet();
        client.create(new Payment("", "", 0, "", null)).blockingGet();
        client.create(new Payment("", "", 0, "", null)).blockingGet();
        client.create(new Payment("", "", 0, "", null)).blockingGet();
        client.create(new Payment("", "", 0, "", null)).blockingGet();
        verify(circuitBreaker, times(5)).rxExecuteCommandWithFallback(any(), any());
    }
}