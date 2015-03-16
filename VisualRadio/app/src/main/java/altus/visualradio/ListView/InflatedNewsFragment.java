package altus.visualradio.ListView;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import altus.visualradio.R;

/**
 * Created by altus on 2015/03/03.
 *
 * Fragment that handles the data when view is inflated
 */
// TODO Fill view in thread
public class InflatedNewsFragment extends Fragment {
    private ModelBase modelBase;
    public fragmentCallback callback;
    private boolean firstLaunch = true;

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
    }

    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            modelBase = (ModelBase) savedInstanceState.getSerializable("viewData");
            firstLaunch = false;
        } else {
            firstLaunch = true;
        }
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onStart();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        return inflater.inflate(R.layout.fragment_inflated_news, container, false);
    }


    public void onStart() {
        if(firstLaunch) {
            callback.initView("InflatedNews");
        } else {
            initDetail();
        }
        super.onStart();
    }

    public void setDetail(ModelBase mb) {
        modelBase = mb;
        initDetail();
    }

    /**
     * Assigns data to the views in the layout
     */
    public void initDetail() {

        WebView textBody = (WebView) getView().findViewById(R.id.text_body);

            Post p = (Post) modelBase;

            textBody.loadData((p.content), "text/html; charset=UTF-8", null);
            getActivity().getActionBar().setTitle("News");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // return true;
        }
        return super.onOptionsItemSelected(item);    }

    public void onSaveInstanceState (Bundle outState) {
        outState.putSerializable("viewData", modelBase);
        super.onSaveInstanceState(outState);
    }
}
