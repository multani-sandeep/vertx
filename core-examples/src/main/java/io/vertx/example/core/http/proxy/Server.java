package io.vertx.example.core.http.proxy;

import io.fabric8.gateway.handlers.http.HttpGatewayHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.example.util.Runner;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends AbstractVerticle {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(Server.class);
  }

  @Override
  public void start() throws Exception {
	Handler<HttpServerRequest> req= new MyRequestHandler();
	req = new HttpGatewayHandler(vertx);
	System.out.println("HTTP Gateway Handler");
    vertx.createHttpServer().requestHandler(req).listen(8282);

  }

}