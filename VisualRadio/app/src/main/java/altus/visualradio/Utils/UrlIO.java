package altus.visualradio.Utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by altus on 2015/02/02.
 */
public class UrlIO {

    public static String readTextURL(String url) {
        // Read from server
        URL feedUrl;
        String data = "";
        BufferedReader input;
        try{
            feedUrl = new URL(url);
            input = new BufferedReader(new InputStreamReader(feedUrl.openStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = input.readLine()) != null) {
                sb.append(line);
                //Log.d("String from URL", line);
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
