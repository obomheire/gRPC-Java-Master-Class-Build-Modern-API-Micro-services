package grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");
        int port = 50051;

/*
        //PLAIN TEXT SERVER
        Server server = ServerBuilder.forPort(port).addService(new GreetServiceImpl()).build();
        server.start();
        System.out.println("Server started");
        System.out.println("Server listening on port " + port);
*/

        //SECURE SERVER
        Server server = ServerBuilder.forPort(port).addService(new GreetServiceImpl())
                .useTransportSecurity(new File("ssl/server.crt"), new File("ssl/server.pem")).build();
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
