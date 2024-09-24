package com.CustomHttpServer.visitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public interface Visitor {
    void handleGet() throws IOException;
    void handlePost() throws IOException;
}
