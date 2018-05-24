package io.vertx.example.core.http.proxy;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public class MyRequestHandler implements Handler<HttpServerRequest> {

	@Override
	public void handle(HttpServerRequest req) {

	      System.out.println("Got request " + req.uri());

	      for (String name : req.headers().names()) {
	        System.out.println(name + ": " + req.headers().get(name));
	      }

	      req.handler(data -> System.out.println("Got data " + data.toString("ISO-8859-1")));

	      req.endHandler(v -> {
	        // Now send back a response
	        req.response().setChunked(true);

	        for (int i = 0; i < 10; i++) {
	          req.response().write("server-data-chunk-" + i);
	        }

	        req.response().end();
	      });
	}

}
