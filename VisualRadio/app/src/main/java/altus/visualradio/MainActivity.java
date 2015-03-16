package altus.visualradio;

import android.app.Activity;
import android.app.FragmentManager;

import android.os.Bundle;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import altus.visualradio.ListView.InflatedListFragment;
import altus.visualradio.ListView.InflatedMusicFragment;
import altus.visualradio.ListView.InflatedNewsFragment;
import altus.visualradio.ListView.ListViewFragment;
import altus.visualradio.ListView.ModelBase;
import altus.visualradio.Utils.Constants;

/**
 * @author Altus Barry
 * @version 1.0
 *
 * Main Activity of the app.
 * Inflates the layouts and handles fragments
 */

public class MainActivity extends Activity implements ListViewFragment.listCallbacks,
        DataHandler.handlerCallbacks, InflatedListFragment.fragmentCallback, InflatedMusicFragment.fragmentCallback,
        InflatedNewsFragment.fragmentCallback {

    private static final String FRAGMENT_TAG = "data_handler";
    private FragmentManager manager;
    private boolean inflatedState = false;
    private ModelBase inflatedData;
    private Bundle position = null;

    // Fragments
    private DataHandler dataHandler;
    // Inflated Views
    private InflatedMusicFragment inflatedMusic;
    private InflatedNewsFragment inflatedNews;
    private InflatedListFragment inflatedList;

    // Initial Fragments
    private PlayerFragment player;
    private ListViewFragment listFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        manager = getFragmentManager();

        // Retained Fragment that is used to store data and delete old files.
        dataHandler = (DataHandler) manager.findFragmentByTag(FRAGMENT_TAG);

        if(dataHandler == null) {
            dataHandler = new DataHandler();
            manager.beginTransaction().add(dataHandler, FRAGMENT_TAG).commit();
        }


        // Checks if portrait or landscape, can be sued to inflate different layouts
   /*     if(getResources().getConfiguration().orientation == 0) {
            Log.d("Oreintation", "Portrait");
        }else if (getResources().getConfiguration().orientation == 90) {
            Log.d("Oreintation", "LandScape");
        }*/

        setContentView(R.layout.main_activity);
        Log.i("MainActivity: ", "Layout set");

        //initFragments();

        // Checks if saved instanceState exists and assumes that if yes it should check if the inflated view was open.
        if(savedInstanceState == null) {
            initFragments(0);
            Log.e("SavedBundleState", "Null");
        }else {
            initFragments(1);
            position = dataHandler.getData();
           Log.e("SavedBundleState", savedInstanceState.toString());

            //listFragment.setStateChange(true);
            //listFragment.setPosition(position);

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
        dataHandler.setData(position);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        //manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
                back();
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
        if(inflatedState) {
            Log.i("Button Press: ", "Back");
            back();
            getActionBar().setDisplayHomeAsUpEnabled(false);
            inflatedState = false;
            getActionBar().setTitle("Visual Radio");
        }
    }
    // END ANDROID SPECIFIC METHODS

    /**
     * Adds Fragments to views or checks if Fragments already exist and re uses them
     */
    private void initFragments(int runType) {
        switch (runType) {
            case 0:
                // Fragment that drives the list View and its contents
                listFragment = (ListViewFragment) manager.findFragmentByTag("main_index_list");

                if(findViewById(R.id.list_fragment) != null) {
                    if(listFragment == null) {
                        listFragment = new ListViewFragment();
                        manager.beginTransaction().replace(R.id.list_fragment, listFragment, "main_index_list").commit();
                    }

                }
                break;
            case 1:
                break;
        }

        // Fragment that contains the media player
        player = (PlayerFragment) manager.findFragmentById(R.id.player_fragment);

        if (findViewById(R.id.player_fragment) != null) {
            if (player == null) {
                player = new PlayerFragment();
                manager.beginTransaction().replace(R.id.player_fragment, player, "fragment_player").commit();
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
                manager.beginTransaction().replace(R.id.player_fragment, player, "fragment_player").commit();
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
    // NOTE Backstack removed TODO
    public void inflateView(ModelBase mb, String id) {

        inflatedState = true;
        inflatedData = mb;

        switch (id) {
            case Constants.INFLATED_TRAFFIC:
                if (inflatedList == null) {
                    inflatedList = new InflatedListFragment();
                    //manager.beginTransaction().replace(R.id.list_fragment, inflatedList, "fragment_list_inflated").addToBackStack(null).commit();
                    manager.beginTransaction().replace(R.id.list_fragment, inflatedList, "fragment_traffic").commit();
                }else {
                    manager.beginTransaction().replace(R.id.list_fragment, inflatedList, "fragment_traffic").commit();
                }
                break;

            case Constants.INFLATED_MUSIC:
                if (inflatedMusic == null) {
                    inflatedMusic = new InflatedMusicFragment();
                    //manager.beginTransaction().replace(R.id.list_fragment, inflatedView, "fragment_inflated_view").addToBackStack(null).commit();
                    manager.beginTransaction().replace(R.id.list_fragment, inflatedMusic, "fragment_music").commit();
                }else {
                    manager.beginTransaction().replace(R.id.list_fragment, inflatedMusic, "fragment_music").commit();
                }
                break;

            case Constants.INFLATED_NEWS:
                if (inflatedNews == null) {
                    inflatedNews = new InflatedNewsFragment();
                    //manager.beginTransaction().replace(R.id.list_fragment, inflatedView, "fragment_inflated_view").addToBackStack(null).commit();
                    manager.beginTransaction().replace(R.id.list_fragment, inflatedNews, "fragment_news").commit();
                }else {
                    manager.beginTransaction().replace(R.id.list_fragment, inflatedNews, "fragment_news").commit();
                }
                break;
        }
    }


    /**
     * Sets the position of the lsitView each time the ListFragment's onPause methods is called
     * @param position
     */
    public void setPosition(Bundle position) {
        this.position = position;
    }

    /**
     * Set the detail for each respective Inflated View, called by each fragment to ensure no null pointer exceptions happen
     * due to view not being inflated yet
     * @param id
     */
    @Override
    public void initView(String id) {
        switch(id) {
            case "InflatedList":
                inflatedList.setDetail(inflatedData);
                break;
            case "InflatedMusic":
                inflatedMusic.setDetail(inflatedData);
                break;
            case "InflatedNews":
                inflatedNews.setDetail(inflatedData);
                break;
        }
    }

    /**
     * Functionality for navigating back to the index List
     * Method called by both the up navigation button and back button
     * Currently never navigated more than one view away from list so always inflating the same fragment is fine
     */
    @Override
    public void back() {
        listFragment = (ListViewFragment) manager.findFragmentByTag("main_index_list");

        if(findViewById(R.id.list_fragment) != null) {
            if(listFragment == null) {
                listFragment = new ListViewFragment();
                manager.beginTransaction().replace(R.id.list_fragment, listFragment, "main_index_list").commit();
            }
            listFragment.refocused(position, true);
        }
    }
}



