package altus.visualradio;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import altus.visualradio.AsyncTasks.IndexFileDownloadAsync;
import altus.visualradio.ListView.CustomListViewAdapter;
import altus.visualradio.ListView.DataStore;
import altus.visualradio.ListView.ModelBase;

// After Handlers, Large parts should be shifted to Fragments.
// ListView creation and population should be a Fragment.
// Reading File should be a Fragment
// Only one Async can be run at a time

public class MainListingActivity extends ListActivity {
    // Private in class variables
    private static      String serverIP = "http://192.168.0.246:8000/list.json";
    private static      String indexFilename = "VS_index_feed.json";
    private             ArrayList<ModelBase> listContents;
    private             int lastPublishOn = 0;
    private             PollingThread pollThread;

    // KEY variables
    private static      String JSON_INDEX_KEY = "com.index_feed";
    public static       String TITLE_KEY() {return "com.visual.header";}

    // Non default variables
    private             IndexFileDownloadAsync indexFileDownloadAsync;

    // Fragments
    private DataStore dataStoreThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_listing);
        // Variables given values for use later;
        indexFileDownloadAsync = new IndexFileDownloadAsync();
        if(savedInstanceState == null) {
            createFeedThread();
        }
        // **writeToIndexFile();** \\
        // Read Index DataStore into JSON Array and then displays it in the list view

        indexFileDownloadAsync.setFilePath(getExternalFilesDir(null).toString());

        pollThread = new PollingThread();
        pollThread.start();
         //indexFileDownloadAsync.execute(serverIP);
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
        pollThread.terminate();
        try {
            pollThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        super.onDestroy();
    }

   /* public void writeToIndexFile() {
        // Creates an empty JSON file in the applications private external folder
        // Which was then manually populated and later used

        File msg_feed_file = new File(getExternalFilesDir(null), index_filename);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(msg_feed_file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write("Test_Value");
            outputStreamWriter.close();
        }catch (IOException e){
            Log.e("Exception", "File write failed: "+e.toString());
        }
    }*/

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
    public void createFeedThread() {
        // Creates a headless fragment that contains the methods for reading the file and assigning
        // te values to the ListArray
        FragmentManager fm = getFragmentManager();
        // Check to see if we have retained the worker fragment.
        dataStoreThread = (DataStore)fm.findFragmentByTag("feedReadFragment");
        // If not retained (or first time running), we need to create it.
        if (dataStoreThread == null) {
            // create instance of NON UI Fragment
            dataStoreThread = new DataStore();
            // NON UI Fragment added
            fm.beginTransaction().add(dataStoreThread, "feedReadFragment").commit();

            // Executes the reading of the file and assigning it to ListArray
            // TODO should later be moved to refresh method or something similar currently only reading on app start or state changes
            dataStoreThread.loadItems(getExternalFilesDir(null).toString(), indexFilename);
        }
    }

    public void cleanupFragments(String fragmentID) {
        //  Can be called at any time to remove fragment with specific tag
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(fm.findFragmentByTag((fragmentID))).commit();
    }

    // Main activity CallBack Methods
    public void modifyUI() {
        CustomListViewAdapter customAdapter = new CustomListViewAdapter(this, listContents);
        // Assign adapter to ListView
        // Needs to extend the ListActivity
        setListAdapter(customAdapter);
    }

    private class PollingThread extends Thread {
        private boolean terminated = false;
        private boolean paused = true;

        final Handler pollingHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                modifyUI();
            }
        };

        public void run() {
            listContents = new ArrayList<>();
            Message msg = new Message();
            msg.setTarget(pollingHandler);
            while (!terminated) {
                //Log.d("PublishTIme", )
                if (!paused && (lastPublishOn < dataStoreThread.getLastItem())) {
                    Log.d("XXXXXX", "XXXXX");
                    listContents.addAll(dataStoreThread.getItems(lastPublishOn));
                    Log.d("ListContentSize", ""+listContents.size());

                    lastPublishOn = dataStoreThread.getLastItem();
                    // get out of listcontents
                    //msg.sendToTarget();
                    // use different wait method
                }
                //SystemClock.sleep(1000);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("Looped", "Loopy");
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

}

