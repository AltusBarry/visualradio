package altus.visualradio.ListView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import altus.visualradio.R;

/**
 * Created by altus on 2015/01/12.
 */
public class CustomListViewAdapter extends ArrayAdapter<ModelBase> {
    private LayoutInflater inflater;

    public CustomListViewAdapter(Activity activity, ArrayList indexList) {
        super(activity, R.layout.main_index_list_music, indexList);
        inflater = activity.getWindow().getLayoutInflater();
    }

    public class ViewHolder {
        // Universal views
        ImageView imageView;
        int position;
        TextView titleText;
        TextView timeStamp;
        // Music Specific
        TextView artistName;
        // News Specific
        String type;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ModelBase indexList = getItem(position);
        ViewHolder viewHolder;
        ImageDownloader imageDownloader = new ImageDownloader();
        Music music = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            // TODO only inflating one type of view
            convertView = inflater.inflate(R.layout.main_index_list_music, parent, false);
            convertView.setTag(viewHolder);

            viewHolder.artistName = (TextView) convertView.findViewById(R.id.text_artist_name);
            viewHolder.titleText = (TextView) convertView.findViewById(R.id.text_title);
            viewHolder.timeStamp = (TextView) convertView.findViewById(R.id.text_time_stamp);

            // Assign image view into a ViewHolder class, which is then updated when the Async task
            // has finished downloading the images
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.index_picture);
            viewHolder.position = position;
            // Launches a downloader Async task, its given the Directory, ImageUrl, ImageName,
            // where the Image view is located and the current position in the list
            imageDownloader.execute(indexList.imageDir, indexList.imageUrl, indexList.imageName, viewHolder, position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Set view data

        //viewHolder.type = "Music";
        viewHolder.titleText.setText(indexList.title);
        viewHolder.timeStamp.setText(indexList.publishOn);

        // TODO Set different types of content
       // if (viewHolder.type.equals("Music")) {
            //music = (Music) getItem(position);
            //viewHolder.artistName.setText(music.artist);
        //}

        return convertView;
    }
}
