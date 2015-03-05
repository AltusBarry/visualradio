package altus.visualradio.ListView;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import altus.visualradio.R;

/**
 * Created by altus on 2015/03/05.
 */
public class MultiPostRow {
    private Context context;

     public MultiPostRow(Context context) {
        this.context = context;
     }

    public void inflateRow(ModelBase modelbase, RelativeLayout layout) {

        if(modelbase.type.equals("weather")) {
            inflateWeather((Weather) modelbase, layout, context);
        } else if (modelbase.type == "traffic") {

        }

    }

    public void inflateWeather(Weather weather,RelativeLayout layout, Context context) {

        RelativeLayout cityBlock[] = new RelativeLayout[weather.cities.length()];

        for(int i = 0; i < weather.cities.length(); i++) {

         /*   try {
                if(weather.cities.getJSONObject(i).getJSONArray("days").getJSONObject(0).getString("high")){

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            cityBlock[i] = (RelativeLayout) layoutInflater.inflate(R.layout.block_weather, null);

            TextView cityName = (TextView) cityBlock[i].findViewById(R.id.city_name);
            TextView highTemp = (TextView) cityBlock[i].findViewById(R.id.high_temp);
            TextView lowTemp = (TextView) cityBlock[i].findViewById(R.id.low_temp);
            try {
                cityName.setText(weather.cities.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                highTemp.setText(weather.cities.getJSONObject(i).getJSONArray("days").getJSONObject(0).getString("high") + " ºC");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                lowTemp.setText(weather.cities.getJSONObject(i).getJSONArray("days").getJSONObject(0).getString("low") + " ºC");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            layout.addView(cityBlock[i], i);

            cityBlock[i].setId(i);


            RelativeLayout.LayoutParams params =  (RelativeLayout.LayoutParams)  cityBlock[i].getLayoutParams();

            if(i > 0) {
                params.addRule(RelativeLayout.BELOW,  cityBlock[i-1].getId());
                //cityBlock[i].addRule(RelativeLayout.BELOW, layout.getId())
                cityBlock[i].setLayoutParams(params);
            }
        }
    }



    public void inflateTraffic(Traffic traffic, RelativeLayout layout, Context context) {

    }
}
