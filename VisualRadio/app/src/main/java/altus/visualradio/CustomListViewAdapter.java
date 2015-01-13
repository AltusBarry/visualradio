package altus.visualradio;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import altus.visualradio.models.ListDetailSetter;

/**
 * Created by altus on 2015/01/12.
 */
public class CustomListViewAdapter extends ArrayAdapter<ListDetailSetter> {
    private LayoutInflater inflater;

    public CustomListViewAdapter(Activity activity, ArrayList indexList) {
        super(activity, R.layout.main_index_list_layout, indexList);
        inflater = activity.getWindow().getLayoutInflater();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListDetailSetter indexList = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.main_index_list_layout, parent, false);
        }

        TextView titleText = (TextView) convertView.findViewById(R.id.text_title);

        titleText.setText(indexList.getTitle());

        return convertView;
    }
}
