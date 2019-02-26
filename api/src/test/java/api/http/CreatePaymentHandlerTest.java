package api.http;

import api.model.Payment;
import io.reactivex.Single;
import io.restassured.RestAssured;
import io.vavr.control.Either;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class CreatePaymentHandlerTest {

    @Test
    public void verifyIneractions() {
        RestAssured.port = 62302;
        var vertx = Vertx.vertx();
        var router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/dummyPath").handler(new CreatePaymentHandler((payment) -> Single.just(Either.right("generated id"))));
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(62302);
        when().post("/dummyPath")
                .then()
                .statusCode(400)
                .body("errors", Matchers.hasSize(2))
                .body("errors[0].reason", Matchers.is("missing request id"));
        given()
                .header("x-request-id", "requestId")
                .body(new Payment("", "", 0, "", null))
                .post("/dummyPath")
                .then()
                .statusCode(201)
                .body("paymentId", Matchers.is("generated id"));
    }


}