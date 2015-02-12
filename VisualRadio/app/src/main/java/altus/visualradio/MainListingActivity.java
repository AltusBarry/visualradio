package altus.visualradio;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListActivity;
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
import altus.visualradio.ListView.DataHandler;
import altus.visualradio.ListView.ModelBase;

/**
 * @author Altus Barry
 * @version 1.0
 *
 * Main Activity of the app.
 * Inflates the layouts and passes the app context on to the loader and adapter
 */

public class MainListingActivity extends ListActivity implements DataHandler.ActivityCallBack{
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
        Log.i("MainActivity.initActivity", " Layout Inflated;  Loader initialised;  Adapter set;");

        initFragment();
    }

    private static final String FRAGMENT_TAG = ".DataHandler";
    private void initFragment() {
        FragmentManager fm = getFragmentManager();
        DataHandler dh = (DataHandler) fm.findFragmentByTag(FRAGMENT_TAG);

        if(dh == null) {
            dh = new DataHandler();
            fm.beginTransaction().add(dh,FRAGMENT_TAG).commit();
        }
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

    @Override
    public void updateList() {
        FragmentManager fm = getFragmentManager();
        DataHandler dh = (DataHandler) fm.findFragmentByTag(FRAGMENT_TAG);
        listAdapter.setData(dh.getContents());
        Log.d("CPnt", Integer.toString(listAdapter.getCount()));
    }
}



