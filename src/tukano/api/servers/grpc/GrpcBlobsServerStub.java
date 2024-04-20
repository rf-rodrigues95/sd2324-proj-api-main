package tukano.api.servers.grpc;

import com.google.protobuf.ByteString;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import tukano.api.java.Result;
import tukano.api.java.Blobs;
import tukano.impl.grpc.generated_java.BlobsGrpc;

import tukano.impl.grpc.generated_java.BlobsProtoBuf.UploadArgs;
import tukano.impl.grpc.generated_java.BlobsProtoBuf.UploadResult;
import tukano.impl.grpc.generated_java.BlobsProtoBuf.DownloadArgs;
import tukano.impl.grpc.generated_java.BlobsProtoBuf.DownloadResult;

import tukano.api.servers.java.JavaBlobs;

public class GrpcBlobsServerStub implements BlobsGrpc.AsyncService, BindableService {

	final Blobs impl;

	public GrpcBlobsServerStub() {
		this.impl = new JavaBlobs();
	}

	@Override
	public final ServerServiceDefinition bindService() {
		return BlobsGrpc.bindService(this);
	}

	@Override
	public void upload(UploadArgs request, StreamObserver<UploadResult> responseObserver) {
		
		var res = impl.upload(request.getBlobId(), request.getData().toByteArray());
		if (!res.isOK())
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext(UploadResult.getDefaultInstance());
			responseObserver.onCompleted();
		}
	}

	@Override
	public void download(DownloadArgs request, StreamObserver<DownloadResult> responseObserver) {
		var res = impl.download(request.getBlobId());
		if (!res.isOK())
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			DownloadResult downResult = DownloadResult.newBuilder().setChunk(ByteString.copyFrom(res.value())).build();

			responseObserver.onNext(downResult);
			responseObserver.onCompleted();
		}
	}

	protected static Throwable errorCodeToStatus(Result.ErrorCode error) {
		var status = switch (error) {
		case NOT_FOUND -> io.grpc.Status.NOT_FOUND;
		case CONFLICT -> io.grpc.Status.ALREADY_EXISTS;
		case FORBIDDEN -> io.grpc.Status.PERMISSION_DENIED;
		case NOT_IMPLEMENTED -> io.grpc.Status.UNIMPLEMENTED;
		case BAD_REQUEST -> io.grpc.Status.INVALID_ARGUMENT;
		default -> io.grpc.Status.INTERNAL;
		};

		return status.asException();
	}
}
