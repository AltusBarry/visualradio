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

    public static JSONArray concatArray(JSONArray array, JSONArray extraArray) {
        JSONObject obj = null;
        for(int i = 0; i<array.length(); i++) {
            try {
                obj = array.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                extraArray.put(obj);
                Log.d("JSONFilesIO/Extra object", extraArray.getJSONObject(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return extraArray;
    }
}
