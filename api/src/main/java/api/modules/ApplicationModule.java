package api.modules;

import io.reactivex.Single;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import lombok.AllArgsConstructor;
import lombok.Value;

public class ApplicationModule {

    public static Single<HttpServer> createApplication(Vertx vertx, EnvironmentConfig config) {
        var persistence = PersistenceModule.create(vertx, config.getPersistenceConfig());

        var httpHandlers = HttpHandlersModule.create(persistence);

        return OpenAPI3RouterFactory.rxCreate(vertx, "webroot/api.yml")
                .flatMap(factory -> {
                    var router = factory.getRouter();
                    router.route("/*").handler(StaticHandler.create());
                    var paymentSubRouter = Router.router(vertx);
                    paymentSubRouter.route().handler(BodyHandler.create());
                    httpHandlers.forEach(h -> paymentSubRouter.route(h.getLeft(), h.getMiddle()).handler(h.getRight()));
                    router.mountSubRouter("/api/v1", paymentSubRouter);
                    var server = vertx.createHttpServer(
                            new HttpServerOptions()
                                    .setPort(config.getPort()));

                    return server.requestHandler(router).rxListen();
                });
    }

    @AllArgsConstructor
    @Value
    public static class EnvironmentConfig {
        private PersistenceModule.PersistenceConfig persistenceConfig;
        private Integer port;
    }

}
