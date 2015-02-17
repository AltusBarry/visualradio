package altus.visualradio;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import altus.visualradio.ListView.CustomListViewAdapter;
import altus.visualradio.ListView.InflatedViewActivity;
import altus.visualradio.ListView.ModelBase;
import altus.visualradio.ListView.DataStoreLoader;

/**
 * @author Altus Barry
 * @version 1.0
 *
 * Main Activity of the app.
 * Inflates the layouts and passes the app context on to the loader and adapter
 */

public class MainListingActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<ModelBase>>  {
    // The loader's id as a final int, simply to ensure correct loader is always called and its id can be easily changed
    private static final int LOADER_ID = 0;
    private CustomListViewAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            Log.e("SavedBundleState", "Null");
        }else if(savedInstanceState != null) {
            Log.e("SavedBundleState", savedInstanceState.toString());
        }

        initActivity();

        initFragment();

        getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start intent that will make use of the InflatedViewActivity class
     * Gets the position number in the listView of tapped item,
     * Makes a single instance of ModelBase to store send-able data in
     * Then it sends the data to the InflatedViewActivity to be displayed in the new view
     * @param list
     * @param v
     * @param listPosition
     * @param id
     */
    protected void onListItemClick(ListView list, View v, int listPosition, long id) {
        // Start intent that will make use of the InflatedViewActivity class
        // Gets the position number in the listView of tapped item,
        // Makes a single instance of ModelBase to store send-able data in
        // Then it sends the data to the InflatedViewActivity to be displayed in the new view
        Intent inflateView = new Intent(this, InflatedViewActivity.class);
        ModelBase item = (ModelBase)  listAdapter.getItem(listPosition);

        inflateView.putExtra("itemData", item);
        startActivity(inflateView);
    }
    // END ANDROID SPECIFIC METHODS

    /**
     * Initiate first time startup
     * sets the view to inflate
     * initialises the loader
     * and assigns the adapter to be used
     */
    private void initActivity() {
        // Inflates the main layout
        setContentView(R.layout.activity_main_listing);

        // Assign adapter to ListView
        listAdapter = new CustomListViewAdapter(this, new ArrayList<ModelBase>());
        // Main Activity needs to extend the ListActivity
        setListAdapter(listAdapter);
        Log.i("MainActivity.initActivity", " Layout Inflated;");
    }

    private static final String FRAGMENT_TAG = ".DataHandler";
    FragmentManager fm;
    DataHandler dataHandler;

    private void initFragment() {
        fm = getFragmentManager();
        dataHandler = (DataHandler) fm.findFragmentByTag(FRAGMENT_TAG);

        if(dataHandler == null) {
            dataHandler = new DataHandler();
            fm.beginTransaction().add(dataHandler,FRAGMENT_TAG).commit();
        }
        listAdapter.setData(dataHandler.getContents());
    }

    @Override
    public Loader<List<ModelBase>> onCreateLoader(int id, Bundle args) {
        return new DataStoreLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<List<ModelBase>> loader, List<ModelBase> data) {
        listAdapter.setData(data);
        dataHandler.setContents(data);
    }

    @Override
    public void onLoaderReset(Loader<List<ModelBase>> loader) {

    }
}



