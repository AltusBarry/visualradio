package altus.visualradio.ListView;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
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

import altus.visualradio.Utils.JSONFilesIO;
import altus.visualradio.Utils.UrlIO;

/**
 * Created by altus on 2015/01/20.
 * @author  Altus Barry
 * @version 1.0
 *
 * Does most of its heavy work in a thread
 * In charge of Downloading data from URL
 * and populating the list data
 * as well as writing out list data to cached file
 *
 * Loader calls on new contents when it is alerted about data change.
 */
public class DataDownloader {
    private ArrayList<ModelBase> contents = new ArrayList<>();
    private String externalDirectory;

    public class ContentsReady
    {
        public void notify(ArrayList<ModelBase> lContents)
        {
            contents = (ArrayList<ModelBase>) lContents.clone();
        }
    }

    public void initialize(final String externalDirectory) {

        this.externalDirectory = externalDirectory;
        ContentsThread contentsThread = new ContentsThread(externalDirectory, new ContentsReady());
        contentsThread.start();
    }

    public ArrayList getContent() {
        return contents;
    }

    private class ContentsThread extends Thread {
        // Reads URL and updates contents
        private String externalDirectory;
        private ArrayList<ModelBase> contents = new ArrayList<>();
        private ContentsReady callback;

        ContentsThread(String externalDirectory, ContentsReady callback) {
            this.externalDirectory = externalDirectory;
            this.callback = callback;
        }

        final Handler alertHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };

        // TODO add check to not crash if URL is unavailable
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                readUrl();
                // TODO currently just keeps app from crashing nothing happens
                if(readUrl()) {
                    callback.notify(contents);
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
        public boolean readUrl() {
            // Parse JSON and update cache file
            JSONTokener jt = null;
            JSONArray arr = new JSONArray();
            File cachedFile = new File(externalDirectory, "feed.json");

            try {
                jt = new JSONTokener(UrlIO.readTextURL("http://192.168.0.249:8080"));
            }catch(IOException e) {
                // TODO alert handler empty. App merely crashes if exception is thrown
                alertHandler.sendEmptyMessage(1);
                return false;
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

            // Adds read data into contents list. Contents list is limited to 20 objects and is a ModeBase object
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
            return true;
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
}


