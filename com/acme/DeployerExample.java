package com.acme;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class DeployerExample extends Verticle {
  private static final String PERSISTOR_MAIN_ADDRESS = "persistor.main.address";
  private static final String PERSISTOR_MODULE_NAME = "io.vertx~mod-mongo-persistor~2.0.0-beta2";
  private static final String MONGO_HOST = "localhost";
  private static final int MONGO_PORT = 27017;
  private static final String DB_NAME = "deployer-example";

  @Override
  public void start() {
    // Deploy the persistor module.
    JsonObject persistorConfig = new JsonObject();
    persistorConfig.putString("address", PERSISTOR_MAIN_ADDRESS);
    persistorConfig.putString("host", MONGO_HOST);
    persistorConfig.putNumber("port", MONGO_PORT);
    persistorConfig.putString("db_name", DB_NAME);

    // Deploy a module.
    final CountDownLatch deployLatch = new CountDownLatch(1);
    container.deployModule(PERSISTOR_MODULE_NAME, persistorConfig,
        new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<String> result) {
            container.logger().info(
                "Expecting this first; Thread = " + Thread.currentThread().getName());
            if (result.succeeded()) {
              container.logger().info("Persistor module deployed.");
            } else {
              container.logger().fatal("Problem deploying persistor module.", result.cause());
            }
            // Signal that the module was deployed.
            deployLatch.countDown();
          }
        });

    // Wait until the module is deployed.
    try {
      if (!deployLatch.await(10L, TimeUnit.SECONDS)) {
        container.logger().warn("Timeout occurred!");
      }
    } catch (InterruptedException intEx) {
      container.logger().warn(intEx);
    }
    container.logger().info("Expecting this last; Thread = " + Thread.currentThread().getName());
  }
}
