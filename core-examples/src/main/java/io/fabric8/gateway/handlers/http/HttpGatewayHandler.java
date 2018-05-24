/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.gateway.handlers.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.vertx.java.core.VoidHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 */
public class HttpGatewayHandler implements Handler<HttpServerRequest> {

    private final io.vertx.core.Vertx vertx;
    //private final HttpGateway httpGateway;
    private final ObjectMapper mapper = new ObjectMapper();

    public HttpGatewayHandler(Vertx vertx){//, HttpGateway httpGateway) {
        this.vertx = vertx;
        //this.httpGateway = httpGateway;
    }

    @Override
    public void handle(final HttpServerRequest request) {
    	long callStart = System.nanoTime();
        String uri = request.uri();
        String uri2 = null;
        if (!uri.endsWith("/")) {
            uri2 = uri + "/";
        }
        
        // lets map the request URI to map to the service URI and then the renaming URI
        // using mapping rules...
       
        String remaining = null;
        String prefix = null;
        String proxyServiceUrl = "http://www.yahoo.com";
        
        HttpClient client = createClient();
		
        //String reverseServiceUrl = null;
        try {
            if (request.absoluteURI().contains("ppp")) {
                // lets return the JSON of all the results
                //String json = mappingRulesToJson(mappingRules);
                HttpServerResponse response = request.response();
                response.headers().set("ContentType", "application/json");
                response.end("Hello");
                response.setStatusCode(200);
            } else {

                if (client != null) {
                    String servicePath = prefix != null ? prefix : "";
                    // we should usually end the prefix path with a slash for web apps at least
                    if (servicePath.length() > 0 && !servicePath.endsWith("/")) {
                        servicePath += "/";
                    }
                    if (remaining != null) {
                        servicePath += remaining;
                    }

                    System.out.println("Proxying request " + uri + " to service path: " + servicePath + " on service: " + proxyServiceUrl );//+ " reverseServiceUrl: " + reverseServiceUrl);
                    final HttpClient finalClient = client;
                    Handler<HttpClientResponse> responseHandler = new Handler<HttpClientResponse>() {
                        public void handle(HttpClientResponse clientResponse) {
                        	System.out.println("Proxying response: " + clientResponse.statusCode());
                            request.response().setStatusCode(clientResponse.statusCode());
                            //request.response().headers().set(clientResponse.headers());
                            request.response().setChunked(true);
                            clientResponse.bodyHandler(new Handler<Buffer>() {
                                public void handle(Buffer data) {
                                	System.out.println("Proxying response body:" + data);
                                    request.response().write(data);
                                }
                            });
                            clientResponse.endHandler(new VoidHandler() {
                            	public void handle() {
                                    request.response().end();
                                    finalClient.close();
                                }
							});
                        }
                    };
//                    if (mappedServices != null) {
//                        ProxyMappingDetails proxyMappingDetails = new ProxyMappingDetails(proxyServiceUrl, reverseServiceUrl, servicePath);
//                        responseHandler = mappedServices.wrapResponseHandlerInPolicies(request, responseHandler, proxyMappingDetails);
//                    }
                    final HttpClientRequest clientRequest = client.request(request.method(), servicePath, responseHandler);
                    //clientRequest.headers().set(request.headers());
                    clientRequest.setChunked(true);
                    request.bodyHandler(new Handler<Buffer>() {
                        public void handle(Buffer data) {
                        	System.out.println("Proxying request body:" + data);
                            clientRequest.write(data);
                        }
                    });
                    request.endHandler(new VoidHandler() {
                        public void handle() {
                        	System.out.println("end of the request");
                            clientRequest.end();
                        }
                    });

                } else {
                    //  lets return a 404
                	System.out.println("Could not find matching proxy path for " + uri + " from paths: " );//+ mappingRules.keySet());
                    request.response().setStatusCode(404);
                    request.response().close();
                }
            }
            
        } catch (Throwable e) {
        	System.out.println("Caught: " + e );
            request.response().setStatusCode(404);
            StringWriter buffer = new StringWriter();
            e.printStackTrace(new PrintWriter(buffer));
            request.response().setStatusMessage("Error: " + e + "\nStack Trace: " + buffer);
            request.response().close();
        }
    }

//    protected String mappingRulesToJson(Map<String, MappedServices> rules) throws IOException {
//        Map<String, Collection<String>> data = new HashMap<String, Collection<String>>();
//
//        Set<Map.Entry<String, MappedServices>> entries = rules.entrySet();
//        for (Map.Entry<String, MappedServices> entry : entries) {
//            String key = entry.getKey();
//            MappedServices value = entry.getValue();
//            Collection<String> serviceUrls = value.getServiceUrls();
//            data.put(key, serviceUrls);
//        }
//        return mapper.writeValueAsString(data);
//    }

//    protected boolean isMappingIndexRequest(HttpServerRequest request) {
//        if (httpGateway == null || !httpGateway.isEnableIndex()) {
//            return false;
//        }
//        String uri = request.uri();
//        return uri == null || uri.length() == 0 || uri.equals("/");
//    }

    protected HttpClient createClient()  {
        // lets create a client
    	HttpClientOptions m = new HttpClientOptions();
    	m.setDefaultHost("www.yahoo.com");
    	m.setDefaultPort(80);
        HttpClient client = vertx.createHttpClient(m);
        return client;

    }

}