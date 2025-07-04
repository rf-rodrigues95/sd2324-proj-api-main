package utils;

import tukano.api.User;
import tukano.api.Short;

import tukano.impl.grpc.generated_java.UsersProtoBuf.GrpcUser;

import tukano.impl.grpc.generated_java.ShortsProtoBuf.GrpcShort;

public class DataModelAdaptor {

	public static User GrpcUser_to_User( GrpcUser from )  {
		return new User( 
				from.getUserId(), 
				from.getPassword(), 
				from.getEmail(), 
				from.getDisplayName());
	}

	public static GrpcUser User_to_GrpcUser( User from )  {
		return GrpcUser.newBuilder()
				.setUserId( from.getUserId())
				.setPassword( from.getPwd())
				.setEmail( from.getEmail())
				.setDisplayName( from.getDisplayName())
				.build();
	}
	

	public static Short GrpcShort_to_Short( GrpcShort from )  {
		return new Short( 
				from.getShortId(), 
				from.getOwnerId(), 
				from.getBlobUrl(), 
				from.getTimestamp(),
				from.getTotalLikes());
	}

	public static GrpcShort Short_to_GrpcShort( Short from )  {
		return GrpcShort.newBuilder()
				.setShortId( from.getShortId())
				.setOwnerId( from.getOwnerId())
				.setBlobUrl( from.getBlobUrl())
				.setTimestamp( from.getTimestamp())
				.setTotalLikes(from.getTotalLikes())
				.build();
	}
}
