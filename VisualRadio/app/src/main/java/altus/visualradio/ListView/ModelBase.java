package altus.visualradio.ListView;

import android.text.format.DateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by altus on 2015/01/20.
 * The class is used as a variable to store all the list data in one object type
 */
public class ModelBase implements Serializable {
    public String title;
    public String imageUrl;
    public String publishOn;
    public String imageDir;
    public String imageName;
    // Extra variable set by DataStore for now as there are no different types. For testing purpouses
    public String type;

    public ModelBase(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getJSONObject("card").getString("title");

        //TODO Add value for neither existing
        if(jsonObject.getJSONObject("card").has("image_url")) {
            imageUrl = jsonObject.getJSONObject("card").getString("image_url");
        }else if(jsonObject.getJSONObject("card").has("thumbnail_url")) {
            imageUrl = jsonObject.getJSONObject("card").getString("thumbnail_url");
        }


        publishOn = getDate(Long.parseLong(jsonObject.getJSONObject("card").getString("publish_on")));
        type = jsonObject.getJSONObject("card").getString("content_type");
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("hh:mm aa", cal).toString();
        return date;
    }
}



class Music extends ModelBase {
    public String artist;

    public Music(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        artist = jsonObject.getJSONObject("card").getString("artist");
    }
}

class Post extends ModelBase {
    public String content;

    public Post(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        content = jsonObject.getJSONObject("card").getString("content");
    }
}



