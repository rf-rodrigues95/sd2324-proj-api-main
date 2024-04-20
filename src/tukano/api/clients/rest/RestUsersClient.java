/**
 * 
 */
package tukano.api.clients.rest;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import tukano.api.rest.RestUsers;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Users;

/**
 * 
 */
public class RestUsersClient extends RestClient implements Users{

	final URI serverURI;
	final Client client;

	final WebTarget target;
	
	public RestUsersClient( URI serverURI ) {
		super();
		
		this.serverURI = serverURI;
		
		this.client = ClientBuilder.newClient(config);
		target = client.target( serverURI ).path( RestUsers.PATH );
	}
	
	//CREATE USER
	public Result<String> clt_createUser(User user) {
		return super.toJavaResult(target.request()
				.post(Entity.entity(user, MediaType.APPLICATION_JSON)), String.class );
	}
	
	@Override
    public Result<String> createUser(User user) {
    	return super.reTry( () -> clt_createUser(user));
    }
	
	
	//GET USER
	public Result<User> clt_getUser(String name, String pwd) {
		return super.toJavaResult(target.path( name )
				.queryParam(RestUsers.PWD, pwd).request()
				.accept(MediaType.APPLICATION_JSON)
				.get(), User.class );
	}
	
	@Override
    public Result<User> getUser(String name, String pwd) {
    	return super.reTry( () -> clt_getUser(name, pwd));
    }
	
	
	//UPDATE USER
	public Result<User> clt_updateUser(String userId, String password, User user) {
		return super.toJavaResult(target.path( userId )
				.queryParam(RestUsers.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON)), User.class );
	}
	
	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		return super.reTry( () -> clt_updateUser(userId, password, user));
	}
	
	
	//DELETE USER
	public Result<User> clt_deleteUser(String userId, String password) {
		return super.toJavaResult(target.path( userId )
				.queryParam(RestUsers.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.delete(), User.class );
	}
	@Override
	public Result<User> deleteUser(String userId, String password) {
		return super.reTry( () -> clt_deleteUser(userId, password));
	}

	
	//SEARCH USERS
	public Result<List<User>> clt_searchUsers(String pattern) {
		return super.toJavaResult(target.queryParam(RestUsers.QUERY, pattern)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(), new GenericType<List<User>>() {});
	}
	
	@Override
	public Result<List<User>> searchUsers(String pattern) {
		return super.reTry( () -> clt_searchUsers(pattern));		
	}
	

}
