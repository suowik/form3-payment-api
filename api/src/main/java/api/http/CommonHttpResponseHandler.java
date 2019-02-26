package api.http;

import api.exceptions.ValidationException;
import io.vavr.API;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.List;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

public class CommonHttpResponseHandler {

    public static <R> void handleCreateResponse(RoutingContext ctx, Either<Throwable, R> input) {
        handleResponse(201, ctx, input);
    }

    public static <R> void handleOKResponse(RoutingContext ctx, Either<Throwable, R> input) {
        handleResponse(200, ctx, input);
    }

    private static <R> void handleResponse(Integer successStatusCode, RoutingContext ctx, Either<Throwable, R> input) {
        var response = ctx.response();
        input.fold(error -> {
            var statusCodeWithReason = API.Match(error).of(
                    Case($(instanceOf(ValidationException.class)), createErrorResponseObject(400, ((ValidationException) error).getValidationExceptions().stream().map(Throwable::getMessage).collect(Collectors.toList()))),
                    Case($(instanceOf(Throwable.class)), createErrorResponseObject(500, ((ValidationException) error).getValidationExceptions().stream().map(Throwable::getMessage).collect(Collectors.toList())))
                    //Case($(), createErrorResponseObject(500, List.of(error.getMessage())))
            );
            response.setStatusCode(statusCodeWithReason._1)
                    .putHeader("content-type", "application/json")
                    .end(statusCodeWithReason._2.encode());
            return null;
        }, success -> {
            response.setStatusCode(successStatusCode)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject.mapFrom(success).encode());
            return null;
        });
    }

    private static Tuple2<Integer, JsonObject> createErrorResponseObject(Integer statusCode, List<String> message) {
        JsonArray reasons = message.stream()
                .reduce(new JsonArray(),
                        (array, error) -> array.add(new JsonObject().put("reason", error)),
                        JsonArray::add);
        return Tuple.of(statusCode, new JsonObject()
                .put("errors",
                        reasons));
    }

}
