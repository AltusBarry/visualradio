package altus.visualradio.ListView;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import altus.visualradio.Loaders.DataStoreLoader;
import altus.visualradio.Utils.JSONFilesIO;

/**
 * Created by altus on 2015/02/12.
 */
public class DataHandler extends Fragment implements LoaderManager.LoaderCallbacks<List<ModelBase>> {
    private static final int LOADER_ID = 1;
    private Context appContext;
    private static final String ECHIDNA_URL = "qa-visual-radio.za.prk-host.net";
    //private static final String ECHIDNA_URL = "192.168.0.3(00)";

    private List<ModelBase> contents = new ArrayList<>();

    // CALLBACKS
    ActivityCallBack calback;
    // Container Activity must implement this interface
    public interface ActivityCallBack {
        public void updateList();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            calback = (ActivityCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // Create new loader or re use existing one
        getLoaderManager().initLoader(LOADER_ID, null, this);

        // Create Socket
        FragmentSocket client = null;
        try {
            client = new FragmentSocket(new URI("ws://" + ECHIDNA_URL + ":8888/subscribe"), new Draft_10());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // Initiate Socket connection
        client.connect();
    }

    public void setApplicationContext(Context appContext) {
    }
    //LOADER METHODS AND CALLBACKS
    /**
     * returns the application context to the loader class, to ensure no data leaks occur on activity recreate
     * @param id loader to be created`s id
     * @param args any arguments to be passed to loader
     * @return
     */
    @Override
    public Loader<List<ModelBase>> onCreateLoader(int id, Bundle args) {
        return new DataStoreLoader(getActivity().getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<List<ModelBase>> loader, List<ModelBase> data) {
        //contents = new List<>(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ModelBase>> loader) {

    }

    public List<ModelBase> getContents() {
        return contents;
    }

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
            this.send("{\"msg_type\": \"subscribe\", \"channel\": \"visualradio\"}");
        }

        @Override
        public void onMessage(String message) {
            parseMessage(message);
            Log.i("WebSocket Client Received Message:", "");
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.i("WebSocket closed with exit code " + code, " additional info: " + reason);
        }

        @Override
        public void onError(Exception ex) {

        }
    }

    public void parseMessage(String message) {
        JSONArray arr = JSONFilesIO.parseToArray(message);

        File cachedFile = new File(getActivity().getExternalFilesDir(null), "feed.json");
        // TODO File is currently never used
            /*JSONArray currentJSONArray = null;
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
            JSONFilesIO.writeArrayToFile(currentJSONArray, cachedFile);*/

        // Assign the values into the contents list

        ModelBase mbObj = null;
        JSONObject obj = null;
        Log.e("ArraySize", Integer.toString(arr.length()));
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
                mbObj.imageDir = (getActivity().getExternalFilesDir(null).toString());
                mbObj.imageName = createUniqueName(obj.getJSONObject("card").getString("image_url"));
                Log.d("DataHandler/contents.size", Integer.toString(contents.size()));
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

        calback.updateList();
        getLoaderManager().getLoader(LOADER_ID).onContentChanged();
    }

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
}
