package altus.visualradio;

import android.app.Application;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by altus on 2015/02/11.
 */
public class SocketClient extends WebSocketClient {

    public SocketClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.w("Websocket: ", "Handshake successfull");
        Log.w("Websocket: ",handshakedata.toString());
        //this.send("");
    }

    @Override
    public void onMessage(String message) {
        Log.w("WebSocket Client Received Message:", message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.w("WebSocket closed with exit code " + code,  " additional info: " + reason);
    }

    @Override
    public void onError(Exception ex) {

    }
}
