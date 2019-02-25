package api.persistence;

import api.model.Payment;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vavr.control.Either;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.reactivex.ext.mongo.MongoClient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MongoDbClient implements DbClient {

    static String COLLECTION = "payments";

    private final MongoClient client;

    public MongoDbClient(MongoClient client) {
        this.client = client;
    }

    @Override
    public Single<Either<Throwable, String>> create(Payment payment) {
        return Single.just(payment)
                .observeOn(Schedulers.computation())
                .flatMapMaybe(p -> client.rxInsert(COLLECTION, JsonObject.mapFrom(p)).observeOn(Schedulers.computation()))
                .map(Either::<Throwable, String>right)
                .onErrorReturn(Either::left)
                .doOnError(Throwable::printStackTrace)
                .toSingle();
    }

    @Override
    public Single<Either<Throwable, Boolean>> delete(String paymentId) {

        return Single.just(paymentId)
                .observeOn(Schedulers.computation())
                .flatMapMaybe(id -> client.rxFindOneAndDelete(COLLECTION, new JsonObject().put("_id", id)).observeOn(Schedulers.computation()))
                .map(Objects::nonNull)
                .toSingle(false)
                .doOnError(Throwable::printStackTrace)
                .map(Either::<Throwable, Boolean>right)
                .onErrorReturn(Either::left);
    }

    //not tested on purpose
    @Override
    public Single<Either<Throwable, Payment>> getOne(String paymentId) {
        return Single.just(paymentId)
                .observeOn(Schedulers.computation())
                .flatMapMaybe(id -> client.rxFindOne(COLLECTION, new JsonObject().put("_id", id), new JsonObject()))
                .map(s -> Either.<Throwable, Payment>right(s.mapTo(Payment.class)))
                .doOnError(Throwable::printStackTrace)
                .toSingle(Either.left(new Exception()));
    }

    @Override
    public Single<Either<Throwable, Payment>> update(UpdateRequest updateRequest) {

        return Single.just(updateRequest)
                .observeOn(Schedulers.computation())
                .flatMapMaybe(request -> {
                    var updateData = new JsonObject();
                    request.getUpdateData().forEach(e -> updateData.put("$set", new JsonObject().put(e.getKey(), e.getValue())));
                    return client.rxFindOneAndUpdate(COLLECTION, new JsonObject().put("_id", request.getPaymentId()), updateData);
                })
                .flatMapSingle(s -> getOne(s.getString("_id")))
                .doOnError(Throwable::printStackTrace)
                .onErrorReturn(Either::left);
    }


    @Override
    public Single<Either<Throwable, List<Payment>>> search(SearchCriteria searchCriteria) {
        return Single.just(searchCriteria)
                .observeOn(Schedulers.computation())
                .flatMap(c -> client.rxFindWithOptions(COLLECTION, new JsonObject(), new FindOptions().setLimit(c.getLimit()).setSkip(c.getOffset())))
                .map(l -> l.stream().map(payment -> payment.mapTo(Payment.class)).collect(Collectors.toList()))
                .map(Either::<Throwable, List<Payment>>right)
                .onErrorReturn(Either::left);
    }
}
