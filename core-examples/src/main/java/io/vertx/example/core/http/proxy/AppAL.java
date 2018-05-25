package io.vertx.example.core.http.proxy;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.example.util.Runner;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class AppAL extends Application {

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Runner.runExample(AppAL.class);
	}

	@Override
	public void start() throws Exception {
		
		final int appAL = 8383;
		startServer(appAL);
	}
	
	public void startServer(int port) {
		
		 vertx.createHttpServer().requestHandler(req -> {
			 log("Send response");
//			 try {
//				Thread.sleep(1000);
//				log("Added Delay");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		      req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
		    }).listen(port);
	}

	public void startApp(int port, int downPort ){
		HttpClient client = vertx.createHttpClient(new HttpClientOptions());
		vertx.createHttpServer().requestHandler(req -> {
			log("Proxying request: " + req.uri());
			HttpClientRequest c_req = client.request(req.method(), downPort, "localhost", req.uri(), c_res -> {
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
		}).listen(port);
	}
}
