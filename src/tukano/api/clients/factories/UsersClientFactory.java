package tukano.api.clients.factories;


import java.net.URI;

import tukano.api.clients.grpc.GrpcUsersClient;
import tukano.api.clients.rest.RestUsersClient;
import tukano.api.discovery.Discovery;
import tukano.api.java.Users;


public class UsersClientFactory {

	public static Users getClient() {
		URI[] uris = Discovery.getInstance().knownUrisOf("users", 1);

		if (uris == null || uris.length == 0) {
			System.out.println("No server found for service Discover.");
			return null;
		}
		
		var serverURI = uris[0];
		if (serverURI.toString().endsWith("rest"))	
			return new RestUsersClient(serverURI);
		else
			return new GrpcUsersClient(serverURI);
		
	}
}
