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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private JSONArray readArray;
    private ArrayList<ModelBase> listContent;
    private int lastPublishedOn;


    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        // This fragment is now used to run a different thread.
        // It will also not recreate itself when the app changes states
        // Meaning the process can continue running uninterrupted
        this.setRetainInstance(true);
        listContent = new ArrayList<>();
    }

    public void loadItems(final String directory, final String filename) {
        // Currently Unhandled thread, no call backs made to ensure main activity does not update
        // UI before reading is done
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
               // listUpdateCallBack.modifyUI();
            }
        };
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    readFile(directory, filename);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               Message msg = new Message();
               msg.setTarget(handler);
               msg.sendToTarget();
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    public JSONArray readFile(String directory, String filename) throws JSONException {
        fileDirectory = directory;
        // Read file into JSON array
        // Assign file with name and directory of the VS_index_feed.json, which was created manually earlier
        // Add each JSON object in Array into modelBase array list for use in view adapter to display list values
        File msgFeedFile = new File(directory, filename);

        readArray = new JSONArray();
        String tempString = "";

        if (msgFeedFile != null) {
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
            readArray = new JSONArray(jsonTokener);
        }
       // lastPublishedOn = Integer.parseInt(readArray.getJSONObject(readArray.length()-1).getJSONObject("card").getString("publish_on"));
        fillListArray();
        return readArray;
    }

    public void fillListArray() throws JSONException {
        ModelBase tempListContentHolder = null;

        for (int i = 0; i < readArray.length(); i++) {
            this.lastPublishedOn = Integer.parseInt(readArray.getJSONObject(i).getJSONObject("card").getString("publish_on"));
            Log.d("CONTENT TYPE: ", readArray.getJSONObject(i).getJSONObject("card").getString("content_type"));
            if (readArray.getJSONObject(i).getJSONObject("card").getString("content_type").equals("music")) {
                tempListContentHolder = new Music(readArray.getJSONObject(i));
                tempListContentHolder.type = "Music";
            } else if (readArray.getJSONObject(i).getJSONObject("card").getString("content_type").equals("news")) {
                tempListContentHolder = new Music(readArray.getJSONObject(i));
                tempListContentHolder.type = "News";
            }
            tempListContentHolder.imageDir = fileDirectory;
            tempListContentHolder.imageName = createUniqueName(readArray.getJSONObject(i).getJSONObject("card").getString("image_url"));
            listContent.add(tempListContentHolder);
        }
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

    public int getLastItem () {
        return lastPublishedOn;
    }

    public ArrayList getItems(int lastPublishedOn) {
        ArrayList<ModelBase> tempArray = new ArrayList<>();

        for (int i = 0; i<listContent.size(); i++) {
            Log.d("PublishVAlue", ""+Integer.parseInt(listContent.get(i).publishOn));
            if(Integer.parseInt(listContent.get(i).publishOn) > lastPublishedOn) {
                tempArray.add(listContent.get(i));
            }
        }
        return tempArray;
    }
}

