package api.http;

import api.exceptions.ValidationException;
import api.model.Payment;
import api.utils.Flowables;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vavr.control.Either;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

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
                .toFlowable()
                .map(this::validateRequest)
                .flatMap(i -> Flowables.flatMap(i, e -> {
                    var payment = e.getBodyAsJson().mapTo(Payment.class);
                    return delegate.apply(payment).toFlowable();
                }))
                .map(e -> e.map(c -> new JsonObject().put("paymentId", c)))
                .subscribe(s -> CommonHttpResponseHandler.handleCreateResponse(event, s),
                        e -> {
                            e.printStackTrace();
                            event.response()
                                    .setStatusCode(500)
                                    .putHeader("content-type", "application/json")
                                    .end();
                        }
                );
    }

    private Either<Throwable, RoutingContext> validateRequest(RoutingContext ctx) {
        return Validation.combine(validateHeaders(ctx), validateBody(ctx))
                .ap((a, b) -> a)
                .toEither()
                .mapLeft(errors -> new ValidationException(errors.asJava()));
    }

    private Validation<Throwable, RoutingContext> validateHeaders(RoutingContext ctx) {
        if (StringUtils.isBlank(ctx.request().getHeader("x-request-id"))) {
            return Validation.invalid(new Exception("missing request id"));
        } else {
            return Validation.valid(ctx);
        }
    }

    private Validation<Throwable, RoutingContext> validateBody(RoutingContext ctx) {
        return Try.ofSupplier(() -> {
            ctx.getBodyAsJson();
            return Validation.<Throwable, RoutingContext>valid(ctx);
        }).getOrElseGet(Validation::invalid);
    }

}
