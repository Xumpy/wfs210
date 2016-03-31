package be.velleman.nicowfs210;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * @author bn
 * 
 */

public class TCPConnector extends Connector {
        private static final Logger log = Logger.getLogger(TCPConnector.class);
        
	private static final String TAG = "WFS210-TCPConnector";
	int iPoort;
	String ip;
	InetAddress oIP;
	Socket socket = null;
	OutputStream oStream;
	BufferedInputStream iStream;
	Boolean isReceiving = false;
	
	public TCPConnector(String ip, int poort) {

		this.ip = ip;
		try {
			InetAddress[] inetarray = InetAddress.getAllByName(ip);
			oIP = inetarray[0];
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.iPoort = poort;

	}

	private static Boolean isReceiveRunning = false;

	/**
	 * A Thread that will start listening on the socket of this class It will be
	 * interrupted if the socket is <code>null</code> Or there is no bytes to be
	 * red.
	 * 
	 */
	public void startReceivingPackets() {

		if (!isReceiveRunning) {
                        log.debug("Start Connection");
			isReceiveRunning = true;
			Thread receiveWorker = new Thread("Receiving Thread") {
				public void run() {
                                        log.debug("Start Thread Running");
					try {
						byte[] buffer = new byte[4096];

						Packet foundPacket = null;
						int Count = 0;
						isReceiving = true;
						while (isReceiveRunning) {
							log.debug("Thread isReceiveRunning socket: " + socket);
                                                        if (socket != null) {
								Count = iStream.read(buffer);
                                                                log.debug("Count equals: " + Count);
								if (Count > 0) {
									parser.addDataToParse(buffer, Count); 
									System.currentTimeMillis();
									foundPacket = parser.parseNext();
									log.debug("foundPacket equals: " + foundPacket);
                                                                        while (foundPacket != null) {
                                                                                notifyNewPacket(foundPacket);
										foundPacket = parser.parseNext();
									}
									System.currentTimeMillis();
								} else {
									notifyDisconnectListeners();
									isReceiveRunning = false;
								}
							} else {
								isReceiveRunning = false;
							}
						}
						isReceiving = false;
					} catch (IOException error) {
						log.error(TAG + " - IOException startReceivingPackets");
						error.printStackTrace();
                                                close();
						isReceiveRunning = false;
						isReceiving = false;
					}

				}
			};
			receiveWorker.start();
		}
	}

	@Override
	public void open() {
		Thread worker1 = new Thread("Connection Init") {
			public void run() {
				try {
					socket = new Socket(ip, iPoort);
					socket.setTcpNoDelay(true);
					while (!socket.isConnected());
					isConnected = true;
					oStream = socket.getOutputStream();
					iStream = new BufferedInputStream(socket.getInputStream());
					notifyConnectListeners();
				} catch (IOException e) {
					log.error(TAG + " - IOException open: " + e.getMessage());
					e.printStackTrace();
					close();
				} catch (Exception e) {
					log.error(TAG + " - Exception open");
					e.printStackTrace(); 
					close();
				}
			}
		};
		worker1.start();

	}

	@Override
	public void close() {
		try {
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(TAG + " - Failed to properly close the socket");
		}
		socket = null;
		iStream = null;
		oStream = null;
		isConnected = false;
		notifyDisconnectListeners();
	}

	@Override
	public void send(Packet packet) {
		try {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : packet.getPacket()) {
                            sb.append(String.format("%02X ", b));
                        }
                        log.debug("Send packet: " + sb);
                        
			oStream.write(packet.getPacket());
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
		catch(NullPointerException e)
		{
			log.error(TAG + " - NullPointerException send");
                        e.printStackTrace();
		}
	}

}
