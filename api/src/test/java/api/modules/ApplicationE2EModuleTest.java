package api.modules;

import api.model.Payment;
import io.vertx.reactivex.core.Vertx;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import static io.restassured.RestAssured.given;

public class ApplicationE2EModuleTest {
    @ClassRule
    public static GenericContainer mongoTestClient = new GenericContainer<>("mongo:3.4.19-jessie")
            .withExposedPorts(27017);

    @BeforeClass
    public static void setup() {
        var vertx = Vertx.vertx();
        var application = ApplicationModule.createApplication(vertx,
                new ApplicationModule.EnvironmentConfig(
                        new PersistenceModule.PersistenceConfig(
                                "localhost",
                                mongoTestClient.getMappedPort(27017),
                                "form3"
                        ),
                        8080));
        application.subscribe(server -> System.out.println("up and running"), Throwable::printStackTrace);
    }

    @Test
    public void applicationShouldBeAbleToCreatePayment() {
        given()
                .header("x-request-id", "requestId")
                .body(new Payment("", "", 0, "", null))
                .post("/api/v1/payments")
                .then()
                .statusCode(201)
                .body("paymentId", Matchers.notNullValue());
    }

    @Test
    public void applicationShouldNotBeAbleToListPayment() {
        given()
                .get("/api/v1/payments")
                .then()
                .statusCode(501);
    }

    @Test
    public void applicationShouldNotBeAbleToDeletePayment() {
        given()
                .delete("/api/v1/payments/anyPaymentId")
                .then()
                .statusCode(501);
    }

    @Test
    public void applicationShouldNotBeAbleToGetPayment() {
        given()
                .get("/api/v1/payments/anyPaymentId")
                .then()
                .statusCode(501);
    }

}