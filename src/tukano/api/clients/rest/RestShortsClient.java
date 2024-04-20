package tukano.api.clients.rest;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import tukano.api.Short;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;
import tukano.api.rest.RestUsers;

public class RestShortsClient extends RestClient implements Shorts {

	final URI serverURI;
	final Client client;

	final WebTarget target;

	public RestShortsClient(URI serverURI) {
		super();
		
		this.serverURI = serverURI;
		
		this.client = ClientBuilder.newClient(config);
		target = client.target(serverURI).path(RestShorts.PATH);
	}

	// CREATE SHORT
	public Result<Short> clt_createShort(String userId, String password) {
		return super.toJavaResult(target.path(userId).queryParam(RestUsers.PWD, password)
				.request(MediaType.APPLICATION_JSON).post(Entity.json("")), Short.class);
	}

	@Override
	public Result<Short> createShort(String userId, String password) {
		return super.reTry(() -> createShort(userId, password));
	}

	// DELETE SHORT
	public Result<Void> clt_deleteShort(String shortId, String password) {
		return super.toJavaResult(target.path(shortId)
				.queryParam(RestUsers.PWD, password).request().delete(), Void.class);
	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		return super.reTry(() -> clt_deleteShort(shortId, password));
	}

	// GET SHORT
	public Result<Short> clt_getShort(String shortId) {
		return super.toJavaResult(target.path(shortId)
				.request().accept(MediaType.APPLICATION_JSON).get(), Short.class);
	}

	@Override
	public Result<Short> getShort(String shortId) {
		return super.reTry(() -> clt_getShort(shortId));
	}

	// GET SHORTS
	public Result<List<String>> clt_getShorts(String userId) {
		return super.toJavaResult(
				target.path(userId + RestShorts.SHORTS)
				.request().accept(MediaType.APPLICATION_JSON)
				.get(), new GenericType<List<String>>() {});
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
		return super.reTry(() -> clt_getShorts(userId));
	}

	// FOLLOW
	public Result<Void> clt_follow(String userId1, String userId2, boolean isFollowing, String password) {
		return super.toJavaResult(target.path(userId1 + "/" + userId2 + RestShorts.FOLLOWERS)
				.queryParam(RestShorts.PWD, password)
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.json(isFollowing)), Void.class);
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		return super.reTry(() -> clt_follow(userId1, userId2, isFollowing, password));
	}

	// FOLLOWERS
	public Result<List<String>> clt_followers(String userId, String password) {
		return super.toJavaResult(target.path(userId + RestShorts.FOLLOWERS)
				.queryParam(RestShorts.PWD, password)
				.request().accept(MediaType.APPLICATION_JSON)
				.get(), new GenericType<List<String>>() {});
	}

	@Override
	public Result<List<String>> followers(String userId, String password) {
		return super.reTry(() -> clt_followers(userId, password));
	}

	// LIKE
	public Result<Void> clt_like(String shortId, String userId, boolean isLiked, String password) {
		return super.toJavaResult(target.path(shortId + "/" + userId + RestShorts.LIKES)
				.queryParam(RestShorts.PWD, password).request(MediaType.APPLICATION_JSON)
				.post(Entity.json(isLiked)), Void.class);
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		return super.reTry(() -> clt_like(shortId, userId, isLiked, password));
	}

	// LIKES
	public Result<List<String>> clt_likes(String shortId, String password) {
		return super.toJavaResult(target.path(shortId + RestShorts.LIKES)
				.queryParam(RestShorts.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON).get(), new GenericType<List<String>>() {});
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {
		return super.reTry(() -> clt_likes(shortId, password));
	}

	// GET FEED
	public Result<List<String>> clt_getFeed(String userId, String password) {
		return super.toJavaResult(target.path(userId + RestShorts.FEED)
				.queryParam(RestShorts.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.get(), new GenericType<List<String>>() {});
	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		return super.reTry(() -> clt_getFeed(userId, password));
	}

	// EXTRA - REMOVE LIKES OF USER
	public Result<String> clt_removeLikesOfUser(String userId, String password) {
		return super.toJavaResult(target.path(userId + RestShorts.LIKES)
				.queryParam(RestShorts.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON).delete(), String.class);
	}

	@Override
	public Result<String> removeLikesOfUser(String userId, String password) {
		return super.reTry(() -> clt_removeLikesOfUser(userId, password));
	}

}
