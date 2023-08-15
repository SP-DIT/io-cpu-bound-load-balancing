import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

        httpServer.createContext("/io-bound", new IoBoundHandler());
        httpServer.createContext("/cpu-bound", new CpuBoundHandler());

        httpServer.start();
        System.out.println("Server started on port 8080");
    }
}

class IoBoundHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        CompletableFuture<Void> allRequestsCompleted = new CompletableFuture<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // TODO: Set number of concurrent request
        int numberOfConcurrentRequest = 10;
        for (int i = 0; i < numberOfConcurrentRequest; i++) {
            executorService.submit(() -> makeHttpRequest(allRequestsCompleted));
        }
        executorService.shutdown();

        try {
            allRequestsCompleted.get(); // Wait for all requests to complete
        } catch (Exception e) {
            e.printStackTrace();
        }

        String response = "All IO-bound requests completed.";
        sendResponse(exchange, response);
    }

    private void makeHttpRequest(CompletableFuture<Void> allRequestsCompleted) {
        try {
            // TODO: Set delay duration
            int delay = 3000; //ms
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            allRequestsCompleted.complete(null);
        }
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

class CpuBoundHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        long sum = 0;

        // TODO: Set duration of CPU-bound operation
        long loopCount = 100000;

        for (int i = 0; i < loopCount; i++) {
            for (int j = 0; j < loopCount; j++) {
                sum += i + j;
            }
        }

        String response = "CPU-bound computation done. Sum: " + sum;
        sendResponse(exchange, response);
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
