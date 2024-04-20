/**
 * 
 */
package tukano.api.servers.java;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import tukano.api.discovery.Discovery;
import tukano.api.Follow;
import tukano.api.ShortLikes;
import tukano.api.User;
import tukano.api.clients.factories.UsersClientFactory;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Result.ErrorCode;

import tukano.persistence.Hibernate;

/**
 * 
 */
public class JavaShorts implements Shorts {

	private static Logger Log = Logger.getLogger(JavaShorts.class.getName());

	private UUID shortId;

	@Override
	public Result<Short> createShort(String userId, String password) {

		Log.info("createShort from owner: " + userId);

		// Check if user data is valid
		if (userId.isBlank() || password.isBlank()) {
			Log.info("User object invalid.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}
		
		var client = UsersClientFactory.getClient();

		var result = client.getUser(userId, password);
		if (!result.isOK())
			return Result.error(result.error());
		
		try {

			shortId = UUID.randomUUID();
			
			String blobURL = getBlobServiceURL() + "/blobs/" + shortId;
			Short st = new Short(shortId.toString(), userId, blobURL);

			Hibernate.getInstance().persist(st);

			Log.info("Short created from: " + st.getOwnerId()); // debug del return
			return Result.ok(st);
		} catch (Exception e) {
			Log.severe("Error creating short: " + e.getMessage());
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

	}

	private String getBlobServiceURL() {
		try {
			URI[] uris;

			uris = Discovery.getInstance().knownUrisOf("blobs", -1);

			return uris[0].toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {

		Result<Short> shortResult = getShort(shortId);
		if (!shortResult.isOK())
			return Result.error(ErrorCode.NOT_FOUND);

		var client = UsersClientFactory.getClient();

		var result = client.getUser(shortResult.value().getOwnerId(), password);
		if (!result.isOK())
			return Result.error(ErrorCode.FORBIDDEN);

		try {
			Hibernate.getInstance().delete(shortResult.value());
			Log.info("Short deleted: " + shortResult.value());

			List<ShortLikes> shortLikes = Hibernate.getInstance().jpql(
					"SELECT u FROM ShortLikes u WHERE u.shortId = '" + shortResult.value().getShortId() + "'",
					ShortLikes.class);

			for (ShortLikes sh : shortLikes) {
				Log.info("Short Likes deleted: " + sh.getShortId());
				Hibernate.getInstance().delete(sh);
			}

		} catch (Exception e) {
			Log.severe("Error deleting short: " + e.getMessage());
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

		return Result.ok();
	}

	@Override
	public Result<Short> getShort(String shortId) {
		Log.info("getShort : user = " + shortId);

		if (shortId == null) {
			Log.info("ShortId null.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		List<Short> existingShorts = Hibernate.getInstance()
				.jpql("SELECT u FROM Short u WHERE u.shortId = '" + shortId + "'", Short.class);

		if (existingShorts.isEmpty()) {
			Log.info("Short does not exist.");
			return Result.error(ErrorCode.NOT_FOUND);
		}

		Short st = existingShorts.get(0);
		return Result.ok(st);
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
		Log.info("getShorts : user = " + userId);

		if (userId == null) {
			Log.info("userId null.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		var client = UsersClientFactory.getClient();

		var result = client.getUser(userId, userId);
		if (result.error() == ErrorCode.NOT_FOUND)
			return Result.error(result.error());

		List<String> existingShorts = Hibernate.getInstance()
				.jpql("SELECT u.shortId FROM Short u WHERE u.ownerId = '" + userId + "'", String.class);

		return Result.ok(existingShorts);
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {

		Log.info("follow: " + userId1 + " > " + userId2);

		var client = UsersClientFactory.getClient();

		var result1 = client.getUser(userId1, password);
		if (!result1.isOK())
			return Result.error(result1.error());

		var result2 = client.getUser(userId2, password);
		if (result2.error() == ErrorCode.NOT_FOUND)
			return Result.error(result2.error());

		List<Follow> user1Follows = Hibernate.getInstance()
				.jpql("SELECT u FROM Follow u WHERE u.userId = '" + result1.value().getUserId() + "'", Follow.class);

		Follow toDel = null;
		for (Follow f : user1Follows) {
			if (isFollowing && f.getFollowed().equals(userId2))
				return Result.error(ErrorCode.CONFLICT);
			else if (!isFollowing) {
				if (f.getFollowed().equals(userId2))
					toDel = f;
			}
		}

		if (!isFollowing && toDel == null)
			return Result.ok();
		// return Result.error(ErrorCode.CONFLICT);

		try {
			if (isFollowing) {
				Follow fol = new Follow(userId1, userId2);
				Hibernate.getInstance().persist(fol);
				Log.info(userId1 + " is following " + userId2);
			} else {
				Hibernate.getInstance().delete(toDel);
				Log.info(userId2 + " unfollowed " + userId2);
			}

		} catch (Exception e) {
			Log.severe("Error updating user: " + e.getMessage());
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

		return Result.ok();

	}

	@Override
	public Result<List<String>> followers(String userId, String password) {
		var client = UsersClientFactory.getClient();

		var result1 = client.getUser(userId, password);
		if (!result1.isOK())
			return Result.error(result1.error());

		List<String> userFollowers = Hibernate.getInstance()
				.jpql("SELECT u.userId FROM Follow u WHERE u.followed = '" + userId + "'", String.class);

		return Result.ok(userFollowers);
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		var client = UsersClientFactory.getClient();

		var userResult = client.getUser(userId, password);
		if (!userResult.isOK())
			return Result.error(userResult.error());

		var shortResult = getShort(shortId);
		if (!shortResult.isOK())
			return Result.error(shortResult.error());

		List<ShortLikes> shortLikes = Hibernate.getInstance().jpql(
				"SELECT u FROM ShortLikes u WHERE u.shortId = '" + shortResult.value().getShortId() + "'",
				ShortLikes.class);

		ShortLikes toDel = null;
		for (ShortLikes f : shortLikes) {
			if (isLiked && f.getUserId().equals(userId))
				return Result.error(ErrorCode.CONFLICT);
			else if (!isLiked) {
				if (f.getUserId().equals(userId))
					toDel = f;
			}
		}

		if (!isLiked && toDel == null)
			return Result.error(ErrorCode.CONFLICT);

		try {
			if (isLiked) {
				ShortLikes lik = new ShortLikes(shortId, userId);
				Hibernate.getInstance().persist(lik);

				shortResult.value().setTotalLikes(shortResult.value().getTotalLikes() + 1);
				Hibernate.getInstance().update(shortResult.value());
				Log.info(shortId + " liked by " + userId);
			} else {
				Hibernate.getInstance().delete(toDel);

				shortResult.value().setTotalLikes(Math.max(0, shortResult.value().getTotalLikes() - 1));
				Hibernate.getInstance().update(shortResult.value());
				Log.info(shortId + " disliked by " + userId);
			}
		} catch (Exception e) {
			Log.severe("Error updating user: " + e.getMessage());
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

		return Result.ok();
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {

		var resultSt = getShort(shortId);
		if (!resultSt.isOK())
			return Result.error(resultSt.error());

		// verify if given password is valid
		var client = UsersClientFactory.getClient();
		var resultUs = client.getUser(resultSt.value().getOwnerId(), password);
		if (!resultUs.isOK())
			return Result.error(ErrorCode.FORBIDDEN);

		List<String> existingLikes = Hibernate.getInstance()
				.jpql("SELECT u.userId FROM ShortLikes u WHERE u.shortId = '" + shortId + "'", String.class);

		return Result.ok(existingLikes);

	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		Log.info("Get Feed: " + userId);

		var client = UsersClientFactory.getClient();

		var result1 = client.getUser(userId, password);
		if (!result1.isOK())
			return Result.error(result1.error());

		List<String> targets = Hibernate.getInstance()
				.jpql("SELECT u.followed FROM Follow u WHERE u.userId = '" + userId + "'", String.class);

		targets.add(userId);

		List<Short> allShorts = new ArrayList<>();
		for (String us : targets) {

			List<Short> existingShorts = Hibernate.getInstance()
					.jpql("SELECT u FROM Short u WHERE u.ownerId = '" + us + "'", Short.class);

			allShorts.addAll(existingShorts);
		}

		allShorts.sort(Comparator.comparing(Short::getTimestamp).reversed());
		List<String> feedShorts = allShorts.stream().map(Short::getShortId).collect(Collectors.toList());

		return Result.ok(feedShorts);
	}

	@Override
	public Result<String> removeLikesOfUser(String userId, String password) {
		var client = UsersClientFactory.getClient();
		var result = client.getUser(userId, password);
		if (!result.isOK())
			return Result.error(result.error());

		// they are in the table, they exist.
		List<Short> existingShorts = Hibernate.getInstance()
				.jpql("SELECT u FROM Short u WHERE u.ownerId = '" + userId + "'", Short.class);

		// All verifications are done in deleteShort
		for (Short sh : existingShorts) {
			Hibernate.getInstance().delete(sh);

			List<ShortLikes> shortLikes = Hibernate.getInstance()
					.jpql("SELECT u FROM ShortLikes u WHERE u.shortId = '" + sh.getShortId() + "'", ShortLikes.class);

			for (ShortLikes lk : shortLikes) {
				Log.info("Short Likes deleted: " + lk.getShortId());
				Hibernate.getInstance().delete(sh);
			}
		}

		List<ShortLikes> likedShorts = Hibernate.getInstance()
				.jpql("SELECT u FROM ShortLikes u WHERE u.userId = '" + userId + "'", ShortLikes.class);

		for (ShortLikes sh : likedShorts) {
			var res = getShort(sh.getShortId());
			if (res.isOK()) {
				Short shResult = res.value();
				shResult.setTotalLikes(shResult.getTotalLikes() - 1);
				Hibernate.getInstance().update(shResult);

				Hibernate.getInstance().delete(sh);

			}

		}

		return Result.ok(userId);
	}

}
