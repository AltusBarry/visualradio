package altus.visualradio.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by altus on 2015/01/20.
 */
public class ModelBase {
    public ModelBase(JSONObject jsonObject) throws JSONException {
        String title = jsonObject.getJSONObject("card").getString("title");
        Log.d("Title", title);
        String imageUrl = jsonObject.getJSONObject("card").getString("image_url");
        String publishOn = jsonObject.getJSONObject("card").getString("publish_on");
    }
}
class Music extends ModelBase {

    public Music(JSONObject jsonObject) throws JSONException {
        super();

        String artist = jsonObject.getJSONObject("card").getString("artist");
    }
}


