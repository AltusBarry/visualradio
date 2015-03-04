package altus.visualradio.ListView;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import altus.visualradio.R;

/**
 * Created by altus on 2015/03/03.
 *
 * Populates the list view with data from the loader
 */
public class ListViewFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<ModelBase>> {

    private static final int LOADER_ID = 0;
    private CustomListViewAdapter listAdapter;
    public listCallbacks callback;
    private boolean stateChange = false;

    /*public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            getListView().onRestoreInstanceState(savedInstanceState.getParcelable("LIST_STATE"));
        }
    }*/

    /**
     *
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
       public void inflateView(ModelBase mb);
        public void setPosition(Bundle bundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Inflate the layout
        Log.i("List View:", " Inflating");
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }

    public void onStart() {
        super.onStart();
        if(stateChange) {
            getListView().onRestoreInstanceState(position.getParcelable("listPosition"));
            stateChange = false;
        }
    }

    public void onPause() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("listPosition", getListView().onSaveInstanceState());
        callback.setPosition(bundle);
        super.onPause();
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("LIST_STATE", getListView().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }*/

    public void onListItemClick(ListView list, View v, int listPosition, long id) {
        //Send Data of Clicked Item
        ModelBase item = (ModelBase) listAdapter.getItem(listPosition);
        callback.inflateView(item);
    }

    private void init() {
        // Inflates the main layout
        Log.i("Initialising List", "True");
        // Assign adapter to ListView
        listAdapter = new CustomListViewAdapter(getActivity().getApplicationContext(), new ArrayList<ModelBase>());
        // Main Activity needs to extend the ListActivity
        setListAdapter(listAdapter);
        Log.i("MainActivity.initActivity", " Inflated");
    }

    @Override
    public Loader<List<ModelBase>> onCreateLoader(int id, Bundle args) {
        return new DataStoreLoader(getActivity().getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<List<ModelBase>> loader, List<ModelBase> data) {
        listAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ModelBase>> loader) {

    }

    private Bundle position = new Bundle();
    public void setPosition(Bundle position) {
        this.position = position;
    }

    public void setStateChange(Boolean stateChange) {
        this.stateChange = stateChange;
    }
}
