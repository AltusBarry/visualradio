package altus.visualradio.Threads;

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

import altus.visualradio.models.ModelBase;

/**
 * Created by altus on 2015/01/20.
 */
// Should also remove unnecessary key form JSON, does not currently do so
public class Feed {
    private static String JSON_INDEX_KEY = "com.index_feed";
    private JSONObject jsonObject;
    private ModelBase[] items;

    public void loadItems(final String directory, final String filename) {
        // Currently Unhandled thread, no call backs made to ensure main activity does not update
        // UI before reading is done
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    readFile(directory, filename);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    public void readFile(String directory, String filename) throws JSONException {
        // Read file into JSON array
        // Assign file with name and directory of the VS_index_feed.json, which was created manually earlier
        File msgFeedFile = new File(directory, filename);

        jsonObject = new JSONObject();
        JSONArray tempArray = new JSONArray();
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
            // Feed the parsed string into JSONArray
            tempArray = new JSONArray(jsonTokener);
        }
        //TODO Remove "card" KEY
/*        String tempObject1 = "";
        String tempObject2 = "";
        String temp1 = "";
        String temp2 = "";
        for(int i = 0; i<tempArray.length(); i++) {
            tempObject1 = tempArray.getJSONObject(i).getString("card").substring(0, tempArray.getJSONObject(i).getString("card").toString().length() - 1);
            tempObject2 = "";
            Log.d("YES WHAT!?", tempObject2);
        }*/
        // TODO END
        items = new ModelBase[tempArray.length()];
        for(int i = 0; i<tempArray.length(); i++) {
            Log.d("CONTENT TYPE: ", tempArray.getJSONObject(i).getJSONObject("card").getString("content_type"));
            if(tempArray.getJSONObject(i).getJSONObject("card").getString("content_type").equals("music")) {
                //items[i] = (Music) (tempArray.getJSONObject(i));
            }
        }

        jsonObject.put(JSON_INDEX_KEY, tempArray);
    }

    public ModelBase getItems(int position){
        return items[position];
    }
}

