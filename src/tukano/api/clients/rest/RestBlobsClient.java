/**
 * 
 */
package tukano.api.clients.rest;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.rest.RestBlobs;

/**
 * 
 */
public class RestBlobsClient extends RestClient implements Blobs{
	
	final URI serverURI;
	final Client client;

	final WebTarget target;
	
	public RestBlobsClient( URI serverURI ) {
		super();
		
		this.serverURI = serverURI;
		
		this.client = ClientBuilder.newClient(config);
		target = client.target( serverURI ).path( RestBlobs.PATH );
	}

	public Result<Void> clt_upload(String blobId, byte[] bytes) {
		return super.toJavaResult(target.path(blobId)
	            .request()
	            .post(Entity.entity(bytes, "application/octet-stream")), Void.class);
	}
	
	@Override
	public Result<Void> upload(String blobId, byte[] bytes) {
		return super.reTry(() -> clt_upload(blobId, bytes));
	}

	
	public Result<byte[]> clt_download(String blobId) {
		return super.toJavaResult(target.path(blobId)
                .request()
                .get(), byte[].class);
	}
	
	@Override
	public Result<byte[]> download(String blobId) {
		return super.reTry(() -> clt_download(blobId));
	}
	
}
