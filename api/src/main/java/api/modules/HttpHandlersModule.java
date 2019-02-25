package api.modules;

import api.http.*;
import api.persistence.DbClient;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class HttpHandlersModule {

    public static List<Triple<HttpMethod, String, Handler<RoutingContext>>> create(DbClient dbClient) {
        return List.of(
                Triple.of(HttpMethod.GET, "/payments", new FindPaymentsHandler()),
                Triple.of(HttpMethod.POST, "/payments", new CreatePaymentHandler(dbClient::create)),
                Triple.of(HttpMethod.DELETE, "/payments/:paymentId", new DeletePaymentHandler()),
                Triple.of(HttpMethod.PATCH, "/payments/:paymentId", new UpdatePaymentHandler()),
                Triple.of(HttpMethod.GET, "/payments/:paymentId", new GetPaymentHandler())
        );
    }


}
