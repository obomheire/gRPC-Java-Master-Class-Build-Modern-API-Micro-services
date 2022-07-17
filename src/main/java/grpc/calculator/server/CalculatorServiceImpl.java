package grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {

        //Extract the field that is needed
        int number_1 = request.getFirstNumber();
        int number_2 = request.getSecondNumber();

        //Build the response
        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumResult(number_1 + number_2)
                .build();

        //Send the response
        responseObserver.onNext(sumResponse);

        //Complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Integer number = request.getNumber();
        Integer divisor = 2;

        while (number > 1) {
            if (number % divisor == 0) {
                number = number / divisor;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                                .setPrimeFactor(divisor).build());
            }else {
                divisor++;
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {

        StreamObserver<ComputeAverageRequest> requestObserver = new StreamObserver<ComputeAverageRequest>() {

            //RUNNING SUM AND COUNT
            int sum = 0;
            int count = 0;
            @Override
            public void onNext(ComputeAverageRequest value) {
                //INCREAMENT THE SUM
                sum += value.getNumber();
                //INCREAMENT THE COUNT
                count++;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                //COMPUTE AVERAGE
                double average = (double) sum / count;
                responseObserver.onNext(ComputeAverageResponse.newBuilder().setAverage(average).build());

                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }

    @Override
    public StreamObserver<FindMaximumRequest> findMaximum(StreamObserver<FindMaximumResponse> responseObserver) {
        return new StreamObserver<FindMaximumRequest>() {

            int currentMaximum = 0;

            @Override
            public void onNext(FindMaximumRequest value) {
                int currentNumber = value.getNumber();

                if (currentNumber > currentMaximum) {
                    currentMaximum = currentNumber;
                    responseObserver.onNext(FindMaximumResponse.newBuilder().setMaximum(currentNumber).build());
                }else {
                    //Do Nothing
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(FindMaximumResponse.newBuilder().setMaximum(currentMaximum).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {

        Integer number = request.getNumber();
        if (number >= 0) {
            double numberRoot = Math.sqrt(number);
            responseObserver.onNext(SquareRootResponse.newBuilder().setNumberRoot(numberRoot).build());

            responseObserver.onCompleted();
        }else {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Number must be greater than 0")
                            .augmentDescription("Number is " + number).asRuntimeException()
            );
        }
    }
}
