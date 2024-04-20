package tukano.api.clients.grpc;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannelBuilder;
import tukano.api.java.Blobs;
import tukano.api.java.Result;

import tukano.impl.grpc.generated_java.BlobsGrpc;

import tukano.impl.grpc.generated_java.BlobsProtoBuf.UploadArgs;
import tukano.impl.grpc.generated_java.BlobsProtoBuf.DownloadArgs;


public class GrpcBlobsClient extends GrpcClient implements Blobs {

	private static final long GRPC_REQUEST_TIMEOUT = 5000;
	final BlobsGrpc.BlobsBlockingStub stub;
	

	public GrpcBlobsClient(URI serverURI) {
		var channel = ManagedChannelBuilder.forAddress(serverURI.getHost(), serverURI.getPort())
				.usePlaintext().build();
		stub = BlobsGrpc.newBlockingStub( channel ).withDeadlineAfter(GRPC_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
	}


	@Override
	public Result<Void> upload(String blobId, byte[] bytes) {
			stub.upload(UploadArgs.newBuilder()
					.setBlobId(blobId)
					.setData(ByteString.copyFrom(bytes))
					.build());
				
			return Result.ok();				
	}

	@Override
	public Result<byte[]> download(String blobId) {
		return super.toJavaResult(() -> {
			 var response = stub.download(DownloadArgs.newBuilder().setBlobId(blobId).build());
			 
			 ByteString ch = response.next().getChunk();
			 
			 byte[] downBytes = ch.toByteArray();
			 
			 
			 return downBytes;
	    });
	}	

}
