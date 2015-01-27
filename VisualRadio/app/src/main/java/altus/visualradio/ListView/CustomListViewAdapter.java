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
        ImageView imageView;
        int position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        ModelBase indexList = getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        ImageDownloader imageDownloader = new ImageDownloader();

        if (convertView == null) {
            if(indexList.type.equals("Music")) {
                Music music = (Music) getItem(position);
                convertView = inflater.inflate(R.layout.main_index_list_music, parent, false);
                TextView titleText = (TextView) convertView.findViewById(R.id.text_title);
                TextView artistName = (TextView) convertView.findViewById(R.id.text_artist_name);

                titleText.setText(music.title);
                artistName.setText(music.artist);

            }else if(indexList.type.equals("News")) {
                convertView = inflater.inflate(R.layout.main_index_list_news, parent, false);

                TextView titleText = (TextView) convertView.findViewById(R.id.text_title);
                TextView textSnippet = (TextView) convertView.findViewById(R.id.text_snippet);

                titleText.setText(indexList.title);
                textSnippet.setText(indexList.type);
            }
            TextView timeStamp = (TextView) convertView.findViewById(R.id.text_time_stamp);
            timeStamp.setText(indexList.publishOn);

            // Assign image view into a ViewHolder class, which is then updated when the Async task
            // has finished downloading the images
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.index_picture);
            viewHolder.position = position;

            // Launches a downloader Async task, its given the Directory, ImageUrl, ImageName, where the Image view is located and the current position in the list
            imageDownloader.execute(indexList.imageDir, indexList.imageUrl, indexList.imageName, viewHolder, position);
        }
        return convertView;
    }
}
