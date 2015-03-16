package altus.visualradio.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import altus.visualradio.R;

/**
 * Created by altus on 2015/03/06.
 */
public class InflatedListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Traffic data;
    private JSONArray incidentArray;

    public InflatedListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(ModelBase data) {
            //incidentArray = new JSONArray();
            //incidentArray = data;
        this.data = (Traffic) data;
        notifyDataSetChanged();
    }

    public class ViewHolder {
        int position;

        TextView incidentType;
        TextView timeAgo;
        TextView description;

        ImageView typePic;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject indexItem = null;
        try {
            indexItem = new JSONObject(String.valueOf(getItem(position)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViewHolder viewHolder;

        int type = getItemViewType(position);
        // if current view does not exist, inflate new layout and add views to viewHolder
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_inflated_list_traffic, parent, false);

            viewHolder.incidentType = (TextView) convertView.findViewById(R.id.traffic_type);
            viewHolder.timeAgo = (TextView) convertView.findViewById(R.id.time_ago);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);

            viewHolder.typePic = (ImageView) convertView.findViewById(R.id.traffic_pic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            viewHolder.incidentType.setText(indexItem.getString("type"));
            //viewHolder.timeAgo.setText();
            viewHolder.description.setText(indexItem.getString("description"));
        } catch (JSONException e) {

        }
        final String obstruction = "Obstruction";
        final String congestion = "Congestion";
        final String roadworks = "Roadworks";

        String incidentType = null;
        try {
            incidentType = data.incidents.getJSONObject(position).getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

            switch(incidentType) {
                case obstruction:
                    viewHolder.typePic.setImageResource(R.drawable.traffic_road_closure);
                    break;
                case congestion:
                    viewHolder.typePic.setImageResource(R.drawable.traffic_delays);
                    break;
                case roadworks:
                    viewHolder.typePic.setImageResource(R.drawable.traffic_road_closure);
                    break;
                default:
                    viewHolder.typePic.setImageResource(R.drawable.traffic_delays);
            }

        return convertView;
    }
    @Override
    public int getCount() {
        if(data == null) {
            return 0;
        } else {
            return data.incidents.length();
        }
    }

    @Override
    public Object getItem(int position) {
        try {
            return data.incidents.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
