package altus.visualradio.ListView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import altus.visualradio.R;

/**
 * Created by altus on 2015/03/05.
 */
public class MultiPostRow {
    private Context context;
    private ExecutorService executorService;
    private Map<RelativeLayout, String> rowView = Collections.synchronizedMap((new WeakHashMap<RelativeLayout, String>()));
    private long viewId;

     public MultiPostRow(Context context) {
        this.context = context;
         executorService= Executors.newFixedThreadPool(5);
     }

    public void inflateRow(ModelBase data, RelativeLayout layout, long viewId) {
        this.viewId = viewId;
        if(data.type.equals("weather")) {
            //executorService.submit(new inflateWeather((Weather) data, layout, context, viewId));
            WeatherInflate iWeather = new WeatherInflate((Weather) data, layout, context, viewId);
            iWeather.run();
        } else if (data.type.equals("traffic")) {
            //executorService.submit(new inflateTraffic((Traffic) data, layout, context, viewId));
            TrafficInflate iTraffic = new TrafficInflate((Traffic) data, layout, context, viewId);
            iTraffic.run();
        }
    }

    class WeatherInflate implements Runnable {
        private Weather weather;
        private RelativeLayout layout;
        private Context context;
        private long id;
        private RelativeLayout lLayout;

        WeatherInflate(Weather weather, RelativeLayout layout, Context context, long id) {
            this.weather = weather;
            this.layout = layout;
            this.context = context;
            this.id = id;
            lLayout = new RelativeLayout(context);
            lLayout = layout;
        }
        @Override
        public void run() {
            inflate();
        }

        public void inflate() {
            lLayout.findViewById(R.id.cell1);
            for(int i = 0; i < 3; i++) {
                int resID = context.getResources().getIdentifier(("cell" + (i +1)), "id", context.getPackageName());

                TextView cityName = (TextView) lLayout.findViewById(resID).findViewById(R.id.city_name);
                TextView highTemp = (TextView) lLayout.findViewById(resID).findViewById(R.id.high_temp);
                TextView lowTemp = (TextView) lLayout.findViewById(resID).findViewById(R.id.low_temp);
                ImageView type = (ImageView) lLayout.findViewById(resID).findViewById(R.id.weather_pic);

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

                String weatherType = null;
                try {
                    weatherType = weather.cities.getJSONObject(i).getJSONArray("days").getJSONObject(0).getString("icon");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String clearSunny = "clear";
                final String cloudy = "partlycloudy";
                final String rain = "chancerain";

                switch(weatherType) {
                    case clearSunny:
                        type.setImageResource(R.drawable.weather_clear_sunny);
                        break;
                    case cloudy:
                        type.setImageResource(R.drawable.weather_cloudy_overcast);
                        break;
                    case rain:
                        type.setImageResource(R.drawable.weather_rain);
                        break;
                    default:
                        type.setImageResource(R.drawable.weather_thunderstorm_unknown);
                }

            }
        }
    }

    class TrafficInflate implements Runnable {
        private Traffic traffic;
        private RelativeLayout layout;
        private Context context;
        private long id;
        private RelativeLayout lLayout;

        TrafficInflate(Traffic traffic, RelativeLayout layout, Context context, long id) {
            this.traffic = traffic;
            this.layout = layout;
            this.context = context;
            this.id = id;
            lLayout = new RelativeLayout(context);
            lLayout = layout;
        }
        @Override
        public void run() {
            inflate();
        }

