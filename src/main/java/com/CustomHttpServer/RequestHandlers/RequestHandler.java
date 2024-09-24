package com.CustomHttpServer.RequestHandlers;

import com.CustomHttpServer.visitor.Visitor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public enum RequestHandler {
    POST {
        @Override
        public void handle(Visitor visitor) throws IOException {
            visitor.handlePost();
        }
    },
    GET {
        @Override
        public void handle(Visitor visitor) throws IOException {
            visitor.handleGet();
        }
    };

    public abstract void handle(Visitor visitor) throws IOException;
}
