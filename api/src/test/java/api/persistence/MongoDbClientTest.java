package api.persistence;

import api.model.Payment;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.mongo.MongoClient;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MongoDbClientTest {

    @Rule
    public GenericContainer mongoTestClient = new GenericContainer<>("mongo:3.4.19-jessie")
            .withExposedPorts(27017);

    @Test
    public void shouldCreateANewPayment() {
        var vertx = Vertx.vertx();
        var mongo = MongoClient.createNonShared(
                vertx,
                new JsonObject()
                        .put("host", "localhost")
                        .put("port", mongoTestClient.getMappedPort(27017))
                        .put("db_name", "form3"));
        var client = new MongoDbClient(mongo);
        var result = client.create(new Payment("Payment", UUID.randomUUID().toString(), 0, UUID.randomUUID().toString(), null)).blockingGet();
        Assert.assertTrue(result.isRight());
        Assert.assertNotNull(result.get());
    }

    @Test
    public void shouldReturnLeftOnMongoError() {
        var mongo = mock(MongoClient.class);
        when(mongo.rxInsert(anyString(), any(JsonObject.class))).thenThrow(new RuntimeException("error"));

        var client = new MongoDbClient(mongo);
        var result = client.create(new Payment("Payment", UUID.randomUUID().toString(), 0, UUID.randomUUID().toString(), null)).blockingGet();
        Assert.assertTrue(result.isLeft());
    }

    @Test
    public void shouldDeleteExistingPayment() {
        var vertx = Vertx.vertx();
        var mongo = MongoClient.createNonShared(
                vertx,
                new JsonObject()
                        .put("host", "localhost")
                        .put("port", mongoTestClient.getMappedPort(27017))
                        .put("db_name", "form3"));
        var client = new MongoDbClient(mongo);
        //create a new payment in the empty db
        var result = client.create(new Payment("Payment", UUID.randomUUID().toString(), 0, UUID.randomUUID().toString(), null)).blockingGet();
        Assert.assertTrue(result.isRight());
        Assert.assertNotNull(result.get());
        //real test begins here
        var deletionResult = client.delete(result.get()).blockingGet();
        Assert.assertTrue(deletionResult.isRight());
        Assert.assertTrue(deletionResult.get());
    }

    @Test
    public void shouldResponseWithFalseWhenPaymentIdNotFound() {
        var vertx = Vertx.vertx();
        var mongo = MongoClient.createNonShared(
                vertx,
                new JsonObject()
                        .put("host", "localhost")
                        .put("port", mongoTestClient.getMappedPort(27017))
                        .put("db_name", "form3"));
        var client = new MongoDbClient(mongo);
        var deletionResult = client.delete("notExistingId").blockingGet();
        Assert.assertTrue(deletionResult.isRight());
        Assert.assertFalse(deletionResult.get());
    }

    @Test
    public void shouldReturnLeftOnMongoErrorOnDelete() {
        var mongo = mock(MongoClient.class);
        when(mongo.rxFindOneAndDelete(anyString(), any(JsonObject.class))).thenThrow(new RuntimeException("error"));

        var client = new MongoDbClient(mongo);
        var result = client.delete("anyId").blockingGet();
        Assert.assertTrue(result.isLeft());
    }

    @Test
    public void shouldUpdateExistingPayment() {
        var vertx = Vertx.vertx();
        var mongo = MongoClient.createNonShared(
                vertx,
                new JsonObject()
                        .put("host", "localhost")
                        .put("port", mongoTestClient.getMappedPort(27017))
                        .put("db_name", "form3"));
        var client = new MongoDbClient(mongo);
        //create a new payment in the empty db
        var result = client.create(new Payment("Payment", UUID.randomUUID().toString(), 90, UUID.randomUUID().toString(), null)).blockingGet();
        Assert.assertTrue(result.isRight());
        Assert.assertNotNull(result.get());
        //real test begins here
        var updateResult = client.update(new DbClient.UpdateRequest(result.get(), new JsonObject().put("version", 999))).blockingGet();
        Assert.assertTrue(updateResult.isRight());
        Assert.assertEquals(999, updateResult.get().getVersion().intValue());
    }
}