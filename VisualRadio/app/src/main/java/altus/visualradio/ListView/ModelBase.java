package altus.visualradio.ListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by altus on 2015/01/20.
 * The class used to store the data types that is used by the adapter to populate ListView
 */
public class ModelBase {
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
        //type = jsonObject.getJSONObject("card").getString("content_type");
    }
}

class Music extends ModelBase {
    public String artist;

    public Music(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        artist = jsonObject.getJSONObject("card").getString("artist");
    }
}




