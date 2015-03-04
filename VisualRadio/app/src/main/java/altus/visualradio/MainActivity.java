package altus.visualradio;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ListFragment;

import android.os.Bundle;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import altus.visualradio.ListView.InflatedViewFragment;
import altus.visualradio.ListView.ListViewFragment;
import altus.visualradio.ListView.ModelBase;

/**
 * @author Altus Barry
 * @version 1.0
 *
 * Main Activity of the app.
 * Inflates the layouts and handles fragments
 */

public class MainActivity extends Activity implements ListViewFragment.listCallbacks{
    private static final String FRAGMENT_TAG = "data_handler";
    private FragmentManager manager;
    private boolean inflatedState = false;
    private ModelBase inflatedData;

    // Fragments
    private DataHandler dataHandler;
    private InflatedViewFragment inflatedView;
    private PlayerFragment player;
    private ListViewFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        manager = getFragmentManager();

        // Checks if portrait or landscape, can be sued to inflate different layouts
        if(getResources().getConfiguration().orientation == 0) {
            Log.d("Oreintation", "Portrait");
        }else if (getResources().getConfiguration().orientation == 90) {
            Log.d("Oreintation", "LandScape");
        }
        setContentView(R.layout.main_activity);
        Log.i("MainActivity: ", "Layout set");
        initFragments();

        // Checks if saved instanceState exists and assumes that if yes it should check if the inflated view was open.
        if(savedInstanceState == null) {
            Log.e("SavedBundleState", "Null");
        }else {
           inflatedState = savedInstanceState.getBoolean("infaltedState");
            // If it was in an inflated state, re-inflate with old data
            if(inflatedState) {
                inflatedData = (ModelBase) savedInstanceState.getSerializable("infaltedData");
                inflateView(inflatedData);
            }
           Log.e("SavedBundleState", savedInstanceState.toString());

            position = savedInstanceState.getBundle("listPosition");
            listFragment.setStateChange(true);
            listFragment.setPosition(position);
           //listFragment = (ListViewFragment) manager.getFragment(savedInstanceState, "mContent");
        }
    }

    protected void onStart() {
        super.onStart();
    }
    protected void onStop() {
        super.onStop();
    }
    protected void onPause() {
        super.onPause();
    }

    /**
     * Saves a boolean for the inflated state
     * also passes the old data of the inflated view to save bundle (empty/null if it wasn't inflated)
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("infaltedState", inflatedState);
        outState.putSerializable("infaltedData", inflatedData);
        outState.putBundle("listPosition", position);
        //manager.putFragment(outState, "listOutstate", listFragment);
        super.onSaveInstanceState(outState);
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

    /**
     * Retrieves fragment on backstack and disables the up navigation button
     * Also sets the inflated state boolean back to false
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                getActionBar().setDisplayHomeAsUpEnabled(false);
                inflatedState = false;
                getActionBar().setTitle("Visual Radio");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Retrieves fragment on backstack and disables the up navigation button
     * Also sets the inflated state boolean back to false
     */
    public void onBackPressed() {
        if(inflatedState = true) {
            getFragmentManager().popBackStack();
            getActionBar().setDisplayHomeAsUpEnabled(false);
            inflatedState = false;
            getActionBar().setTitle("Visual Radio");
        }
    }
    // END ANDROID SPECIFIC METHODS


    /**
     * Adds Fragments to views or checks if Fragments already exist and re uses them
     */
    private void initFragments() {

        // Retained Fragment that is used to store data and delete old files.
        dataHandler = (DataHandler) manager.findFragmentByTag(FRAGMENT_TAG);

        if(dataHandler == null) {
            dataHandler = new DataHandler();
            manager.beginTransaction().add(dataHandler, FRAGMENT_TAG).commit();
        }

        // Fragment that drives the list View and its contents
        if(findViewById(R.id.list_fragment) != null) {
            if(listFragment == null) {
                listFragment = new ListViewFragment();
                manager.beginTransaction().replace(R.id.list_fragment, listFragment, "list_fragment").commit();
            }

        }

        // Fragment that handles the inflated/detailed view
        inflatedView = (InflatedViewFragment) manager.findFragmentByTag("inflated_fragment");

        //TODO no landscape layouts created
      /*  if((findViewById(R.id.landscape_activity) != null)) {
            if(inflatedView == null) {
                inflatedView = new InflatedViewFragment();
                manager.beginTransaction().replace(R.id.inflated_fragment, inflatedView, "inflated_fragment").commit();
            }
        }*/

        // Fragment that contains the media player
        player = (PlayerFragment) manager.findFragmentById(R.id.player_fragment);

        if (findViewById(R.id.player_fragment) != null) {
            if (player == null) {
                player = new PlayerFragment();
                manager.beginTransaction().replace(R.id.player_fragment, player, "player_fragment").commit();
            }
        }
    }

    /**
     * Method linked to the toggelable button on the player fragment
     * Toggles the mediaPlayer on or off byt calling each respective method in the Fragment
     * @param view
     */
    public void playPause(View view) {

        if (findViewById(R.id.player_fragment) != null) {
            if (player == null) {
                player = new PlayerFragment();
                manager.beginTransaction().replace(R.id.player_fragment, player, "player_fragment").commit();
            }
        }

        // Checks state of toggle
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            player.play();
        } else {
            player.pause();
        }
    }

    /**
     * Replaces the list view with an inflated view, list fragment is added to backstack
     * inflatedState is toggled to true
     * the inflated Data to be saved out is set
     * the Fragment is given its data to use
     * @param mb a Single ModelBase object's data for usage in inflated view
     */
    @Override
    public void inflateView(ModelBase mb) {
        //if((findViewById(R.id.landscape_activity) == null)) {
        if (inflatedView == null) {
            inflatedView = new InflatedViewFragment();
            manager.beginTransaction().replace(R.id.list_fragment, inflatedView, "inflated_fragment").addToBackStack(null).commit();
        }else {
            manager.beginTransaction().replace(R.id.list_fragment, inflatedView, "inflated_fragment").addToBackStack(null).commit();
        }
       // }
        inflatedState = true;
        inflatedData = mb;
        inflatedView.setDetail(mb);
    }

    private Bundle position = new Bundle();
    public void setPosition(Bundle bundle) {
        position = bundle;
    }
}



