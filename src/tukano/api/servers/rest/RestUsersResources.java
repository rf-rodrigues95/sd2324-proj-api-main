/**
 * 
 */
package tukano.api.servers.rest;

import java.util.List;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;
import tukano.api.servers.java.JavaUsers;

/**
 * 
 */
public class RestUsersResources implements RestUsers {

	final Users impl;

	public RestUsersResources() {
		this.impl = new JavaUsers();
	}

	@Override
	public String createUser(User user) {
		return resultOrThrow(impl.createUser(user));
	}

	@Override
	public User getUser(String userId, String pwd) {
		return resultOrThrow(impl.getUser(userId, pwd));
	}

	@Override
	public User updateUser(String name, String pwd, User user) {
		return resultOrThrow(impl.updateUser(name, pwd, user));
	}

	@Override
	public User deleteUser(String name, String pwd) {
		return resultOrThrow(impl.deleteUser(name, pwd));
	}

	@Override
	public List<User> searchUsers(String pattern) {
		return resultOrThrow(impl.searchUsers(pattern));
	}

	/**
	 * Given a Result<T>, either returns the value, or throws the JAX-WS Exception
	 * matching the error code...
	 */
	protected <T> T resultOrThrow(Result<T> result) {
		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(statusCodeFrom(result));
	}

	/**
	 * Translates a Result<T> to a HTTP Status code
	 */
	private static Status statusCodeFrom(Result<?> result) {
		return switch (result.error()) {
		case CONFLICT -> Status.CONFLICT;
		case NOT_FOUND -> Status.NOT_FOUND;
		case FORBIDDEN -> Status.FORBIDDEN;
		case BAD_REQUEST -> Status.BAD_REQUEST;
		case INTERNAL_ERROR -> Status.INTERNAL_SERVER_ERROR;
		case NOT_IMPLEMENTED -> Status.NOT_IMPLEMENTED;
		case OK -> result.value() == null ? Status.NO_CONTENT : Status.OK;
		default -> Status.INTERNAL_SERVER_ERROR;
		};
	}
}
