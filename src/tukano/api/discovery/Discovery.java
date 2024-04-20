package tukano.api.discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.URI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * <p>
 * A class interface to perform service discovery based on periodic
 * announcements over multicast communication.
 * </p>
 * 
 */

public interface Discovery {

	/**
	 * Used to announce the URI of the given service name.
	 * 
	 * @param serviceName - the name of the service
	 * @param serviceURI  - the uri of the service
	 */
	public void announce(String serviceName, String serviceURI);

	/**
	 * Get discovered URIs for a given service name
	 * 
	 * @param serviceName - name of the service
	 * @param minReplies  - minimum number of requested URIs. Blocks until the
	 *                    number is satisfied.
	 * @return array with the discovered URIs for the given service name.
	 */
	public URI[] knownUrisOf(String serviceName, int minReplies);

	/**
	 * Get the instance of the Discovery service
	 * 
	 * @return the singleton instance of the Discovery service
	 */
	public static Discovery getInstance() {
		return DiscoveryImpl.getInstance();
	}
}

/**
 * Implementation of the multicast discovery service
 */
class DiscoveryImpl implements Discovery {

	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	// The pre-aggreed multicast endpoint assigned to perform discovery.

	static final int DISCOVERY_RETRY_TIMEOUT = 5000;
	static final int DISCOVERY_ANNOUNCE_PERIOD = 1000;

	// Replace with appropriate values: allowed IP Multicast range: 224.0.0.1 -
	// 239.255.255.255
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("224.0.0.1", 9000);

	// Used to separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	private static final int MAX_DATAGRAM_SIZE = 65536;

	private static Discovery singleton;

	private Map<URI, String> serviceURIs = new ConcurrentHashMap<>();

	private Map<String, Integer> serviceHits = new ConcurrentHashMap<>();

	
	private int replicaCurrent = 0;
	private int lastCapacity = 0;
	
	synchronized static Discovery getInstance() {
		if (singleton == null) {
			singleton = new DiscoveryImpl();
		}
		return singleton;
	}

	private DiscoveryImpl() {
		this.startListener();
	}

	@Override
	public void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName,
				serviceURI));

		var pktBytes = String.format("%s%s%s", "" + serviceName + "", DELIMITER, "" + serviceURI + "").getBytes();
		var pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);

		// start thread to send periodic announcements
		new Thread(() -> {
			try (var ds = new DatagramSocket()) {
				while (true) {
					try {
						ds.send(pkt);
						Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public URI[] knownUrisOf(String serviceName, int minEntries) {
		URI[] uriArray;
		var res = serviceHits.get(serviceName);
		if (minEntries != -1) {
			int i = -1;	
			if (res != null)
				i = res;

			while (i < minEntries) {
				try {
					Thread.sleep(DISCOVERY_RETRY_TIMEOUT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			uriArray = new URI[i];
		} else {
			while (res == null) {
				try {
					Thread.sleep(DISCOVERY_RETRY_TIMEOUT);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			lastCapacity = res;
			uriArray = new URI[lastCapacity];
		}
		
		int curr = 0;
		for (Map.Entry<URI, String> entry : serviceURIs.entrySet()) {
			if (entry.getValue().equals(serviceName)) {
				uriArray[curr++] = entry.getKey();
			}
				
		}
		
		if (minEntries == -1) {
			replicaCurrent = (replicaCurrent +1) % (lastCapacity);
			uriArray[0] = uriArray[replicaCurrent];
		}
		
		
		return uriArray;
		
	}

	private void startListener() {
		Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n", DISCOVERY_ADDR.getAddress(),
				DISCOVERY_ADDR.getPort()));

		new Thread(() -> {
			try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
				ms.joinGroup(DISCOVERY_ADDR, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
				for (;;) {
					try {
						var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);
						ms.receive(pkt);

						var msg = new String(pkt.getData(), 0, pkt.getLength());

						var parts = msg.split(DELIMITER);

						if (parts.length == 2) {

							var serviceName = parts[0];
							var uri = URI.create(parts[1]);		

							var res = serviceURIs.get(uri);
							if (res == null)
							{
								serviceURIs.put(uri, serviceName);
								
								var res1 = serviceHits.get(serviceName);
								if (res1 == null)
									res1 = 1;
								else
									res1 += 1;
								
								serviceHits.put(serviceName, res1);
							}
								
							
						}

					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}).start();
	}
}