        public void inflate() {
            lLayout.findViewById(R.id.cell1);
            for(int i = 0; i < 3; i++) {
                int resID = context.getResources().getIdentifier(("cell" + (i +1)), "id", context.getPackageName());

                TextView incidentType = (TextView) lLayout.findViewById(resID).findViewById(R.id.traffic_type);
                TextView description = (TextView) lLayout.findViewById(resID).findViewById(R.id.description);
                ImageView type = (ImageView) lLayout.findViewById(resID).findViewById(R.id.traffic_pic);
                try {
                    incidentType.setText(traffic.incidents.getJSONObject(i).getString("type"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    description.setText(traffic.incidents.getJSONObject(i).getString("description"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String trafficType = null;
                try {
                    trafficType = traffic.incidents.getJSONObject(i).getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String obstruction = "Obstruction";
                final String congestion = "Congestion";
                final String roadworks = "Roadworks";

                switch(trafficType) {
                    case obstruction:
                        type.setImageResource(R.drawable.traffic_road_closure);
                        break;
                    case congestion:
                        type.setImageResource(R.drawable.traffic_delays);
                        break;
                    case roadworks:
                        type.setImageResource(R.drawable.traffic_road_closure);
                        break;
                    default:
                        type.setImageResource(R.drawable.traffic_delays);
                }

            }
        }
    }
// TODO OLD UNDIFINED LINE NUMBER METHODS
    class inflateWeather implements Runnable {
        private Weather weather;
        private RelativeLayout layout;
        private Context context;
        private long id;
        private RelativeLayout lLayout;

        inflateWeather(Weather weather, RelativeLayout layout, Context context, long id) {
            this.weather = weather;
            this.layout = layout;
            this.context = context;
            this.id = id;
            lLayout = new RelativeLayout(context);
            lLayout = layout;
        }

        @Override
        public void run() {
            if(viewReused(lLayout)) {
                return;
            }
            inflate();
        }

        final Handler weatherHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                layout = lLayout;
            }
        };

        public void inflate() {
            rowView.put(lLayout, String.valueOf(id));
            RelativeLayout cityBlock[] = new RelativeLayout[weather.cities.length()];

            for(int i = 0; i < weather.cities.length(); i++) {

                LayoutInflater layoutInflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                cityBlock[i] = (RelativeLayout) layoutInflater.inflate(R.layout.block_weather, null);

                TextView cityName = (TextView) cityBlock[i].findViewById(R.id.city_name);
                TextView highTemp = (TextView) cityBlock[i].findViewById(R.id.high_temp);
                TextView lowTemp = (TextView) cityBlock[i].findViewById(R.id.low_temp);
                ImageView type = (ImageView) cityBlock[i].findViewById(R.id.weather_pic);
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

                String weatherType = null;
                try {
                    weatherType = weather.cities.getJSONObject(i).getJSONArray("days").getJSONObject(0).getString("icon");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String clearSunny = "clear";
                final String cloudy = "partlycloudy";
                final String rain = "chancerain";

                switch(weatherType) {
                    case clearSunny:
                        type.setImageResource(R.drawable.weather_clear_sunny);
                        break;
                    case cloudy:
                        type.setImageResource(R.drawable.weather_cloudy_overcast);
                        break;
                    case rain:
                        type.setImageResource(R.drawable.weather_rain);
                        break;
                    default:
                        type.setImageResource(R.drawable.weather_thunderstorm_unknown);
                }

                lLayout.addView(cityBlock[i], i);

                cityBlock[i].setId(i);


                RelativeLayout.LayoutParams params =  (RelativeLayout.LayoutParams)  cityBlock[i].getLayoutParams();

                if(i > 0) {
                    params.addRule(RelativeLayout.BELOW,  cityBlock[i-1].getId());
                    cityBlock[i].setLayoutParams(params);
                }
            }
            weatherHandler.sendEmptyMessage(0);
        }
    }

    class inflateTraffic implements Runnable {

        private Traffic traffic;
        private RelativeLayout layout;
        private Context context;
        private long id;
        private RelativeLayout lLayout;

        inflateTraffic(Traffic traffic, RelativeLayout layout, Context context, long id) {
            this.traffic = traffic;
            this.layout = layout;
            this.context = context;
            this.id = id;
            lLayout = new RelativeLayout(context);
            lLayout = layout;
        }

        @Override
        public void run() {
            if(viewReused(lLayout)) {
                return;
            }
            inflate();
        }

        final Handler trafficHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                layout = lLayout;
            }
        };

        public void inflate() {
            rowView.put(lLayout, String.valueOf(id));
            RelativeLayout trafficBLock[] = new RelativeLayout[traffic.incidents.length()];

            for(int i = 0; i <= 3; i++) {

                LayoutInflater layoutInflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                trafficBLock[i] = (RelativeLayout) layoutInflater.inflate(R.layout.block_traffic, null);

                TextView incidentType = (TextView) trafficBLock[i].findViewById(R.id.traffic_type);
                TextView description = (TextView) trafficBLock[i].findViewById(R.id.description);
                ImageView type = (ImageView) trafficBLock[i].findViewById(R.id.traffic_pic);

                try {
                    incidentType.setText(traffic.incidents.getJSONObject(i).getString("type"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    description.setText(traffic.incidents.getJSONObject(i).getString("description"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String trafficType = null;
                try {
                    trafficType = traffic.incidents.getJSONObject(i).getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String obstruction = "Obstruction";
                final String congestion = "Congestion";
                final String roadworks = "Roadworks";

                switch(trafficType) {
                    case obstruction:
                        type.setImageResource(R.drawable.traffic_road_closure);
                        break;
                    case congestion:
                        type.setImageResource(R.drawable.traffic_delays);
                        break;
                    case roadworks:
                        type.setImageResource(R.drawable.traffic_road_closure);
                        break;
                    default:
                        type.setImageResource(R.drawable.traffic_delays);
                }

                lLayout.addView(trafficBLock[i], i);

                trafficBLock[i].setId(i);


                RelativeLayout.LayoutParams params =  (RelativeLayout.LayoutParams)  trafficBLock[i].getLayoutParams();

                if(i > 0) {
                    params.addRule(RelativeLayout.BELOW,  trafficBLock[i-1].getId());
                    trafficBLock[i].setLayoutParams(params);
                }
            }
            trafficHandler.sendEmptyMessage(0);
        }
    }

    boolean viewReused(RelativeLayout layout) {
        String tag = rowView.get(layout);

        if(tag==null || !tag.equals(viewId))
            return false;
        return true;
        //return false;
    }
}
