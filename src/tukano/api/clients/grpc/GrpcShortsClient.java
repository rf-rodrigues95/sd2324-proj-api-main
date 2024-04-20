package tukano.api.clients.grpc;

import static utils.DataModelAdaptor.GrpcShort_to_Short;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import io.grpc.ManagedChannelBuilder;

import tukano.api.java.Result;
import tukano.api.Short;
import tukano.api.User;
import tukano.api.java.Shorts;
import tukano.impl.grpc.generated_java.ShortsGrpc;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.CreateShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.DeleteShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetShortsArgs;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.FollowersArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikeArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.LikesArgs;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.GetFeedArgs;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.RemoveLikesOfUserArgs;


public class GrpcShortsClient extends GrpcClient implements Shorts {

	private static final long GRPC_REQUEST_TIMEOUT = 5000;
	final ShortsGrpc.ShortsBlockingStub stub;

	public GrpcShortsClient(URI serverURI) {
		var channel = ManagedChannelBuilder.forAddress(serverURI.getHost(), serverURI.getPort()).usePlaintext().build();
		stub = ShortsGrpc.newBlockingStub(channel).withDeadlineAfter(GRPC_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@Override
	public Result<Short> createShort(String userId, String password) {
		return super.toJavaResult(() -> {
			var res = stub.createShort(CreateShortArgs.newBuilder().setUserId(userId).setPassword(password).build());

			return GrpcShort_to_Short(res.getValue());
		});
	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		stub.deleteShort(DeleteShortArgs.newBuilder().setShortId(shortId).setPassword(password).build());

		return Result.ok();
	}

	@Override
	public Result<Short> getShort(String shortId) {
		return super.toJavaResult(() -> {
			var res = stub.getShort(GetShortArgs.newBuilder().setShortId(shortId).build());
			return GrpcShort_to_Short(res.getValue());
		});
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
		return super.toJavaResult(() -> {
			var res = stub.getShorts(GetShortsArgs.newBuilder().setUserId(userId).build());

			List<String> found = new ArrayList<>(res.getShortIdList());

			return found;
		});
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		stub.follow(FollowArgs.newBuilder()
			.setUserId1(userId1).setUserId2(userId2)
				.setIsFollowing(isFollowing).setPassword(password).build());

		return Result.ok();
	}

	@Override
	public Result<List<String>> followers(String userId, String password) {
		return super.toJavaResult(() -> {
			var res = stub.followers(FollowersArgs.newBuilder().setUserId(userId).setPassword(password).build());

			List<String> found = new ArrayList<>(res.getUserIdList());

			return found;
		});
	}
	
	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		stub.like(LikeArgs.newBuilder()
				.setShortId(shortId).setUserId(userId)
					.setIsLiked(isLiked).setPassword(password).build());

		return Result.ok();
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {
		return super.toJavaResult(() -> {
			var res = stub.likes(LikesArgs.newBuilder().setShortId(shortId).setPassword(password).build());

			List<String> found = new ArrayList<>(res.getUserIdList());

			return found;
		});
	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		return super.toJavaResult(() -> {
			var res = stub.getFeed(GetFeedArgs.newBuilder().setUserId(userId).setPassword(password).build());

			List<String> found = new ArrayList<>(res.getShortIdList());

			return found;
		});
	}

	@Override
	public Result<String> removeLikesOfUser(String userId, String password) {
		return super.toJavaResult(() -> {
			var res = stub.removeLikesOfUser(RemoveLikesOfUserArgs.newBuilder().setUserId(userId).setPassword(password).build());

			return res.getUserId();
		});
		
		/*
		return super.toJavaResult(() -> {
			var res = stub.getFeed(GetFeedArgs.newBuilder().setUserId(userId).setPassword(password).build());

			List<String> found = new ArrayList<>(res.getShortIdList());

			return found;
		});
		*/
	}

	/*
	@Override
	public Result<User> removeLikesOfUser(String userId, String password) {
		return super.toJavaResult(() -> {
			var res = stub.getShort(removeLikesOfUserArgs.newBuilder().setUserId(userId).build());
			return GrpcUser_to_User(res.getValue());
		});
	}
	*/
}
