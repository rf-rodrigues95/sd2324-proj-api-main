package tukano.api.clients.factories;

import java.net.URI;

import tukano.api.clients.grpc.GrpcShortsClient;
import tukano.api.clients.rest.RestShortsClient;
import tukano.api.discovery.Discovery;
import tukano.api.java.Shorts;


public class ShortsClientFactory {

	public static Shorts getClient() {
		URI[] uris = Discovery.getInstance().knownUrisOf("shorts", 1);

		if (uris == null || uris.length == 0) {
			System.out.println("No server found for service Discover.");
			return null;
		}
		
		var serverURI = uris[0];
		if (serverURI.toString().endsWith("rest"))	
			return new RestShortsClient(serverURI);
		else
			return new GrpcShortsClient(serverURI);
		
	}
}
