package tukano.api.clients.factories;

import java.net.URI;

import tukano.api.clients.grpc.GrpcBlobsClient;
import tukano.api.clients.rest.RestBlobsClient;
import tukano.api.discovery.Discovery;
import tukano.api.java.Blobs;


public class BlobsClientFactory {

	public static Blobs getClient() {
		URI[] uris = Discovery.getInstance().knownUrisOf("blobs", 1);

		if (uris == null || uris.length == 0) {
			System.out.println("No server found for service Discover.");
			return null;
		}
		
		var serverURI = uris[0];
		if (serverURI.toString().endsWith("rest"))	
			return new RestBlobsClient(serverURI);
		else
			return new GrpcBlobsClient(serverURI);
		
	}
}
