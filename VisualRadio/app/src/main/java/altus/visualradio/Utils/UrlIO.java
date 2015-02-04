package altus.visualradio.Utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import altus.visualradio.MainListingActivity;

/**
 * Created by altus on 2015/02/02.
 * Util class for the download of the server data
 */
public class UrlIO {

    public static String readTextURL(String url) throws IOException {
        // Read from server
        URL feedUrl;
        String data = "";

        BufferedReader input = null;
        // Text server, so input is read as String
        feedUrl = new URL(url);
        try {
            input = new BufferedReader(new InputStreamReader(feedUrl.openStream()));
        } catch (IOException e) {
            Log.e("Cannot connect", "Connection Failed");
            throw e;
        }
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = input.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            data = sb.toString();
        }catch(MalformedURLException e) {
            Log.e("readTextURL()", "URL not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
