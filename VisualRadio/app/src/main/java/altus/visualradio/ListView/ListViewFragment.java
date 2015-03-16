package altus.visualradio.ListView;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import altus.visualradio.R;
import altus.visualradio.Utils.Constants;

/**
 * Created by altus on 2015/03/03.
 *
 * Populates the list view with data from the loader
 */
public class ListViewFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<ModelBase>> {

    private static final int LOADER_ID = 0;
    private IndexListAdapter listAdapter;
    public listCallbacks callback;
    private boolean updatePos = false;
    private Parcelable position = null;

    /*public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            getListView().onRestoreInstanceState(savedInstanceState.getParcelable("LIST_STATE"));
        }
    }*/

    /**
     * Instantiates the callback method
     * @param activity
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (listCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    public interface listCallbacks {
        public void inflateView(ModelBase mb, String id);
        public void setPosition(Bundle bundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Inflate the layout
        Log.i("List View:", " Inflating");
        return inflater.inflate(R.layout.main_index_list, container, false);
    }

    /**
     * Initialises the list view
     * and initialises the loader
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    /**
     * Saves the current list position out to the DataHandler fragment via the Main Activity
     */
    public void onPause() {
        Bundle bundle = new Bundle();
        position = getListView().onSaveInstanceState();
        bundle.putParcelable("listPosition", position);

        callback.setPosition(bundle);
        super.onPause();
    }

    /**
     * Notifies the main activity which view it needs to inflate, based on the item selected from the list
     * @param list
     * @param v
     * @param listPosition
     * @param id
     */
    public void onListItemClick(ListView list, View v, int listPosition, long id) {
        //Send Data of Clicked Item
        ModelBase item = (ModelBase) listAdapter.getItem(listPosition);
        if(item.type.equals("traffic")) {
           callback.inflateView(item, Constants.INFLATED_TRAFFIC);
        }else if(item.type.equals("music")) {
            callback.inflateView(item, Constants.INFLATED_MUSIC);
        } else if(item.type.equals("post")) {
            callback.inflateView(item, Constants.INFLATED_NEWS);
        }
    }

    /**
     * Assigns te list Adapter
     */
    private void init() {
        // Inflates the main layout
        Log.i("Initialising List", "True");
        // Assign adapter to ListView
        listAdapter = new IndexListAdapter(getActivity().getApplicationContext(), new ArrayList<ModelBase>());
        // Fragment needs to extend the ListFragment
        setListAdapter(listAdapter);
        Log.i("MainActivity.initActivity", " Inflated");
    }

    @Override
    public Loader<List<ModelBase>> onCreateLoader(int id, Bundle args) {
        return new DataStoreLoader(getActivity().getApplicationContext());
    }

    /**
     * Loader data used to update list with new data and the scroll position is set after data is assigned
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<List<ModelBase>> loader, List<ModelBase> data) {
        listAdapter.setData(data);
        updatePosition(position);
    }

    @Override
    public void onLoaderReset(Loader<List<ModelBase>> loader) {

    }

    /**
     * Main Activity sends the list position and sets a boolean flag that allows the position to be updated
     * @param position
     * @param updatePos
     */
    public void refocused(Bundle position, boolean updatePos) {
        this.position =  position.getParcelable("listPosition");
        this.updatePos = updatePos;
    }

    /**
     * Lsit position is updated if the boolean is true
     * @param position
     */
    public void updatePosition(Parcelable position) {
        if(updatePos) {
            getListView().onRestoreInstanceState(position);
            updatePos = false;
        }
    }
}
