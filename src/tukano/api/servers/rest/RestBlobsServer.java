package tukano.api.servers.rest;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import tukano.api.discovery.Discovery;

public class RestBlobsServer {

	private static Logger Log = Logger.getLogger(RestBlobsServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 8070;
	public static final String SERVICE = "blobs";
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";
	
	public static final int DISCOVERY_ANNOUNCE_PERIOD = 1000;
	

	public static void main(String[] args) {
		try {
			
			//int serverPort = Integer.parseInt(args[1]);
			
			ResourceConfig config = new ResourceConfig();
			config.register(  RestBlobsResources.class );
			
			String ip = InetAddress.getLocalHost().getHostAddress();
			String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
			
			Discovery.getInstance().announce("blobs", serverURI);
			
			JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

			Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));
			
			
			//periodicAnnouncements(serverURI);

			// More code can be executed here...
		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
	}
	
	/*
	private static void periodicAnnouncements(String svURI) {
		new Thread(() -> {

			
			while (true) {
				try {
					String message = "<blobs><tab>";
					message += "<" + svURI+ ">";

					Discovery.getInstance().announce(message);
					Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}
	*/
}
