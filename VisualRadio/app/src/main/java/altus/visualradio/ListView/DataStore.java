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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by altus on 2015/01/20.
 */
public class DataStore extends Fragment {
    private static final String JSON_INDEX_KEY = "com.index_feed";
    private String fileDirectory;
    private JSONArray feedJSON = new JSONArray();
    private ArrayList<ModelBase> contents = new ArrayList<>();
    private int lastPublishedOn;
    private String externalDirectory;

    public interface Callback
    {
        public void execute(ArrayList<ModelBase> lContents);
    }

    public class ContentsReady implements Callback
    {
        public void execute(ArrayList<ModelBase> lContents)
        {
            contents = (ArrayList<ModelBase>) lContents.clone();
            lastPublishedOn = Integer.parseInt(contents.get(contents.size()-1).publishOn);
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

    public void loadItems(final String directory, final String filename) {
        // Currently Unhandled thread, no call backs made to ensure main activity does not update
        // UI before reading is done
    }

    public int getLastContent () {
        return lastPublishedOn;
    }

    public ArrayList getContents(int lastPublishedOn) {
        ArrayList<ModelBase> tempArray = new ArrayList<>();

        for (int i = 0; i<contents.size(); i++) {
            Log.d("PublishValue", (contents.get(i).publishOn));
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


        public void readFile(String directory, String filename) {
            fileDirectory = directory;
            // Read file into JSON array
            // Assign file with name and directory of the VS_index_feed.json, which was created manually earlier
            // Add each JSON object in Array into modelBase array list for use in view adapter to display list values
            File msgFeedFile = new File(directory, filename);
            File outPutFile = new File(directory, "feed.json");
            FileInputStream stream;
            FileOutputStream outputStream;
            try {
                stream = new FileInputStream(msgFeedFile);
                outputStream = new FileOutputStream(outPutFile);
                int length = (int) msgFeedFile.length();

                byte[] buffer = new byte[length];
                int readLength;
                while((readLength = stream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readLength);
                }
                stream.close();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("File not Found", "File may not have been downloaded");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void onFileRead() throws JSONException{
            String tempString = "";
            File msgFeedFile = new File(externalDirectory, "feed.json");

            // Create new file input stream
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(msgFeedFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("File not found", "Unable to find Index File");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String currentLine = null;
            // Read every line till end of file
            try {
                while ((currentLine = reader.readLine()) != null) {
                    stringBuilder.append(currentLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException", "Unable to read file");
            }
            try {
                reader.close();
                fileInputStream.close();
            } catch (IOException e) {
                Log.e("IOException", "Unable to close operations");
            }
            tempString = stringBuilder.toString();
            // Parse string from file through JSON tokener to extract characters and tokens properly
            JSONTokener jsonTokener = new JSONTokener(tempString);
            // DataStore the parsed string into JSONArray
            feedJSON = new JSONArray(jsonTokener);
            // lastPublishedOn = Integer.parseInt(readArray.getJSONObject(readArray.length()-1).getJSONObject("card").getString("publish_on"));
            ModelBase tempListContentHolder = null;

            for (int i = 0; i < feedJSON.length(); i++) {

                Log.d("CONTENT TYPE: ", feedJSON.getJSONObject(i).getJSONObject("card").getString("content_type"));
                if (feedJSON.getJSONObject(i).getJSONObject("card").getString("content_type").equals("music")) {
                    tempListContentHolder = new Music(feedJSON.getJSONObject(i));
                    tempListContentHolder.type = "Music";
                } else if (feedJSON.getJSONObject(i).getJSONObject("card").getString("content_type").equals("news")) {
                    tempListContentHolder = new Music(feedJSON.getJSONObject(i));
                    tempListContentHolder.type = "News";
                }
                tempListContentHolder.imageDir = fileDirectory;
                tempListContentHolder.imageName = createUniqueName(feedJSON.getJSONObject(i).getJSONObject("card").getString("image_url"));
                contents.add(tempListContentHolder);
            }
            lastPublishedOn = Integer.parseInt(feedJSON.getJSONObject(feedJSON.length()-1).getJSONObject("card").getString("publish_on"));
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
/*            readFile(externalDirectory, "VS_index_feed.json");
            try {
                onFileRead();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callback.execute(contents);*/

            while(!Thread.currentThread().isInterrupted()) {
                readUrl();
                callback.execute(contents);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        // nested thread for polling of URL should later be replaced with somethign else.

        public void readUrl() {
            JSONArray lArray;
            try{
                URL feedUrl = new URL("http://127.0.0.1:8080/");
                BufferedReader input = new BufferedReader(new InputStreamReader(feedUrl.openStream()));

                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = input.readLine()) != null) {
                    stringBuilder.append(line);
                    Log.d("String from URL", line);
                }
                input.close();
                String currentFeed = stringBuilder.toString();

                JSONTokener jsonTokener = new JSONTokener(currentFeed);
                // Feed the parsed string into JSONArray
                lArray = new JSONArray(jsonTokener);
                ModelBase tempListContentHolder = null;

                for (int i = 0; i < lArray.length(); i++) {

                    Log.d("CONTENT TYPE: ", lArray.getJSONObject(i).getJSONObject("card").getString("content_type"));
                    if (lArray.getJSONObject(i).getJSONObject("card").getString("content_type").equals("music")) {
                        tempListContentHolder = new Music(lArray.getJSONObject(i));
                        tempListContentHolder.type = "Music";
                    } else if (lArray.getJSONObject(i).getJSONObject("card").getString("content_type").equals("news")) {
                        tempListContentHolder = new Music(lArray.getJSONObject(i));
                        tempListContentHolder.type = "News";
                    }
                    tempListContentHolder.imageDir = fileDirectory;
                    tempListContentHolder.imageName = createUniqueName(lArray.getJSONObject(i).getJSONObject("card").getString("image_url"));
                    contents.add(tempListContentHolder);
                }
            }catch(MalformedURLException e) {
                Log.e("URL Error", "URL not found");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

