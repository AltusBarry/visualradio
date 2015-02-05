package altus.visualradio.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import altus.visualradio.R;

/**
 * Created by altus on 2015/01/12.
 */
public class CustomListViewAdapter extends BaseAdapter {//ArrayAdapter<ModelBase> {
    private LayoutInflater inflater;
    private ArrayList<ModelBase> models;

    public CustomListViewAdapter(Context context, ArrayList<ModelBase> contents) {
        this.models = contents;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<ModelBase> data) {
        if (models != null) {
            models.clear();
        } else {
            models = new ArrayList<ModelBase>();
        }
        if (data != null) {
            models.addAll(data);
        }
        notifyDataSetChanged();
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
        ModelBase indexItem = (ModelBase) getItem(position);
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

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Set Image data
        imageDownloader.execute(indexItem.imageDir, indexItem.imageUrl, indexItem.imageName, viewHolder, position);
        //viewHolder.type = "Music";
        viewHolder.titleText.setText(indexItem.title);
        viewHolder.timeStamp.setText(indexItem.publishOn);

        // TODO Set different types of content
       // if (viewHolder.type.equals("Music")) {
            //music = (Music) getItem(position);
            //viewHolder.artistName.setText(music.artist);
        //}

        return convertView;
    }

    public int getCount() {
        return models.size();
    }

    public Object getItem(int i) {
        return models.get(i);
    }

    public long getItemId(int i) {
        return i;
    }
}
