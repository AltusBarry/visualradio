package altus.visualradio;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

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

import altus.visualradio.models.ListDetailSetter;


public class MainListingActivity extends ListActivity {
    private static      String index_filename = "VS_index_feed.json";
    private             ArrayList<ListDetailSetter> index_detail_setter;
    private             JSONObject json_object;

    private static      String JSON_INDEX_KEY = "com.index_feed";
    public static        String TITLE_KEY(){return "com.visual.header";}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //**writeToIndexFile();**\\
        setContentView(R.layout.activity_main_listing);
        //Read Index Feed into JSON Array
        try {
            readMsgFeedFile(index_filename);
            writeToIndexList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomListViewAdapter customAdapter = new CustomListViewAdapter(this, index_detail_setter);
        // Assign adapter to ListView
        setListAdapter(customAdapter);
    }
    //ONCE OFF CREATION OF EMPTY FILE COMMENTED OUT AFTER USE
   /* public void writeToIndexFile(){
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

    //Read file into JSON array
    public void readMsgFeedFile(String index_filename) throws IOException, JSONException {
        //Assign file with name and directory of the VS_index_feed.json, which was created manually earlier
        File msg_feed_file = new File(getExternalFilesDir(null), index_filename);
        json_object = new JSONObject();
        JSONArray temp_array = new JSONArray();
        String temp_string = "";

        if (msg_feed_file != null) {
            //Create new file input stream
            FileInputStream file_input_stream = new FileInputStream(msg_feed_file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(file_input_stream));
            StringBuilder string_builder = new StringBuilder();
            String current_line = null;
            //Read every line till end of file
            while ((current_line = reader.readLine()) != null) {
                string_builder.append(current_line);
            }
            reader.close();
            file_input_stream.close();
            temp_string = string_builder.toString();
            //Parse string from file through JSON tokener to extract characters and tokens properly
            JSONTokener jsonTokener = new JSONTokener(temp_string);
            //feed the parsed string into JSONArray
            temp_array = new JSONArray(jsonTokener);
        }
        json_object.put(JSON_INDEX_KEY, temp_array);
    }

    //TODO
    public void writeToIndexList() throws JSONException {
        index_detail_setter = new ArrayList<>();
        JSONArray temp_assignList_array = json_object.getJSONArray(JSON_INDEX_KEY);

        for(int i = 0; i<temp_assignList_array.length(); i++){
            ListDetailSetter listDetailSetter = new ListDetailSetter();
            listDetailSetter.setTitle(temp_assignList_array.getJSONObject(i).getString("title"));

            index_detail_setter.add(listDetailSetter);
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

    protected void onListItemClick(ListView index_list, View v, int list_position, long id){
        //Start intent that will make use of the InflatedViewActivity class
        Intent inflate_view = new Intent(this, InflatedViewActivity.class);

        String inflated_title = index_detail_setter.get(list_position).getTitle();

        inflate_view.putExtra(TITLE_KEY(), inflated_title);

        startActivity(inflate_view);
    }
}
