package mypackage;//package java;

import com.test.grpc.first.FirstServiceGrpc;
import com.test.grpc.first.HelloReply;
import com.test.grpc.first.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


public class Main {

  private final ManagedChannel channel;
  private final FirstServiceGrpc.FirstServiceBlockingStub blockingStub;

  public Main(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).build());
  }

  Main(ManagedChannel channel) {
    this.channel = channel;
    blockingStub = FirstServiceGrpc.newBlockingStub(channel);
  }

  public static void main(String[] args) {
    Main main=new Main("localhost",50051);
    try {
      main.greet("andrey");
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
}
