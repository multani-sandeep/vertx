package io.vertx.example.core.http.proxy;

import io.vertx.example.util.Runner;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class HybrisAD extends Application {

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Runner.runExample(HybrisAD.class);
	}

	@Override
	public void start() throws Exception {
		
		final int appAD = 8080;
		final int appACP= 8181;
		startApp(appAD, appACP);
	}
	
	public void startServer(int port) {
		 vertx.createHttpServer().requestHandler(req -> {
		      req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
		    }).listen(port);
	}

	public void startApp(int port, int downPort ){
		vertx.createHttpServer().requestHandler(new ProxyHandler(downPort)).listen(port);
	}

}
