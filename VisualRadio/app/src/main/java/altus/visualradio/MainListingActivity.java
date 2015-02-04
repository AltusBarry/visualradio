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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import altus.visualradio.ListView.CustomListViewAdapter;
import altus.visualradio.ListView.DataStore;
import altus.visualradio.ListView.DataStoreLoader;
import altus.visualradio.ListView.ModelBase;

// After Handlers, Large parts should be shifted to Fragments.
// ListView creation and population should be a Fragment.
// Reading File should be a Fragment
// Only one Async can be run at a time

public class MainListingActivity extends ListActivity {
    public static Context context;

    // Private in class variables
    private             ArrayList<ModelBase> listContents = new ArrayList<>();
    private             int lastPublishOn = 0;
    private             PollingThread pollThread;

    // KEY variables
    public static       String TITLE_KEY() {return "com.visual.header";}

    // Fragments
    private DataStore dataStoreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflates the main layout
        setContentView(R.layout.activity_main_listing);
        // Creates the headless worker fragment
        initWorkerFragment();

        initListView();

        // Starts a thread taht will constatntly check for changes in list content
        pollThread = new PollingThread();
        pollThread.start();
    }

    protected void onStart() {
        super.onStart();
        pollThread.unpause();
    }

    protected void onStop() {
        pollThread.pause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("Pre Terminate", "LOG");
        pollThread.interrupt();
        Log.d("Post terminate", "LOG");
        try {
            pollThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    protected void onListItemClick(ListView indexList, View v, int listPosition, long id){
        // Start intent that will make use of the InflatedViewActivity class
        // Gets the position number in the listView of tapped item, feeds it into the indexDetailSetter array
        // which returns the values for that position in the array
        // Then it sends the data to the InflatedViewActivity to be displayed in the new view
        Intent inflateView = new Intent(this, InflatedViewActivity.class);
        String inflatedTitle = listContents.get(listPosition).title;
        inflateView.putExtra(TITLE_KEY(), inflatedTitle);
        startActivity(inflateView);
    }

    // Main Activity Fragment methods
    public void initWorkerFragment() {
        // Creates a headless fragment that contains the methods for reading the file and assigning
        // the values to the ListArray
        FragmentManager fm = getFragmentManager();
        // Check to see if we have retained the worker fragment.
        dataStoreFragment = (DataStore)fm.findFragmentByTag("feedReadFragment");
        // If not retained (or first time running), we need to create it.
        if (dataStoreFragment == null) {
            // create instance of NON UI Fragment
            dataStoreFragment = new DataStore();
            // NON UI Fragment added
            fm.beginTransaction().add(dataStoreFragment, "feedReadFragment").commit();

            // Executes the reading of the file and assigning it to ListArray
            dataStoreFragment.initialize(getExternalFilesDir(null).toString());
        }
    }

    public void cleanupFragments(String fragmentID) {
        //  Can be called at any time to remove fragment with specific tag
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(fm.findFragmentByTag((fragmentID))).commit();
    }

    // Adapter assign and update methods
    private CustomListViewAdapter customAdapter;

    public void initListView() {
        customAdapter = new CustomListViewAdapter(this, listContents);
        // Assign adapter to ListView
        // Needs to extend the ListActivity
        setListAdapter(customAdapter);
    }

    public void adapterUpdate() {
        // Called when listContents has been updated, updates the list view
        Log.d("MainActivity/listSize: ", Integer.toString(listContents.size()));
        // Clears the current index values of list, to ensure position data stays correct
        customAdapter.clear();
        // Adds all listContent Data
        customAdapter.addAll(listContents);
        // Notifies the adapter that data has changed and it needs to update
        customAdapter.notifyDataSetChanged();
    }

    private class PollingThread extends Thread {
        // Thread that constantly polls to see if listContents is out of date with newest version
        private boolean terminated = false;
        private boolean paused = true;

        volatile int counter = 0;

        final Handler pollingHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d("MainActivity/lastPublishOn", Integer.toString(lastPublishOn));
                // Fires the adapterUpdate method located in the main activity
                adapterUpdate();
            }
        };

        public void run() {
            listContents = new ArrayList<>();
            while (!Thread.currentThread().isInterrupted()) {
                if (!paused && (dataStoreFragment != null) && (lastPublishOn < dataStoreFragment.getLastPub())) {

                    // Check if listContent will exceed maximum allowed list length after next Chunk is added
                    // If it will exceed amount, enough indexes are cleared out at end of array to accommodate new data
                    int totalLength = (listContents.size() + dataStoreFragment.getContents(lastPublishOn).size());
                    int minRemovedIndex = listContents.size() - (totalLength - 20);
                    if(totalLength >= 20) {
                        for (int i = (listContents.size()-1); i >=minRemovedIndex; i--) {
                            listContents.remove(i);
                        }
                    }

                    // New chunk of data is added at beginning of list Array
                    listContents.addAll(0, dataStoreFragment.getContents(lastPublishOn));

                    // Last published on date is set to compare against newer data read in from url
                    lastPublishOn = Integer.parseInt(listContents.get(0).publishOn);

                    // Sends empty message to handler to initiate the update of the list view
                    pollingHandler.sendEmptyMessage(0);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                counter++;
                Log.d("Looped", Integer.toString(counter));
            }
        }

        public void terminate() {
            terminated = false;
        }
        public void pause() {
            paused = true;
        }
        public void unpause() {
            paused = false;
        }
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
}



