package altus.visualradio.ListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import altus.visualradio.MainListingActivity;
import altus.visualradio.Utils.JSONFilesIO;
import altus.visualradio.Utils.UrlIO;

/**
 * Created by altus on 2015/01/20.
 * Headless fragment
 * Fragment is retained across state changes
 * Fragment does most of its heavy work in a thread
 * Fragment in charge of Downloading data from URL
 * populating the list data
 * writing out list data to cached file
 * and giving the main activity the data depending on the last publish value the main activity sends the fragment
 */
public class DataStore extends Fragment {
    private static final String JSON_INDEX_KEY = "com.index_feed";
    private ArrayList<ModelBase> contents = new ArrayList<>();
    private int lastPublishedOn;
    private String externalDirectory;
    private MainListingActivity ma = new MainListingActivity();

/*    public interface Callback
    {
        public void execute(ArrayList<ModelBase> lContents);
    }*/

    public class ContentsReady //implements Callback
    {
        public void execute(ArrayList<ModelBase> lContents)
        {
            contents = (ArrayList<ModelBase>) lContents.clone();
            Log.d("DataStore/contentsSize: ", Integer.toString(contents.size()));
            lastPublishedOn = Integer.parseInt(contents.get(0).publishOn);
            Log.d("DataStore/LastPublishOn: ", Integer.toString(lastPublishedOn));
        }

        public void executeAlert() {
            ma.closeApp();
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

        final Handler alertHandler = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                callback.executeAlert();
            }
        };


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
        // TODO add check to not crash if URL is unavailable
        private boolean error = false;
        public void run() {

            while(!Thread.currentThread().isInterrupted()) {
                readUrl();
                if(!error) {
                    callback.execute(contents);
                }
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
            JSONTokener jt = null;
            JSONArray arr = new JSONArray();
            File cachedFile = new File(externalDirectory, "feed.json");

            try {
                jt = new JSONTokener(UrlIO.readTextURL("http://192.168.0.244:8080"));
            }catch(IOException e) {
                alertHandler.sendEmptyMessage(1);
                error = true;
                return;
            }

            // Feed the parsed string into JSONArray
            try {
                arr = new JSONArray(jt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Read cached file into JSONArray
            // TODO File is currently never used
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

            // Append read Array with the newest URL values
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

            try{
                for (int i = arr.length()-1; i >= 0; i--) {
                    obj = arr.getJSONObject(i);
                    // Log.d("DataStore/CONTENT TYPE: ", arr.getJSONObject(i).getJSONObject("card").getString("content_type"));
                    if (obj.getJSONObject("card").getString("content_type").equals("music")) {
                        mbObj = new Music(obj);
                        mbObj.type = "Music";
                    }
                    mbObj.imageDir = externalDirectory;
                    mbObj.imageName = createUniqueName(obj.getJSONObject("card").getString("image_url"));
                    Log.d("DataStore/contents.size", Integer.toString(contents.size()));
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
        }
    }
}

