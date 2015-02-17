package altus.visualradio;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import altus.visualradio.ListView.ModelBase;

/**
 * Created by altus on 2015/02/13.
 * Headless Fragment to carry data on state changes
 */
public class DataHandler extends Fragment {
    private static final int LOADER_ID = 1;
    private List<ModelBase> contents = new ArrayList<>();

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void onDetach() {
        super.onDetach();
    }

    public void setContents(List<ModelBase> contents) {
        this.contents = contents;
    }
    public List<ModelBase> getContents() {
            return this.contents;
    }

}
