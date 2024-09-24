package com.CustomHttpServer.visitor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class RequestHandlingVisitor implements Visitor{

    private final OutputStream outputStream;
    private final BufferedReader reader;

    public RequestHandlingVisitor(OutputStream outputStream, BufferedReader reader) {
        this.outputStream = outputStream;
        this.reader = reader;
    }

    @Override
    public void handleGet() throws IOException {
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n";
        outputStream.write(httpResponse.getBytes());

        // Send the JSON response body
        String jsonResponse = "{ \"message\": \"Hello, World!\" }";
        outputStream.write(jsonResponse.getBytes());
    }

    @Override
    public void handlePost() throws IOException {
        String line;
        int contentLength = 0;
        while (!(line = reader.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Read the body of the POST request
        char[] body = new char[contentLength];
        reader.read(body, 0, contentLength);
        String requestBody = new String(body);
        System.out.println("Received POST body: " + requestBody);

        // Respond with the received data in JSON format
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n";
        outputStream.write(httpResponse.getBytes());

        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("received",gson.fromJson(requestBody, JsonObject.class));

//        String jsonResponse = "{ \"received\": \"" + requestBody.replace("\"", "\\\"") + "\" }";
        outputStream.write(jsonObject.toString().getBytes());
    }
}
