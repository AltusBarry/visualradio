package altus.visualradio.ListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by altus on 2015/01/20.
 */
public class ModelBase {
    public String title;
    public String imageUrl;
    public String publishOn;
    public String type;
    public String imageDir;
    public String imageName;

    public ModelBase(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getJSONObject("card").getString("title");
        imageUrl = jsonObject.getJSONObject("card").getString("image_url");
        publishOn = jsonObject.getJSONObject("card").getString("publish_on");
    }
}

class Music extends ModelBase {
    public String artist;

    public Music(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        artist = jsonObject.getJSONObject("card").getString("artist");
    }
}




