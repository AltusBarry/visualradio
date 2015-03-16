package altus.visualradio.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by altus on 2015/02/02.
 * Util class for use with JSON Objects
 */
public class JSONFilesIO {

    public static JSONTokener readFile(File file) {
        String tempString = "";

        // Read contents of file
        JSONTokener jsonTokener;
            // Create new file input stream
            FileInputStream fip;
            BufferedReader reader;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                fip = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(fip));
                String line = null;
                // Read every line till end of file
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                fip.close();
            } catch (IOException e) {
                Log.e("JSONFilesIO/readFile()", "Reading of file failed");
            }
            // add entire String builder value to a String for use in JSONArray
            tempString = stringBuilder.toString();
            // Parse string from file through JSON tokener to extract characters and tokens properly
            jsonTokener = new JSONTokener(tempString);

        return jsonTokener;
    }

    public static void writeArrayToFile(JSONArray jsonArray, File file) {
        // Writes JSONArray to the input file
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e("JSONFilesIO/writeFile()", "File to write to not found");
        }
        OutputStreamWriter os = new OutputStreamWriter(fileOutputStream);
        try {
            os.write(jsonArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray concatArray(JSONArray holderArray, JSONArray array) {
        // Combines the 2 Arrays for output to file
        JSONObject obj = null;
        JSONArray lArray = null;
        try {
            lArray = new JSONArray(array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Limits ArrayList length and remove overflow values before adding new values
        int totalLength = (holderArray.length() + lArray.length());
        int minRemovedIndex = Constants.LIST_LIMIT - lArray.length();
        Log.d("JSONIOTotal Length :", Integer.toString(totalLength));
        if(totalLength >= Constants.LIST_LIMIT) {
            for (int i = (totalLength); i >=minRemovedIndex; i--) {
                holderArray.remove(i);
            }
        }

        // Adds old values behind newest values and returns a JSONArray
        for(int i = 0; i<holderArray.length(); i++) {
            try {
                obj = holderArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            lArray.put(obj);
        }
        return lArray;
    }

    /**
     * Parses coming messages into an JSONArray
     * @param message
     * @return
     */
    public static JSONArray parseToArray(String message) {
        JSONObject jobj = null;
        JSONTokener jt = new JSONTokener(message);
        JSONArray arr = new JSONArray();

        try {
            arr = new JSONArray(jt);
        } catch (JSONException e) {
            try {
                jobj = new JSONObject(message);
                arr.put(jobj);
            } catch (JSONException e1) {
                Log.e("JSON Error", "Data is not in JSON format");
                e1.printStackTrace();
            }
/*              Log.e("JSON Error", "Data is not in JSONArray format");
                e.printStackTrace();*/
        }
        return arr;
    }
}
