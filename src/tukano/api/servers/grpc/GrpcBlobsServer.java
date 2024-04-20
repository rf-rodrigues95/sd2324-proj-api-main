/**
 * 
 */
package tukano.api.servers.grpc;

import java.net.InetAddress;
import java.util.logging.Logger;

import io.grpc.ServerBuilder;
import tukano.api.discovery.Discovery;
import tukano.api.java.Blobs;

/**
 * 
 */
public class GrpcBlobsServer {

	public static final int PORT = 8030;
	
	private static final String GRPC_CTX = "/grpc";
	private static final String SERVER_BASE_URI = "grpc://%s:%s%s";
	
	private static Logger Log = Logger.getLogger(GrpcBlobsServer.class.getName());
	
	public static void main(String[] args) throws Exception {
		//CHANGE AFTER UNCOMMENT,
	
		var stub = new GrpcBlobsServerStub();
		var server = ServerBuilder.forPort(PORT).addService(stub).build();
		var serverURI = String.format(SERVER_BASE_URI, InetAddress.getLocalHost().getHostAddress(), PORT, GRPC_CTX);

		Discovery.getInstance().announce("blobs", serverURI);
		
		Log.info(String.format("%s gRPC Server ready @ %s\n", Blobs.NAME, serverURI));
		server.start().awaitTermination();
		
	}
	
}
