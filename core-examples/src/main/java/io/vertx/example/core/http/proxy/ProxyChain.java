package io.vertx.example.core.http.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.example.util.Runner;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ProxyChain extends AbstractVerticle {

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Runner.runExample(ProxyChain.class);
	}

	@Override
	public void start() throws Exception {
		
		final int appAD = 8080;
		final int appACP= 8181;
		final int appEI = 8282;
		final int appAL = 8384;
		final int appDeadend = 8484;
		
		startServer(appDeadend);
		startApp(appAL, appDeadend);
		startApp(appEI, appAL);
		startApp(appACP, appEI);
		startApp(appAD, appACP);
	}
	
	public void startServer(int port) {
		 vertx.createHttpServer().requestHandler(req -> {
		      req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
		    }).listen(port);
	}

	public void startApp(int port, int downPort ){
		HttpClient client = vertx.createHttpClient(new HttpClientOptions());
		vertx.createHttpServer().requestHandler(req -> {
			System.out.println("Proxying request: " + req.uri());
			HttpClientRequest c_req = client.request(req.method(), downPort, "localhost", req.uri(), c_res -> {
				System.out.println("Proxying response: " + c_res.statusCode());
				req.response().setChunked(true);
				req.response().setStatusCode(c_res.statusCode());
				req.response().headers().setAll(c_res.headers());
				c_res.handler(data -> {
					System.out.println("Proxying response body: " + data.toString("ISO-8859-1"));
					req.response().write(data);
				});
				c_res.endHandler((v) -> req.response().end());
			});
			c_req.setChunked(true);
			c_req.headers().setAll(req.headers());
			req.handler(data -> {
				System.out.println("Proxying request body " + data.toString("ISO-8859-1"));
				c_req.write(data);
			});
			req.endHandler((v) -> c_req.end());
		}).listen(port);
	}
}
