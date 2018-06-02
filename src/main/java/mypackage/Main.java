package mypackage;//package java;

import com.test.grpc.first.FirstServiceGrpc;
import com.test.grpc.first.HelloReply;
import com.test.grpc.first.HelloRequest;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;


public class Main {

  private ManagedChannel channel;
  private FirstServiceGrpc.FirstServiceBlockingStub blockingStub;

  public Main(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  Main(ManagedChannel channel) {
    this.channel = channel;
    Channel channel1=ClientInterceptors.intercept(channel, new MyClientInterceptor());
    blockingStub = FirstServiceGrpc.newBlockingStub(channel1);
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
      Metadata metadata=new Metadata();
      metadata.put(Metadata.Key.of("key", Metadata.ASCII_STRING_MARSHALLER), "key-value");
      blockingStub= MetadataUtils.attachHeaders(blockingStub, metadata);

      response = blockingStub.test(request);
      System.out.println(response.getMessage());
    } catch (StatusRuntimeException e) {
      System.out.println(e.getMessage());
      return;
    }
  }
}
