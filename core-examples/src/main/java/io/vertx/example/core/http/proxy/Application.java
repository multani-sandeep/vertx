package io.vertx.example.core.http.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerRequest;

public class Application extends AbstractVerticle {

	protected void log(Object... objects) {
		Arrays.stream(objects).forEach(object -> {
			System.out.println(this.getClass().getSimpleName() + ">" + object.toString());
		});
	}
	
//	protected void test(){
//		
//		HttpServlet h = new HttpServlet(){
//
//			@Override
//			public void doGet(HttpServletRequest req, HttpServletResponse resp) {
//				ProxyHandler p = new ProxyHandler(22);
//				
//				
//				
//			}
//			
//		}
//	}


	protected class ProxyHandler implements Handler<HttpServerRequest>{//, HttpServlet {

		int proxyPort;

		public ProxyHandler(int downPort) {
			proxyPort = downPort;
		}

		@Override
		public void handle(HttpServerRequest req) {
			HttpClient client = getVertx().createHttpClient(new HttpClientOptions());
			log("Proxying request: " + req.uri());

			HttpClientRequest c_req = client.request(req.method(), proxyPort, "localhost", mapURI(req), c_res -> {
				log("Proxying response: " + c_res.statusCode());
				req.response().setChunked(true);
				req.response().setStatusCode(c_res.statusCode());
				req.response().headers().setAll(c_res.headers());
				c_res.handler(data -> {
					log("Proxying response body: " + data.toString("ISO-8859-1"));
					req.response().write(data);
				});
				c_res.endHandler((v) -> req.response().end());
			});
			c_req.setChunked(true);
			c_req.headers().setAll(req.headers());
			req.handler(data -> {
				log("Proxying request body " + data.toString("ISO-8859-1"));
				c_req.write(data);
			});
			req.endHandler((v) -> c_req.end());
		}

//		@Override
//		public void doGet(HttpServletRequest req, HttpServletResponse resp) {
//			
//		}

	}

	private static class Mapper {
		public String destURI;
		public String srcURI;

		static public Mapper getDefaultMapper() {
			Mapper m = new Mapper();
			m.destURI = "/";
			return m;
		}
	}

	final static List<Mapper> reqTranslation = new ArrayList();
	static {
		Mapper m = new Mapper();
		m.srcURI = "/agentdesktop/login";
		m.destURI = "/commercial-cpm/v1/agent-login-request";
		reqTranslation.add(m);

	}

	private String mapURI(HttpServerRequest req) {
		return reqTranslation.stream().filter(translation -> {
			return req.uri().contains(translation.srcURI);
		}).findFirst().orElse(Mapper.getDefaultMapper()).destURI;
	}

}
