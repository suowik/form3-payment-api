package api.http;

import api.model.Payment;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vavr.control.Either;
import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.function.Function;

public class CreatePaymentHandler implements Handler<RoutingContext> {

    private final Function<Payment, Single<Either<Throwable, String>>> delegate;

    public CreatePaymentHandler(Function<Payment, Single<Either<Throwable, String>>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handle(RoutingContext event) {
        Single.just(event)
                .observeOn(Schedulers.computation())
                .flatMap(e -> {
                    var payment = e.getBodyAsJson().mapTo(Payment.class);
                    return delegate.apply(payment);
                })
                .subscribe(s -> {

                }, e -> {

                });
    }
}
