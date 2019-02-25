package api.modules;

import api.persistence.DbClient;
import api.persistence.MongoDbClient;
import api.persistence.MongoDbClientWithCircuitBreakers;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.circuitbreaker.CircuitBreaker;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mongo.MongoClient;
import lombok.AllArgsConstructor;
import lombok.Value;

public class PersistenceModule {

    public static DbClient create(Vertx vertx, PersistenceConfig persistenceConfig) {
        var mongoClient = MongoClient.createShared(vertx, new JsonObject()
                .put("host", persistenceConfig.host)
                .put("port", persistenceConfig.port)
                .put("db_name", persistenceConfig.dbName));
        var mongoDbClient = new MongoDbClient(mongoClient);

        return new MongoDbClientWithCircuitBreakers(
                mongoDbClient,
                CircuitBreaker.create(
                        "mongo-db-circuit-breaker",
                        vertx,
                        new CircuitBreakerOptions().setFallbackOnFailure(true)));
    }

    @Value
    @AllArgsConstructor
    public static class PersistenceConfig {
        private final String host;
        private final Integer port;
        private final String dbName;
    }

}
