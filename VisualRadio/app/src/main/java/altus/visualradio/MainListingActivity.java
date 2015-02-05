package altus.visualradio;

import android.app.AlertDialog;
import android.app.FragmentManager;
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import altus.visualradio.ListView.CustomListViewAdapter;
import altus.visualradio.ListView.DataDownloader;
import altus.visualradio.ListView.ModelBase;
import altus.visualradio.Loaders.DataStoreLoader;

// After Handlers, Large parts should be shifted to Fragments.
// ListView creation and population should be a Fragment.
// Reading File should be a Fragment
// Only one Async can be run at a time

public class MainListingActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<ModelBase>>{
    public static Context context;
    private static final int LOADER_ID = 0;

    // Private in class variables

    // KEY variables
    public static       String TITLE_KEY() {return "com.visual.header";}

    // Fragments
    private DataDownloader dataStoreFragment;

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

    protected void onListItemClick(ListView indexList, View v, int listPosition, long id) {
        // Start intent that will make use of the InflatedViewActivity class
        // Gets the position number in the listView of tapped item, feeds it into the indexDetailSetter array
        // which returns the values for that position in the array
        // Then it sends the data to the InflatedViewActivity to be displayed in the new view
        Intent inflateView = new Intent(this, InflatedViewActivity.class);
        startActivity(inflateView);
    }

    public void updateList (View view) {
        getLoaderManager().getLoader(0).onContentChanged();
    }
    // END ANDROID SPECIFIC METHODS

    // Initiate first time startup
    private CustomListViewAdapter customAdapter;
    public void initActivity() {
        // Inflates the main layout
        setContentView(R.layout.activity_main_listing);

        getLoaderManager().initLoader(0, null, this);

        // Assign adapter to ListView
        customAdapter = new CustomListViewAdapter(this, new ArrayList<ModelBase>());
        // Needs to extend the ListActivity
        setListAdapter(customAdapter);
    }

    // TODO should use callback, non implemented at this time
    public void closeApp() {
       new AlertDialog.Builder(this)
            .setTitle("Connection Failed")
            .setMessage("Could not connect to server App will now close")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
    }

    //Loader methods and callbacks
    @Override
    public Loader<List<ModelBase>> onCreateLoader(int id, Bundle args) {
        return new DataStoreLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<List<ModelBase>> loader, List<ModelBase> data) {
        customAdapter.setData(data);
        Log.d("MainActivity/listSize: ", Integer.toString(data.size()));
    }

    @Override
    public void onLoaderReset(Loader<List<ModelBase>> loader) {

    }
}



