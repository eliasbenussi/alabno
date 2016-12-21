package alabno.wserver;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.database.MySqlDatabaseConnection;
import alabno.msfeedback.FeedbackUpdaters;
import alabno.userauth.Authenticator;
import alabno.userauth.TokenGenerator;

public class AutoMarkerWSServer extends WebSocketServer implements Runnable {
	
	private final int threadsAvailable = Runtime.getRuntime().availableProcessors();
	ExecutorService executor = Executors.newFixedThreadPool(threadsAvailable);
	private WebSocketHandler handler;
    private FeedbackUpdaters updaters;

	public AutoMarkerWSServer(int listenPort, FeedbackUpdaters updaters, MySqlDatabaseConnection db, Authenticator authenticator, TokenGenerator tokenGenerator) {
		super(new InetSocketAddress(listenPort));
		this.handler = new WebSocketHandler(executor, updaters, db, authenticator, tokenGenerator);
		this.updaters = updaters;
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		System.out.println("New connection opened");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		System.out.println("Connection closed");
		handler.closeSession(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("Received message: " + message);
		
		handler.handleMessage(conn, message);

	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		System.out.println("WebSocket server error message: ");
		ex.printStackTrace();
	}

}
