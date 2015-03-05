package altus.visualradio.ListView;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import altus.visualradio.R;

/**
 * Created by altus on 2015/03/03.
 *
 * Fragment that handles the data when view is inflated
 */
public class InflatedViewFragment extends Fragment {
    private ModelBase modelBase;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_inflated, container, false);
    }

    public void onStart() {
        super.onStart();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        initDetail();
    }

    public void setDetail(ModelBase mb) {
        modelBase = mb;
    }

    /**
     * Assigns data to the views in the layout
     */
    public void initDetail() {
        TextView headerText = (TextView) getView().findViewById(R.id.text_title);
        TextView textBody = (TextView) getView().findViewById(R.id.text_body);

        headerText.setText(modelBase.title);

        if(modelBase.type.equals("music")) {
            Music m = (Music) modelBase;
            textBody.setText(m.artist);
            getActivity().getActionBar().setTitle("Music");
        }else if (modelBase.type.equals("post")) {
            Post p = (Post) modelBase;
            textBody.setText(Html.fromHtml(p.content));
            getActivity().getActionBar().setTitle("News");
        }
        textBody.setMovementMethod(new ScrollingMovementMethod());

        // Opens the image on the filesystem
        // TODO global cache and image loader still not created
        Bitmap image = BitmapFactory.decodeFile(modelBase.imageDir + "/" + modelBase.imageName);
        ImageView imageView = (ImageView) getView().findViewById(R.id.index_picture);
        imageView.setImageBitmap(image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
