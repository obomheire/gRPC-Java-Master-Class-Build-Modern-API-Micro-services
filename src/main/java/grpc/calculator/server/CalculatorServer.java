package grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Hello gRPC");
        int port = 50052;

        Server server = ServerBuilder.forPort(port).addService(new CalculatorServiceImpl()).build();
        server.start();

        System.out.println("Server started");
        System.out.println("Server listening on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
