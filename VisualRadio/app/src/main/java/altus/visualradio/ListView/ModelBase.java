package altus.visualradio.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

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
        imageUrl = jsonObject.getJSONObject("card").getString("image_url");
        publishOn = jsonObject.getJSONObject("card").getString("publish_on");
        type = jsonObject.getJSONObject("card").getString("content_type");
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




