/**
 * 
 */
package tukano.api.servers.java;

import java.util.List;
import java.util.logging.Logger;

import tukano.api.User;
import tukano.api.clients.factories.ShortsClientFactory;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.java.Users;
import tukano.persistence.Hibernate;

/**
 * 
 */
public class JavaUsers implements Users {

	private static Logger Log = Logger.getLogger(JavaUsers.class.getName());

	@Override
	public Result<String> createUser(User user) {

		Log.info("createUser : " + user);

		// Check if user data is valid
		if (user.userId() == null || user.userId().isBlank() || user.pwd() == null || user.displayName() == null || user.email() == null) {
			Log.info("User object invalid.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		// Check if user already exists
		List<User> existingUsers = Hibernate.getInstance()
				.jpql("SELECT u FROM User u WHERE u.userId = '" + user.getUserId() + "'", User.class);

		if (!existingUsers.isEmpty()) {
			Log.info("User already exists.");
			return Result.error(ErrorCode.CONFLICT);
		}

		try {
			Hibernate.getInstance().persist(user);
			Log.info("User created: " + user.getUserId()); // debug del
			return Result.ok(user.userId());
		} catch (Exception e) {
			Log.severe("Error creating user: " + e.getMessage());
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

	}

	@Override
	public Result<User> getUser(String userId, String pwd) {

		Log.info("getUser : user = " + userId + "; pwd = " + pwd);

		// Check if user is valid
		if (userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		List<User> existingUsers = Hibernate.getInstance()
				.jpql("SELECT u FROM User u WHERE u.userId = '" + userId + "'", User.class);

		if (existingUsers.isEmpty()) {
			Log.info("User does not exist.");
			return Result.error(ErrorCode.NOT_FOUND);
		}

		User user = existingUsers.get(0);
		if (!user.pwd().equals(pwd)) {
			Log.info("Password is incorrect.");
			return Result.error(ErrorCode.FORBIDDEN);
		}

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String userId, String pwd, User user) {

		List<User> existingUsers = Hibernate.getInstance()
				.jpql("SELECT u FROM User u WHERE u.userId = '" + userId + "'", User.class);

		if (existingUsers.isEmpty()) {
			Log.info("User does not exist.");
			return Result.error(ErrorCode.NOT_FOUND);
		}

		User existingUser = existingUsers.get(0);
		if (!existingUser.pwd().equals(pwd)) {
			Log.info("Password is incorrect.");
			return Result.error(ErrorCode.FORBIDDEN);
		}

		if ((user.getUserId() != null && !(user.getUserId().isBlank()))
				&& !(user.getUserId().equals(existingUser.getUserId()))) {
			
			Log.info("User is different.");
			Log.info("Ricardo: " + existingUser + "///" + user);
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		updateUserFields(existingUser, user);

		try {
			Hibernate.getInstance().update(existingUser);
			Log.info("User updated: " + userId);
			return Result.ok(existingUser);
		} catch (Exception e) {
			Log.severe("Error updating user: " + e.getMessage());
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

	}

	@Override
	public Result<User> deleteUser(String userId, String pwd) {
		var client = ShortsClientFactory.getClient();

		Result<String> result = client.removeLikesOfUser(userId, pwd);
		if (!result.isOK())
			return Result.error(result.error());
		
		List<User> existingUsers = Hibernate.getInstance()
				.jpql("SELECT u FROM User u WHERE u.userId = '" + userId + "'", User.class);
		
		User user = existingUsers.get(0);
		
		Hibernate.getInstance().delete(user);
		Log.info("User deleted: " + result.value());

		return Result.ok(user);
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		if (pattern == null) {
			Log.info("Invalid pattern.");
			return Result.error(ErrorCode.BAD_REQUEST);
		}

		try {
			List<User> found = Hibernate.getInstance().jpql(
					"SELECT u FROM User u WHERE LOWER(u.userId) LIKE '%" + pattern.toLowerCase() + "%'", User.class);

			Log.info("There were found " + found.size() + " matching users.");
			return Result.ok(found);
		} catch (Exception e) {
			Log.severe("Error searching users: " + e.getMessage());
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}

	}

	private void updateUserFields(User existingUser, User newUser) {
		if (newUser.getPwd() != null && !(newUser.getPwd().isBlank()) )
			existingUser.setPwd(newUser.getPwd());

		if (newUser.getDisplayName() != null && !(newUser.getDisplayName().isBlank()))
			existingUser.setDisplayName(newUser.getDisplayName());

		if (newUser.getEmail() != null && !(newUser.getEmail().isBlank()))
			existingUser.setEmail(newUser.getEmail());
	}

}
