package io.vertx.example.core.http.proxy;

import java.util.Arrays;

import io.vertx.core.AbstractVerticle;

public class Application extends AbstractVerticle {
	
	protected void log(Object...objects) {
		Arrays.stream(objects).forEach(object ->{System.out.println(this.getClass().getSimpleName()+">"+object.toString());});
	}

}
