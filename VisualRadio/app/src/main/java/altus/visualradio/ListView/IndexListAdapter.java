package altus.visualradio.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import altus.visualradio.R;

/**
 * Created by altus on 2015/01/12.
 * Set the values for the list row layouts
 */
public class IndexListAdapter extends BaseAdapter {//ArrayAdapter<ModelBase> {
    private LayoutInflater inflater;
    private ArrayList<ModelBase> models;
    private ImageLoader imageLoader;
    private MultiPostRow rowPost;

    public IndexListAdapter(Context context, ArrayList<ModelBase> contents) {
        this.models = contents;
        inflater = LayoutInflater.from(context);
        imageLoader = new ImageLoader(context);
        rowPost = new MultiPostRow(context);
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
    }

    public void forceUpdate() {
        notifyDataSetChanged();
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
        RelativeLayout loader;

        // Music Specific
        TextView artistName;

        // Posts Specific
        RelativeLayout layout;
    }

    /**
     * Inflates and sets data for each row item in the list view.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ModelBase indexItem = (ModelBase) getItem(position);
        ViewHolder viewHolder;
        //ImageDownloader imageDownloader = new ImageDownloader();
        Music music = null;
        Post post = null;
        int type = getItemViewType(position);
        // if current view does not exist, inflate new layout and add views to viewHolder
        if (convertView == null) {
            viewHolder = new ViewHolder();

            switch (type) {
                case 0:
                    // Inflate the layout with image
                    convertView = inflater.inflate(R.layout.main_index_list_music, parent, false);
                    viewHolder.artistName = (TextView) convertView.findViewById(R.id.text_artist_name);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.main_index_list_news, parent, false);
                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.main_index_list_weather, parent, false);
                    convertView.setEnabled(false);
                    convertView.setOnClickListener(null);
                    viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.weather);
                    break;
                case 3:
                    convertView = inflater.inflate(R.layout.main_index_list_traffic, parent, false);
                    viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.traffic);
                    break;
            }

            if((type == 0) || (type == 1)) {
                viewHolder.titleText = (TextView) convertView.findViewById(R.id.text_title);
                // Assign image view into a ViewHolder class, which is then updated when the Async task
                // has finished downloading the images
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.index_picture);
                viewHolder.imageView.setImageBitmap(null);
                viewHolder.position = position;
            }
            viewHolder.timeStamp = (TextView) convertView.findViewById(R.id.text_time_stamp);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set Image data
        // Launches a downloader Async task, its given the Directory, ImageUrl, ImageName,
        // where the Image view is located and the current position in the list
        //imageDownloader.execute(indexItem.imageDir, indexItem.imageUrl, indexItem.imageName, viewHolder, position);
        if((type == 0) || (type == 1)) {
            imageLoader.displayImage(indexItem.imageUrl, viewHolder.imageView, indexItem.imageName, indexItem.imageDir);
            viewHolder.titleText.setText(indexItem.title);
        }
        viewHolder.timeStamp.setText(indexItem.publishOn);

        // Set different data types
        switch (type) {
            case 0:
                music = (Music) indexItem;
                viewHolder.artistName.setText(music.artist);
                break;
            case 1 :
                viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                viewHolder.imageView.setAlpha((float) 0.5);
                break;
            case 2:
                rowPost.inflateRow(indexItem, viewHolder.layout, getItemId(position));
                break;
            case 3:
                rowPost.inflateRow(indexItem, viewHolder.layout, getItemId(position));
                break;
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

    public int getViewTypeCount() {
        return 4;
    }

    private static int TYPE_MUSIC = 0;
    private static int TYPE_POST = 1;
    private static int TYPE_WEATHER = 2;
    private static int TYPE_TRAFFIC = 3;
    public int getItemViewType(int position) {

        if (models.get(position).type.equals("music")) {
            return TYPE_MUSIC;
        } else if (models.get(position).type.equals("post")) {
            return TYPE_POST;
        } else if (models.get(position).type.equals("weather")) {
            return TYPE_WEATHER;
        } else if (models.get(position).type.equals("traffic")) {
            return TYPE_TRAFFIC;
        }
        return 0;
    }

}
