package grpc.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GreetingClient {

/*
     //INITIAL DEMO EXAMPLE
        public static void main(String[] args) {
            System.out.println("Hello! I'm a gRPC greeting client");
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                    .usePlaintext().build();
            System.out.println("Creating Stub");
            // Synchronous
            DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);
            //Asynchronous
            //DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel)
            //Do something/write some code
            System.out.println("Shutting down channel");
            channel.shutdown();
            }
*/

/*

//UNARY API EXAMPLE
    public static void main(String[] args) {
        System.out.println("Hello! I'm a gRPC greeting client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext().build();

        System.out.println("Creating Stub");

        //Created a greet service client (blocking synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

           // Created a protocol buffer greeting message
                  Greeting greeting = Greeting.newBuilder().setFirstName("Zack").setLastName("Bello").build();

                  //Do the same for a greeting request
                  GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

                  //Call the RPC and get back a GreetingResponse (protocol buffers)
                  GreetResponse greetResponse = greetClient.greet(greetRequest);

                  System.out.println(greetResponse.getResult());

                  //Do something/write some code
                  System.out.println("Shutting down channel");
                  channel.shutdown();
             }

*/


/*

   //SERVER STREAMING EXAMPLE
   public static void main(String[] args) {
        System.out.println("Hello! I'm a gRPC greeting client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext().build();

        System.out.println("Creating Stub");

        //Created a greet service client (blocking synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Zack")).build();

        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });

        //Do something/write some code
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
*/

    //CODE REFACTOR

    public static void main(String[] args) throws IOException {
        System.out.println("Hello! I'm a gRPC greeting client");
        GreetingClient main = new GreetingClient();
        main.run();

        System.out.println("Creating Stub");

    }

    public  void  run() throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext().build();

        //SERVER AUTHENTICATION SSL/TSL
        ChannelCredentials creds = TlsChannelCredentials.newBuilder().trustManager(new File("ssl/ca.crt"))
                .build();

        ManagedChannel secureChannel = Grpc.newChannelBuilderForAddress("localhost", 50051, creds).build();

//        doUnaryCall(channel);
//        doServerSreamingCall(channel);
//        doClientStreamingCall(channel);
//        doBiDiStreamingCall(channel);
//        doUnaryCallWithDeadline(channel);
        doUnaryCall(secureChannel);

        //Do something/write some code
        System.out.println("Shutting down channel!");
        channel.shutdown();
    }

    //UNARY API CALL
    private void doUnaryCall(ManagedChannel channel) {
        //Created a greet service client (blocking synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder().setFirstName("Zack").setLastName("Bello").build();

        //Do the same for a greeting request
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

        //Call the RPC and get back a GreetingResponse (protocol buffers)
        GreetResponse greetResponse = greetClient.greet(greetRequest);

        System.out.println(greetResponse.getResult());
    }

    //SERVER STREAMING CALL
    private void doServerSreamingCall(ManagedChannel channel) {
        //Created a greet service client (blocking synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Zack")).build();

        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });
    }

    private void doClientStreamingCall(ManagedChannel channel) {

        //CREATE AN ASYNCHRONOUS CLIENT
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                //WE GOT A RESPONSE FROM THE SERVER
                System.out.println("Received a response from the server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                //THE SERVER HAS COMPLETED SENDING THE DATA
                System.out.println("Server has completed sending the data");
                latch.countDown();
            }
        });

        //STREAMING MESSAGE NUMBER #1
        System.out.println("Sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Zack")
                .build()).build());
        //STREAMING MESSAGE NUMBER #2
        System.out.println("Sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Gideon")
                .build()).build());
        //STREAMING MESSAGE NUMBER #3
        System.out.println("Sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Mary")
                .build()).build());

        //TELL THE SERVER THAT THE CLIENT IS DONE SENDING DATA
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        //CREATE AN ASYNCHRONOUS CLIENT
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> responseObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Response from server " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
            }
        });

        Arrays.asList("Zack", "Yusuf", "Gideon", "Muktar", "Maryam").forEach(
                name -> {
                    System.out.println("Sending " + name);
                    responseObserver.onNext(GreetEveryoneRequest.newBuilder().setGreeting(Greeting.newBuilder()
                            .setFirstName(name)).build());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        responseObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doUnaryCallWithDeadline(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(channel);

        try {
            System.out.println("Sending a request with a deadline of 3000 ms");
            GreetWithDeadlineResponse response = blockingStub.withDeadlineAfter(3000, TimeUnit.MILLISECONDS)
                    .greetWithDeadline(GreetWithDeadlineRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Zack"))
                            .build());

            System.out.println(response.getResult());
        }catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline exceeded!");
            }else {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("Sending a request with a deadline of 100 ms");
            GreetWithDeadlineResponse response = blockingStub.withDeadlineAfter(100, TimeUnit.MILLISECONDS)
                    .greetWithDeadline(GreetWithDeadlineRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("Justina"))
                            .build());

            System.out.println(response.getResult());
        }catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline exceeded!");
            }else {
                e.printStackTrace();
            }
        }
    }

}
