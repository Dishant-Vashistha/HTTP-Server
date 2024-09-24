# HTTP-Server
This repository contains a custom-built multi-threaded HTTP server in Java. The server listens for HTTP requests, parses them, and provides appropriate responses. The server uses a thread pool for concurrency and implements the Visitor pattern to handle different HTTP methods (e.g., GET, POST) in a clean and extensible manner.

Features
Multi-threaded: The server uses a thread pool to handle multiple client connections concurrently, with a bounded queue to control the number of active threads and waiting requests.
Custom HTTP Handling: This server manually parses HTTP requests and generates HTTP responses.
Visitor Pattern: Different HTTP methods (e.g., GET, POST) are handled using the Visitor pattern, making the design extensible and clean.
Thread Pool with Bounded Queue: Limits the number of active connections and gracefully handles server overload by rejecting new requests with a "503 Service Unavailable" message.
Supports GET and POST Requests: The server processes both GET and POST requests and returns JSON responses.
Requirements
Java 8 or higher
Maven (optional, for managing dependencies)
How to Run
1. Clone the Repository
   bash
    ```shell
   git clone https://github.com/yourusername/CustomJavaHttpServer.git
   ```
   cd CustomJavaHttpServer
2. Compile and Run the Server
   You can compile the Java files manually using the following commands:

bash
```shell
javac -d bin src/com/CustomHttpServer/*.java
java -cp bin com.CustomHttpServer.CustomDishantHttpServer
```
Alternatively, you can use Maven to compile and run the server.

3. Server Output
   Once the server starts, it will output:

yaml
```yaml
Server started on port: 8080
```
The server is now running and ready to accept HTTP connections on port 8080.

4. Send HTTP Requests
   You can use any HTTP client (like curl or Postman) to send HTTP requests to the server:

Example GET Request:
bash
```shell
curl -X GET http://localhost:8080/
```

Response:

json
```json
{
"message": "Hello, World!"
}
```

Example POST Request:
bash
```shell
curl -X POST http://localhost:8080/ -H "Content-Type: application/json" -d '{"Name": "Dishant Vashistha", "DOB": "05/07/2000"}'
```

Response:

json
```json
{
  "received": {
    "Name": "Dishant Vashistha",
    "DOB": "05/07/2000"
  }
}
```

# **Design Overview**

## Multi-threaded Server

The server uses a thread pool with a maximum size of 100 threads and a bounded queue with a size of 50 to limit the number of tasks waiting to be processed.
If the queue is full, the server responds with a 503 Service Unavailable error.
Visitor Pattern for HTTP Methods
The server uses the Visitor Pattern to handle different HTTP methods (e.g., GET, POST). This allows for an organized and extensible design where new HTTP methods can be added without changing the core server logic.

Code Structure
com.CustomHttpServer.CustomDishantHttpServer: The main class that starts the server, listens for client connections, and manages the thread pool.

com.CustomHttpServer.HttpRequestHandler: Handles incoming HTTP requests and delegates the request to the appropriate visitor based on the HTTP method (e.g., GET, POST).

com.CustomHttpServer.HttpMethodVisitor: Interface that defines the visitable HTTP methods (e.g., GET, POST).

com.CustomHttpServer.GetRequestVisitor / PostRequestVisitor: Concrete implementations of HttpMethodVisitor to handle GET and POST requests.

Thread Pool and Bounded Queue
The thread pool is implemented using ThreadPoolExecutor with a bounded queue to control the number of concurrent tasks.
The pool will spawn up to THREAD_POOL_SIZE threads, and once the queue is full, any additional client requests are rejected with a "Server Busy" message (503 error).
Handling HTTP Requests
GET Request:

The server responds to GET requests with a JSON message: { "message": "Hello, World!" }.
POST Request:

The server reads the body of the POST request and echoes the received data in JSON format: { "received": <POST Body> }.
Extending the Server
This server is designed to be easily extensible
