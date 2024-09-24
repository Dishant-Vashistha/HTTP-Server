package com.CustomHttpServer;

import com.CustomHttpServer.RequestHandlers.RequestHandler;
import com.CustomHttpServer.visitor.RequestHandlingVisitor;
import com.CustomHttpServer.visitor.Visitor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CustomHttpServer {

    private static final int THREAD_POOL_SIZE = 100;
    private static final int QUEUE_SIZE = 10;
    private static final ExecutorService threadPool = new ThreadPoolExecutor(
            THREAD_POOL_SIZE, // corePoolSize (initial thread pool size)
            THREAD_POOL_SIZE, // maximumPoolSize (max number of threads in pool)
            0L, TimeUnit.MILLISECONDS, // keepAliveTime, TimeUnit
            new LinkedBlockingQueue<>(QUEUE_SIZE), // Bounded queue with a max size
            new ThreadPoolExecutor.AbortPolicy() // Rejection policy (Abort if queue is full)
    );
    private static final Logger log = Logger.getLogger("CustomHttpServer");

    public static void main(String[] args) {
        try {
            // Create a server socket to listen on port args[0]
            String port = args[0];
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
            log.info("Server started on port :" + port);

            // Run the server continuously
            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
               try {
                    // Submit the task to the thread pool
                    threadPool.submit(() -> handleClientRequest(clientSocket));
                } catch (RejectedExecutionException e) {
                    // If the thread pool and queue are full, handle the rejection
                    log.info("Server busy, rejecting client connection.");
                    handleServerBusy(clientSocket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            Visitor visitor = new RequestHandlingVisitor(out,in);

            String requestLine = in.readLine();
            log.info("Received request: " + requestLine);

            if (requestLine != null && requestLine.startsWith("GET")) {
                RequestHandler.GET.handle(visitor);
            } else if (requestLine != null && requestLine.startsWith("POST")) {
                RequestHandler.POST.handle(visitor);
            } else {
                // Handle unsupported requests (e.g., PUT, DELETE)
                String httpResponse = "HTTP/1.1 405 Method Not Allowed\r\n" +
                        "Content-Type: application/json\r\n" +
                        "\r\n" +
                        "{ \"error\": \"405 Method Not Allowed\" }";
                out.write(httpResponse.getBytes());
            }

            // Close the streams and socket
            out.flush();
            out.close();
            in.close();
            clientSocket.close();
            System.out.println("Client connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Method to handle "Server Busy" (503 Service Unavailable)
    private static void handleServerBusy(Socket clientSocket) {
        try {
            OutputStream out = clientSocket.getOutputStream();
            // Respond with 503 Service Unavailable
            String httpResponse = "HTTP/1.1 503 Service Unavailable\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    "{ \"error\": \"Server is busy. Please try again later.\" }";
            out.write(httpResponse.getBytes());

            // Close the socket
            out.flush();
            out.close();
            clientSocket.close();
            System.out.println("Server is busy, rejected client connection.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}