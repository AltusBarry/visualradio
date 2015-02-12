package altus.visualradio.ListView;

import android.content.Context;
import android.util.Log;
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
 * Set the values for the list row layouts
 */
public class CustomListViewAdapter extends BaseAdapter {//ArrayAdapter<ModelBase> {
    private LayoutInflater inflater;
    private ArrayList<ModelBase> models;

    public CustomListViewAdapter(Context context, ArrayList<ModelBase> contents) {
        this.models = contents;
        inflater = LayoutInflater.from(context);
    }

    /**
     * clears the list or initialises a new empty list
     * add all the data passed through from the loader
     * notify the list adapter that data has been changed to update list view
     * @param data the data in the form of an list of ModelBase passed from the loader
     */
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
        Log.i("Incoming Size " + data.size(), "Current Set Size " + models.size());
    }

    /**
     * View holder to be used to store recycled views' data
     */
    public class ViewHolder {
        int position;
        // Universal views
        ImageView imageView;
        TextView titleText;
        TextView timeStamp;

        // Music Specific
        TextView artistName;

        // Posts Specific
        TextView content;

        String type;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ModelBase indexItem = (ModelBase) getItem(position);
        ViewHolder viewHolder;
        ImageDownloader imageDownloader = new ImageDownloader();
        Music music = null;
        Post post = null;

        // if current view does not exist, inflate new layout and add views to viewHolder
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // TODO only inflating one type of view
            convertView.setTag(viewHolder);

            viewHolder.titleText = (TextView) convertView.findViewById(R.id.text_title);

            if(indexItem.type.equals("music")) {
                convertView = inflater.inflate(R.layout.main_index_list_music, parent, false);
                viewHolder.artistName = (TextView) convertView.findViewById(R.id.text_artist_name);
                viewHolder.type = "music";
            }else if (indexItem.type.equals("post")) {
                convertView = inflater.inflate(R.layout.main_index_list_post, parent, false);
                viewHolder.content = (TextView) convertView.findViewById(R.id.text_post);
                viewHolder.type = "post";
            }

            viewHolder.timeStamp = (TextView) convertView.findViewById(R.id.text_time_stamp);

            // Assign image view into a ViewHolder class, which is then updated when the Async task
            // has finished downloading the images
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.index_picture);
            viewHolder.imageView.setImageBitmap(null);
            viewHolder.position = position;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set Image data
        // Launches a downloader Async task, its given the Directory, ImageUrl, ImageName,
        // where the Image view is located and the current position in the list
        imageDownloader.execute(indexItem.imageDir, indexItem.imageUrl, indexItem.imageName, viewHolder, position);
        //viewHolder.type = "Music";
        viewHolder.titleText.setText(indexItem.title);
        viewHolder.timeStamp.setText(indexItem.publishOn);

        // TODO Set different types of content
        if (viewHolder.type.equals("music")) {
            music = (Music) getItem(position);
            viewHolder.artistName.setText(music.artist);
        }else if(viewHolder.type.equals("post")) {
            post = (Post) getItem(position);
            viewHolder.content.setText(post.content);
        }

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
