package handler;


import Route.PathName;
import io.netty.handler.codec.http2.Http2Connection;

import java.util.HashMap;
import java.util.Map;

public class RequestHandlerFactory {

    private static RequestHandlerFactory SINGLETON = new RequestHandlerFactory();

    private Map<String, Class<? extends RequestHandler>> resourceToHandlerMap = new HashMap<>();

    private RequestHandlerFactory() {
        initialize();
    }

    public static RequestHandlerFactory getInstance() {
        return SINGLETON;
    }

    private void initialize() {
        resourceToHandlerMap.put(PathName.INDEX, IndexRequestHandler.class);
    }

    public RequestHandler getRequestHandler(String requestedPath, Http2Connection connection) {

        RequestHandler handler;
        Class<? extends RequestHandler> result = resourceToHandlerMap.get(requestedPath);
        if (result == null) {
            handler = new UnknownRequestHandler();
        } else {
            try {
                handler = result.newInstance();
            } catch (InstantiationException | IllegalAccessException iae) {
                iae.printStackTrace();
                throw new IllegalStateException("Unable to load resource", iae);
            }
        }

        handler.setConnection(connection);
        return handler;
    }
}