package mypackage;//package java;

import com.test.grpc.bidir.BiDirServiceGrpc;
import com.test.grpc.bidir.HelloResponse;
import com.test.grpc.first.FirstServiceGrpc;
import com.test.grpc.first.HelloReply;
import com.test.grpc.first.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class Main {

  private final ManagedChannel channel;
  private final FirstServiceGrpc.FirstServiceBlockingStub blockingStub;
  private final BiDirServiceGrpc.BiDirServiceStub biDirServiceStub;

  public Main(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build());
  }

  Main(ManagedChannel channel) {
    this.channel = channel;
    blockingStub = FirstServiceGrpc.newBlockingStub(channel);
    biDirServiceStub= BiDirServiceGrpc.newStub(channel);
  }

  public static void main(String[] args) {
    Main main=new Main("localhost",50051);
    try {
      main.greet("andrey");
      main.biDirMethod();
    } catch (StatusRuntimeException e) {
      System.out.println(e.getMessage());
      return;
    }

  }

  /**
   * Say hello to server.
   */
  public void greet(String name) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      response = blockingStub.test(request);
      System.out.println(response.getMessage());
    } catch (StatusRuntimeException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  public void biDirMethod(){
    StreamObserver<com.test.grpc.bidir.HelloRequest> observer=biDirServiceStub.bidiHello(
            new StreamObserver<HelloResponse>() {
              @Override
              public void onNext(HelloResponse helloResponse) {
                  System.out.println("onNext from client - "+helloResponse.getMessage());
              }

              @Override
              public void onError(Throwable throwable) {
                  System.out.println("on error");
                  throwable.printStackTrace();
              }

              @Override
              public void onCompleted() {
                  System.out.println("on completed");
              }
            });
    for (int i=0; i<10; i++){
        String name="s"+i;
        System.out.println(name);
        observer.onNext(com.test.grpc.bidir.HelloRequest.newBuilder().setName(name).build());
    }
      observer.onCompleted();
      try {
          Thread.sleep(25000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }
}
