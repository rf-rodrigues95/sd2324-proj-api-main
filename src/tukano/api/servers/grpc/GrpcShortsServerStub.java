package tukano.api.servers.grpc;

//import static utils.DataModelAdaptor.User_to_GrpcUser;
import static utils.DataModelAdaptor.Short_to_GrpcShort;

//import static utils.DataModelAdapator.GrpcUser_to_User;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;

import tukano.api.java.Result;
import tukano.api.java.Shorts;

import tukano.impl.grpc.generated_java.ShortsGrpc;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.CreateShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.CreateShortResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.DeleteShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.DeleteShortResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortsArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortsResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowersArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowersResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikeArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikeResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikesArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikesResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetFeedArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetFeedResult;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.RemoveLikesOfUserArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.RemoveLikesOfUserResult;

import tukano.api.servers.java.JavaShorts;

public class GrpcShortsServerStub implements ShortsGrpc.AsyncService, BindableService {

	final Shorts impl;

	public GrpcShortsServerStub() {
		this.impl = new JavaShorts();
	}

	@Override
	public final ServerServiceDefinition bindService() {
		return ShortsGrpc.bindService(this);
	}

	@Override
	public void createShort(CreateShortArgs request, StreamObserver<CreateShortResult> responseObserver) {
		var res = impl.createShort( request.getUserId(), request.getPassword());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
    		
    		CreateShortResult createShortResult = CreateShortResult.newBuilder()
    	            .setValue(Short_to_GrpcShort(res.value()))
    	            .build();
    		
    		responseObserver.onNext( createShortResult);
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void deleteShort(DeleteShortArgs request, StreamObserver<DeleteShortResult> responseObserver) {
		var res = impl.deleteShort( request.getShortId(), request.getPassword());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
    		responseObserver.onNext(DeleteShortResult.getDefaultInstance());
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void getShort(GetShortArgs request, StreamObserver<GetShortResult> responseObserver) {
		var res = impl.getShort( request.getShortId());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
    		GetShortResult getShortResult = GetShortResult.newBuilder()
    	            .setValue(Short_to_GrpcShort(res.value()))
    	            .build();
    		
    		responseObserver.onNext( getShortResult);
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void getShorts(GetShortsArgs request, StreamObserver<GetShortsResult> responseObserver) {
		var res = impl.getShorts( request.getUserId());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
    		
    		 GetShortsResult getShortsResult = GetShortsResult.newBuilder()
    		            .addAllShortId(res.value()) 
    		            .build();
    		
    		responseObserver.onNext( getShortsResult);
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void follow(FollowArgs request, StreamObserver<FollowResult> responseObserver) {
		var res = impl.follow( request.getUserId1(), request.getUserId2(),
				request.getIsFollowing(), request.getPassword());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
    		responseObserver.onNext(FollowResult.getDefaultInstance());
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void followers(FollowersArgs request, StreamObserver<FollowersResult> responseObserver) {
		var res = impl.followers( request.getUserId(), request.getPassword());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
    		FollowersResult followersResult = FollowersResult.newBuilder()
    		            .addAllUserId(res.value()) 
    		            .build();
    		
    		responseObserver.onNext( followersResult);
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void like(LikeArgs request, StreamObserver<LikeResult> responseObserver) {
		var res = impl.like( request.getShortId(), request.getUserId(),
				request.getIsLiked(), request.getPassword());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
    		responseObserver.onNext(LikeResult.getDefaultInstance());
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void likes(LikesArgs request, StreamObserver<LikesResult> responseObserver) {
		var res = impl.likes( request.getShortId(), request.getPassword());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {	
    		LikesResult likesResult = LikesResult.newBuilder()
    		            .addAllUserId(res.value()) 
    		            .build();
    		
    		responseObserver.onNext( likesResult);
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void getFeed(GetFeedArgs request, StreamObserver<GetFeedResult> responseObserver) {
		var res = impl.getFeed( request.getUserId(), request.getPassword());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {
    		
    		GetFeedResult feedResult = GetFeedResult.newBuilder()
    		            .addAllShortId(res.value()) 
    		            .build();
    		
    		responseObserver.onNext( feedResult);
			responseObserver.onCompleted();
    	}
	}

	@Override
	public void removeLikesOfUser(RemoveLikesOfUserArgs request, StreamObserver<RemoveLikesOfUserResult> responseObserver) {
		var res = impl.removeLikesOfUser( request.getUserId(), request.getPassword());
    	if( ! res.isOK() ) 
    		responseObserver.onError(errorCodeToStatus(res.error()));
    	else {

    		
    		RemoveLikesOfUserResult result = RemoveLikesOfUserResult.newBuilder()
    		           .setUserId(res.value()) 
    		            .build();
    		
    		responseObserver.onNext( result );
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
