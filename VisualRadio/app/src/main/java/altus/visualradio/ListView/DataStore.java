package altus.visualradio.ListView;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import altus.visualradio.Utils.JSONFilesIO;
import altus.visualradio.Utils.UrlIO;

/**
 * Created by altus on 2015/01/20.
 */
public class DataStore extends Fragment {
    private static final String JSON_INDEX_KEY = "com.index_feed";
    private ArrayList<ModelBase> contents = new ArrayList<>();
    private int lastPublishedOn;
    private String externalDirectory;

/*    public interface Callback
    {
        public void execute(ArrayList<ModelBase> lContents);
    }*/

    public class ContentsReady //implements Callback
    {
        public void execute(ArrayList<ModelBase> lContents)
        {
            contents = (ArrayList<ModelBase>) lContents.clone();
            lastPublishedOn = Integer.parseInt(contents.get(0).publishOn);
            Log.d("DataStore/LastPublishOn: ", Integer.toString(lastPublishedOn));
        }
    }

    public void initialize(final String externalDirectory) {
        this.externalDirectory = externalDirectory;
        ContentsThread contentsThread = new ContentsThread(externalDirectory, new ContentsReady());
        contentsThread.start();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        // This fragment is now used to run a different thread.
        // It will also not recreate itself when the app changes states
        // Meaning the process can continue running uninterrupted
        this.setRetainInstance(true);
    }

    public int getLastPub () {
        return lastPublishedOn;
    }

    public ArrayList getContents(int lastPublishedOn) {
        ArrayList<ModelBase> tempArray = new ArrayList<>();

        for (int i = 0; i<contents.size(); i++) {
            if(Integer.parseInt(contents.get(i).publishOn) > lastPublishedOn) {
                tempArray.add(contents.get(i));
            }
        }
        return tempArray;
    }

    private class ContentsThread extends Thread {
        private String externalDirectory;
        private ArrayList<ModelBase> contents = new ArrayList<>();
        private ContentsReady callback;

        ContentsThread(String externalDirectory, ContentsReady callback) {
            this.externalDirectory = externalDirectory;
            this.callback = callback;
        }

        public String createUniqueName(String Url) {
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

        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                readUrl();
                callback.execute(contents);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        // Polling url for changes
        public void readUrl() {
            // Parse JSON and update cache file
            JSONArray arr = new JSONArray();
            File cachedFile = new File(externalDirectory, "feed.json");
            JSONTokener jt = new JSONTokener(UrlIO.readTextURL("http://192.168.0.244:8080"));
            // Feed the parsed string into JSONArray
            try {
                arr = new JSONArray(jt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Read cached file into JSONArray
            JSONArray currentJSONArray = null;
            if(cachedFile.exists()) {
                try {
                    currentJSONArray = new JSONArray(JSONFilesIO.readFile(cachedFile));
                    Log.d("", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                currentJSONArray = new JSONArray();
            }

            // Append read Array with the newest URL values
            currentJSONArray = JSONFilesIO.concatArray(arr, currentJSONArray);

            // Write Array back out to cache
            JSONFilesIO.writeArrayToFile(currentJSONArray, cachedFile);

            // Assign the values into the contents list
            ModelBase mbObj = null;
            JSONObject obj = null;
            try{
                for (int i = arr.length()-1; i >= 0; i--) {
                    obj = arr.getJSONObject(i);
                    //Log.d("DataStore/CONTENT TYPE: ", arr.getJSONObject(i).getJSONObject("card").getString("content_type"));
                    if (obj.getJSONObject("card").getString("content_type").equals("music")) {
                        mbObj = new Music(obj);
                        mbObj.type = "Music";
                    } else if (obj.getJSONObject("card").getString("content_type").equals("news")) {
                        mbObj = new Music(obj);
                        mbObj.type = "News";
                    }
                    mbObj.imageDir = externalDirectory;
                    mbObj.imageName = createUniqueName(obj.getJSONObject("card").getString("image_url"));
                    contents.add(0, mbObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

