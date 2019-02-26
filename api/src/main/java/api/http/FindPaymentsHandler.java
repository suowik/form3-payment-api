package api.http;

import io.vertx.core.Handler;
import io.vertx.reactivex.ext.web.RoutingContext;

public class FindPaymentsHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext event) {
        event.response().setStatusCode(501).end();
    }
}
