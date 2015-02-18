package altus.visualradio.ListView;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import altus.visualradio.Utils.JSONFilesIO;

/**
 * Created by altus on 2015/02/04.
 * @author  Altus Barry
 * @version 1.0
 *
 */
public class DataStoreLoader extends AsyncTaskLoader<List<ModelBase>> {
    private List<ModelBase> mContents;
    private ArrayList<ModelBase> usedContents;
    public String externalDir;
    private int lastPublishOn = 0;
    private ConnectionThread connectThread = new ConnectionThread();
    private HeartBeatThread heartBeatThread = new HeartBeatThread();

    // Socket
    private FragmentSocket client = null;
    private static final String ECHIDNA_URL = "qa-visual-radio.za.prk-host.net";

    /**
     * Loader initialise area
     * savedInstanceState bundle data used when applicable
     * external Directory set from application context
     * Connection Thread started.
     * @param context
     */
    public DataStoreLoader(Context context) {
        super(context);

        setExternalDir((context.getExternalFilesDir(null)).toString());
        lastPublishOn(checkCache());
        try {
            client = new FragmentSocket(new URI("ws://" + ECHIDNA_URL + ":8888/subscribe"), new Draft_10());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // Initiate Socket connection
        connectThread.start();

        Log.i("External File directory", (context.getExternalFilesDir(null)).toString());
    }

    /**
     * Check if cached file exists on file system and then loads data from it
     * while also sending last known publish date to server.
     * @return
     */
    private int checkCache() {
        File cachedFile = new File(externalDir, "feed.json");
        JSONArray jArr = null;
        if(cachedFile.exists()) {
            try {
                jArr = new JSONArray(JSONFilesIO.readFile(cachedFile));
                Log.i("LASTP", jArr.getJSONObject(0).getJSONObject("card").getString("publish_on"));
                parseMessage(jArr.toString());
                return Integer.parseInt(jArr.getJSONObject(0).getJSONObject("card").getString("publish_on"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * Set the lastPublishOn time to send to socket server
     * @param lastPublishOn
     */
    public void lastPublishOn(int lastPublishOn) {
        this.lastPublishOn = lastPublishOn;
    }

    /**
     * set extrenal directory for use when saving cached files to phone
     * @param dir
     */
    public void setExternalDir(String dir) {
        this.externalDir = dir;
    }

    /**
     * Loader background task execution
     * fills ModelBase list with content sent by socket server
     * @return
     */
    @Override
    public List<ModelBase> loadInBackground() {
        List<ModelBase> list = new ArrayList<ModelBase>(getContent());
        return list;
    }

    protected void onStartLoading() {
        if (mContents != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mContents);
        }

        // onContentChanged() call causes next call of takeContentChanged() to return true.
        if (takeContentChanged()) {
            forceLoad();
        }
    }

    public void deliverResult(List<ModelBase> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }
        mContents = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    private void releaseResources(List<ModelBase> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    private static final String EXCEPTION = "exeption";
    private static final String LOST_CONNECTION = "lostConnection";
    private static final String SERVER_SHUTDOWN = "serverShutDown";

    /**
     * Switch case statement to handle some known errors that can occur with the server or connection
     * @param error
     */
    private void networkIssues(String error) {
        switch (error) {
            case EXCEPTION:
                break;
            case LOST_CONNECTION:
                connectThread.start();
                break;
            case SERVER_SHUTDOWN:
                lastPublishOn(Integer.parseInt(usedContents.get(0).publishOn));
                break;
        }
    }

    /**
     * Checks whether there is an active network connection on te phone
     * @return true if yes, false if no
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            Log.e("Network Status: ", "Network's Down");
            return false;
        } else {
            Log.i("Network Status: ", "Network's Up");
            return true;
        }
    }

    /**
     * tries to connect to the google server to check if internet access is available
     * @return true if yes, false if no
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("8.8.8.8"); //You can replace it with your name

            if (ipAddr.equals("")) {
                Log.e("Internet Status: ", "Cannot connect to Internet");
                return false;
            } else {
                Log.i("Internet Status: ", "Connected");
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * method for connecting the client
     * made a method merely for easier access by other methods and a controllable point to check if
     * connection is being made
     */
    private void connectSocket() {
        client.connect();
    }

    // Getter and Setter Methods
    private void setContent(ArrayList<ModelBase> contents) {
        this.usedContents = contents;
    }
    private ArrayList getContent() {
        return usedContents;
    }

    /**
     * Nested Websocket class
     */
    class FragmentSocket extends WebSocketClient {

        public FragmentSocket(URI serverURI, Draft draft) {
            super(serverURI, draft);
        }

        public FragmentSocket(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.i("Websocket: ", "Handshake successfull");
            Log.e("SENT TIME", String.valueOf(lastPublishOn));
            this.send("{\"msg_type\": \"subscribe\", \"channel\": \"visualradio\", \"last_seen\":" + String.valueOf(lastPublishOn) + "}");
        }

        @Override
        public void onMessage(String message) {
            parseMessage(message);
            Log.i("WebSocket Client Received Message:", message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.i("WebSocket closed with exit code " + code, " additional info: " + reason);
        }

        @Override
        public void onError(Exception ex) {
            Log.e("Network Error", ex.toString());
            networkIssues(EXCEPTION);
        }
    }

    // WebSocket Data manipulation Methods
    private ArrayList<ModelBase> contents = new ArrayList<>();

    /**
     * Receives message directly from socket server
     * Extracts relevant data and assigns into a List of ModelBase
     * // TODO
     * !! Also writes data out to a text file in the phone's external directory (Disabled)
     * @param message
     */
    public void parseMessage(String message) {
        JSONArray arr = JSONFilesIO.parseToArray(message);

        // TODO File is currently disabled completely
        File cachedFile = new File(externalDir, "feed.json");

            JSONArray currentJSONArray = null;
            if(cachedFile.exists()) {
                try {
                    currentJSONArray = new JSONArray(JSONFilesIO.readFile(cachedFile));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                currentJSONArray = new JSONArray();
            }

            //Append read Array with the newest URL values
            try {
                currentJSONArray = new JSONArray((JSONFilesIO.concatArray(currentJSONArray, arr)).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Write Array back out to cache
            JSONFilesIO.writeArrayToFile(currentJSONArray, cachedFile);

        // Assign the values into the contents list

        ModelBase mbObj = null;
        JSONObject obj = null;
        // Adds read data into contents list. Contents list is limited to 20 objects and is a ModeBase object
        try{
            for (int i = arr.length()-1; i >= 0; i--) {
                obj = arr.getJSONObject(i);
                // Log.d("DataStore/CONTENT TYPE: ", arr.getJSONObject(i).getJSONObject("card").getString("content_type"));
                if (obj.getJSONObject("card").getString("content_type").equals("music")) {
                    mbObj = new Music(obj);
                }else if (obj.getJSONObject("card").getString("content_type").equals("post")) {
                    mbObj = new Post(obj);
                }
                mbObj.imageDir = (externalDir);
                mbObj.imageName = createUniqueName(obj.getJSONObject("card").getString("image_url"));
                // Ensures content list does not exceed maximum amount
                if((contents.size()+1) > 20) {
                    contents.remove(contents.size()-1);
                }
                // Add contents to the front of ArrayList
                contents.add(0, mbObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setContent(contents);

        // Notify the loader to reload data
        this.onContentChanged();
    }

    /**
     * Creates unique MD5 string from incoming String
     * @param Url
     * @return
     */
    public String createUniqueName(String Url) {
        // Creates a unique MD5 key from the incoming string
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(Url.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * Thread to connect client
     * Checks if Network and Internet is up, polls till true
     */
    class ConnectionThread extends Thread {

        final Handler connectionHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                connectSocket();
            }
        };
        public void run() {
            heartBeatThread.start();
            while (!Thread.currentThread().isInterrupted()) {
                if(isNetworkConnected() && isInternetAvailable()) {
                    connectionHandler.sendEmptyMessage(0);
                    Thread.currentThread().interrupt();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // TODO currently does not message Server
    /**
     * Thread to intermittently check if server is still reachable
     */
    class HeartBeatThread extends Thread {

        final Handler heartBeatHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                networkIssues(LOST_CONNECTION);
            }
        };
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                // Check for response

                // if(!response){
                // heartBeatHandler.sendEmptyMessage(0);
                // Thread.currentThread().interrupt();
                // }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
