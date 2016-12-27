package alabno.wserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.SSLException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.database.MySqlDatabaseConnection;
import alabno.msfeedback.FeedbackUpdaters;
import alabno.userauth.Authenticator;
import alabno.userauth.TokenGenerator;
import alabno.usercapabilities.Permissions;

public class AutoMarkerWSServer extends WebSocketServer implements Runnable {

    private final int threadsAvailable = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(threadsAvailable);
    private WebSocketHandler handler;
    private FeedbackUpdaters updaters;
    private boolean running = false;

    public AutoMarkerWSServer(int listenPort, FeedbackUpdaters updaters, MySqlDatabaseConnection db,
            Authenticator authenticator, TokenGenerator tokenGenerator, Permissions permissions) {
        super(new InetSocketAddress(listenPort));
        this.handler = new WebSocketHandler(executor, updaters, db, authenticator, tokenGenerator, permissions);
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
        handler.handleMessage(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (ex instanceof SSLException) {
            ex.printStackTrace();
            try {
                this.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            running = false;
            System.out.println("Setting running to false");
            return;
        }
        
        System.out.println("WebSocket server error message: ");
        ex.printStackTrace();
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Setting running to true");
        try {
            super.run();
        } catch (Exception e) {

        } finally {
            running = false;
            System.out.println("Setting running to false");
        }
    }

}
