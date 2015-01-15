package altus.visualradio;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Handler;

import altus.visualradio.AsyncTasks.IndexFileDownloadAsync;
import altus.visualradio.Threads.ThreadExample;
import altus.visualradio.models.ListDetailSetter;


public class MainListingActivity extends ListActivity {
    private static      String serverIP = "http://192.168.0.246:8000/list.json";
    private static      String indexFilename = "VS_index_feed.json";
    private             ArrayList<ListDetailSetter> indexDetailSetter;
    private             JSONObject jsonObject;
    private             TextView textView;

    private static      String JSON_INDEX_KEY = "com.index_feed";
    public static       String TITLE_KEY() {return "com.visual.header";}

    private             IndexFileDownloadAsync indexFileDownloadAsync;
    private             ThreadExample threadExample;
    private             UIAsyncTask uiAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_listing);
        indexFileDownloadAsync = new IndexFileDownloadAsync();
        uiAsyncTask = new UIAsyncTask();
        textView = (TextView) findViewById(R.id.pageHeader);
        // **writeToIndexFile();** \\

        // waste 20 seconds while rest of activity continues
        nonUIFragment();

        // Read Index Feed into JSON Array and then displays it in the list view
        try {
            indexFileDownloadAsync.setFilePath(getExternalFilesDir(null).toString());
            uiAsyncTask.execute("Stuff");
            //indexFileDownloadAsync.execute(serverIP);
            readMessageFeedFile(indexFilename);
            writeToIndexList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomListViewAdapter customAdapter = new CustomListViewAdapter(this, indexDetailSetter);
        // Assign adapter to ListView
        // Needs to extend the ListActivity
        setListAdapter(customAdapter);
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

    public void readMessageFeedFile(String indexFilename) throws IOException, JSONException {
        // Read file into JSON array
        // Assign file with name and directory of the VS_index_feed.json, which was created manually earlier
        File msgFeedFile = new File(getExternalFilesDir(null), indexFilename);
        jsonObject = new JSONObject();
        JSONArray tempArray = new JSONArray();
        String tempString = "";

        if (msgFeedFile != null) {
            // Create new file input stream
            FileInputStream fileInputStream = new FileInputStream(msgFeedFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String currentLine = null;
            // Read every line till end of file
            while ((currentLine = reader.readLine()) != null) {
                stringBuilder.append(currentLine);
            }
            reader.close();
            fileInputStream.close();
            tempString = stringBuilder.toString();
            // Parse string from file through JSON tokener to extract characters and tokens properly
            JSONTokener jsonTokener = new JSONTokener(tempString);
            // Feed the parsed string into JSONArray
            tempArray = new JSONArray(jsonTokener);
        }
        jsonObject.put(JSON_INDEX_KEY, tempArray);
    }

    public void writeToIndexList() throws JSONException {
        // Creates a new ArrayList that will populate the list view with custom list object layouts
        // using the information stored in the JSONObject's, jsonArray
        indexDetailSetter = new ArrayList<>();
        JSONArray tempAssignListArray = jsonObject.getJSONArray(JSON_INDEX_KEY);

        for(int i = 0; i<tempAssignListArray.length(); i++) {
            ListDetailSetter listDetailSetter = new ListDetailSetter();
            listDetailSetter.setTitle(tempAssignListArray.getJSONObject(i).getString("title"));

            indexDetailSetter.add(listDetailSetter);
        }
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
        String inflatedTitle = indexDetailSetter.get(listPosition).getTitle();
        inflateView.putExtra(TITLE_KEY(), inflatedTitle);
        startActivity(inflateView);
    }

    public void nonUIFragment() {
        FragmentManager fm = getFragmentManager();
        // Check to see if we have retained the worker fragment.
        threadExample = (ThreadExample)fm.findFragmentByTag("retainedFragment");
        // If not retained (or first time running), we need to create it.
        if (threadExample == null) {
            // create instance of NON UI Fragment
            threadExample = new ThreadExample();
            // NON UI Fragment added
            fm.beginTransaction().add(threadExample, "retainedFragment").commit();
        }
    }

    public void cleanupNonUIFragment() {
        // Fragments that are set to retain their instance should be removed when they are no longer needed
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(this.threadExample).commit();
    }

    // Async task that is capable of manipulating UI
    class UIAsyncTask extends AsyncTask<String, String, String> {
        String string;
        protected String doInBackground(String... params) {
            string = params[0];
            long endTime = System.currentTimeMillis() + 20 * 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            return string;
        }

        protected void onPostExecute(String results) {
            textView.setText(results);
        }

    }

}

