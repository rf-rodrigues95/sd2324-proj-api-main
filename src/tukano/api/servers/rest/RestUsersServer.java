package tukano.api.servers.rest;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import tukano.api.discovery.Discovery;

public class RestUsersServer {

	private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 8090;
	public static final String SERVICE = "users";
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";
	
	public static final int DISCOVERY_ANNOUNCE_PERIOD = 5000;

	public static void main(String[] args) {
		try {

			ResourceConfig config = new ResourceConfig();
			config.register(  RestUsersResources.class );
				
			String ip = InetAddress.getLocalHost().getHostAddress();
			String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
		
			Discovery.getInstance().announce("users", serverURI);
			
			JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

			Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));
			
			
			// More code can be executed here...
		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
	}
	
}
