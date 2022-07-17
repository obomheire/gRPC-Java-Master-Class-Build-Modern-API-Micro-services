package grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        //Extract the field that is needed
        Greeting greeting = request.getGreeting();
        String firstname = greeting.getFirstName();
        String lastname = greeting.getLastName();

        //Create the response
        String result = String.format("Hello! %s %s.", firstname, lastname);

        GreetResponse response = GreetResponse.newBuilder()
                    .setResult(result)
                    .build();

        //Send the response
        responseObserver.onNext(response);

        //Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();

       try {
           for ( int i = 0; i < 10; i++) {
               String result = String.format("Hello %s response number: %s", firstName, i);
               GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                       .setResult(result).build();

               responseObserver.onNext(response);
               Thread.sleep(1000L);
        }
    }catch (InterruptedException e) {
            e.printStackTrace();
       }finally {
            responseObserver.onCompleted();
       }
       }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {

            String result = "";
            @Override
            public void onNext(LongGreetRequest value) {
                //CLIENT SENDS A MESSAGE
                result += String.format("Hello %s ! ", value.getGreeting().getFirstName());
            }

            @Override
            public void onError(Throwable t) {
                //CLIENT SENDS AN ERROR

            }

            @Override
            public void onCompleted() {
                //CLIENT IS DONE
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());

                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestObserver = new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello " + value.getGreeting().getFirstName();
                GreetEveryoneResponse greetEveryoneResponse = GreetEveryoneResponse.newBuilder().setResult(result).build();
                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {

        //THIS IS USED TO KNOW IF THE REQUEST HAS BEEN TERMINATED BY THE SERVER AFTER DEADLINE IS EXCEEDED
        Context current = Context.current();

        try {

            for (int i = 0; i < 3; i++) {
                if (!current.isCancelled()) {
                    System.out.println("Sleep for100 ms");
                    Thread.sleep(100);
                }else {
                    return;
                }
            }
            System.out.println("Send response");
            responseObserver.onNext(GreetWithDeadlineResponse.newBuilder().setResult("Hello " + request.getGreeting()
                    .getFirstName()).build());
            responseObserver.onCompleted();

        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
