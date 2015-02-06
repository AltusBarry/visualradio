package altus.visualradio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import altus.visualradio.ListView.CustomListViewAdapter;
import altus.visualradio.ListView.DataDownloader;
import altus.visualradio.ListView.ModelBase;
import altus.visualradio.Loaders.DataStoreLoader;

/**
 * @author Altus Barry
 * @version 1.0
 *
 * Main Activity of the app.
 * Inflates the layouts and passes the app context on to the loader and adapter
 */

public class MainListingActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<ModelBase>>{
    // The loader's id as a final int, simply to ensure correct loader is always called and its id can be easily changed
    private static final int LOADER_ID = 0;
    private CustomListViewAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();
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

    // TODO pass whole ModelBase object, InflatedViewActivity should handle the getting of specifics
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
     * Triggers onContentChanged() which in turn changes teh value of takeContentChanged() to true and tells teh loader to
     * and tells the loader to load new data
     * @param view
     */
    public void updateList (View view) {
        getLoaderManager().getLoader(LOADER_ID).onContentChanged();
    }

    /**
     * Initiate first time startup
     * sets the view to inflate
     * initialises the loader
     * and assigns the adapter to be used
     */
    private void initActivity() {
        // Inflates the main layout
        setContentView(R.layout.activity_main_listing);

        // Create new loader or re use existing one
        getLoaderManager().initLoader(LOADER_ID, null, this);

        // Assign adapter to ListView
        listAdapter = new CustomListViewAdapter(this, new ArrayList<ModelBase>());
        // Main Activity needs to extend the ListActivity
        setListAdapter(listAdapter);
        Log.i("MainActivity.initActivity", " Layout Inflated;  Loader initialised;  Adapter set;");
    }

    // TODO Not implemented at this time
    /**
     * Displays an Alert Dialogue when called
     */
    public void closeAppAlert() {
       new AlertDialog.Builder(this)
            .setTitle("Connection Failed")
            .setMessage("Could not connect to server App will now close")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
    }

    //LOADER METHODS AND CALLBACKS
    /**
     * returns the application context to the loader class, to ensure no data leaks occur on activity recreate
     * @param id loader to be created`s id
     * @param args any arguments to be passed to loader
     * @return
     */
    @Override
    public Loader<List<ModelBase>> onCreateLoader(int id, Bundle args) {
        return new DataStoreLoader(getApplicationContext());
    }

    /**
     * Callback for when loader loadInBackground has been completed
     * sets and updates the list Adapter's data
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<List<ModelBase>> loader, List<ModelBase> data) {
        listAdapter.setData(data);
        Log.i("MainActivity.onLoadFinished(): ", "Adapter Data set");
        Log.i("MainActivity.listSize: ", Integer.toString(data.size()));
    }

    @Override
    public void onLoaderReset(Loader<List<ModelBase>> loader) {

    }
}



