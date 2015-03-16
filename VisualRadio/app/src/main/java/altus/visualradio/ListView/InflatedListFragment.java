package altus.visualradio.ListView;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import altus.visualradio.R;

/**
 * Created by altus on 2015/03/06.
 */
// TODO Fill view in thread
public class InflatedListFragment extends ListFragment {
    private Traffic traffic;
    private InflatedListAdapter listAdapter;

    public fragmentCallback callback;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (fragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    public interface fragmentCallback {
        public void initView(String id);
        public void back();
    }

    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            traffic = (Traffic) savedInstanceState.getSerializable("viewData");
        }
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        listAdapter = new InflatedListAdapter(getActivity().getApplicationContext());
        // Main Activity needs to extend the ListActivity
        setListAdapter(listAdapter);

        if(savedInstanceState == null) {
            callback.initView("InflatedList");
        } else {
            listAdapter.setData(traffic);
        }
        return inflater.inflate(R.layout.fragment_inflated_list, container, false);
    }

    public void onStart() {
        super.onStart();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setDetail(ModelBase mb) {
        traffic = (Traffic) mb;
        listAdapter.setData(traffic);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveInstanceState (Bundle outState) {
        outState.putSerializable("viewData", traffic);
        super.onSaveInstanceState(outState);
    }
}
