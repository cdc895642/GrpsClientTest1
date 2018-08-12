package mypackage;//package java;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
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
import javax.annotation.Nullable;


public class Main {

  private ManagedChannel channel;
  private FirstServiceGrpc.FirstServiceBlockingStub blockingStub;
  private FirstServiceGrpc.FirstServiceFutureStub futureStub;

  public Main(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
  }

  Main(ManagedChannel channel) {
    this.channel = channel;
    Channel channel1 = ClientInterceptors.intercept(channel, new MyClientInterceptor());
    blockingStub = FirstServiceGrpc.newBlockingStub(channel1);
    futureStub = FirstServiceGrpc.newFutureStub(channel1);
  }

  public static void main(String[] args) {
    Main main = new Main("localhost", 50051);
    try {
//      main.blockingGreet("andrey");
      main.futureGreet("andrey");
    } catch (StatusRuntimeException e) {
      System.out.println(e.getMessage());
      return;
    }

  }

  public void futureGreet(String name) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    ListenableFuture<HelloReply> response = futureStub.test(request);
    boolean done=false;
    Futures.addCallback(response, new FutureCallback<HelloReply>() {
      @Override
      public void onSuccess(@Nullable HelloReply helloReply) {
        System.out.println(helloReply.getMessage());
      }

      @Override
      public void onFailure(Throwable throwable) {
        throwable.printStackTrace();
      }
    });
    while (!done){
      try {
        Thread.sleep(60*1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("not yet done");
    }

  }

  /**
   * Say hello to server.
   */
  public void blockingGreet(String name) {
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      Metadata metadata = new Metadata();
      metadata.put(Metadata.Key.of("key", Metadata.ASCII_STRING_MARSHALLER), "key-value");
      blockingStub = MetadataUtils.attachHeaders(blockingStub, metadata);

      response = blockingStub.test(request);
      System.out.println(response.getMessage());
    } catch (StatusRuntimeException e) {
      System.out.println(e.getMessage());
      return;
    }
  }
}
