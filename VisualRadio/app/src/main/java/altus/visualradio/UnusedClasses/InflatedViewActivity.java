/*
package altus.visualradio.ListView;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;;

import altus.visualradio.MainListingActivity;
import altus.visualradio.R;

*/
/**
 * Created by altus on 2015/01/12.
 * Handles the layout for the inflated views
 *//*

public class InflatedViewActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inflated_view_layout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        expandedTextFill();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button

            case android.R.id.home:
                Log.d("HOME HIT", "TRUE");
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    */
/**
     * Receives the selected position ModelBase object and uses the data as needed
     *//*

    private void expandedTextFill() {
        ModelBase item = (ModelBase) getIntent().getSerializableExtra("itemData");
        TextView headerText = (TextView) findViewById(R.id.text_title);
        TextView textBody = (TextView) findViewById(R.id.text_body);
        headerText.setText(item.title);
        if(item.type.equals("music")) {
            Music m = (Music) item;
            textBody.setText(m.artist);
        }else if (item.type.equals("post")) {
            Post p = (Post) item;
            textBody.setText(Html.fromHtml(p.content));
        }
        View view = (View) findViewById(R.id.view_layout);
        textBody.setMovementMethod(new ScrollingMovementMethod());


        // TODO needs to access list Image cache and be able to call a download if the file does not exist
        // TODO needs to place defaut image if none found
        Bitmap image = BitmapFactory.decodeFile(item.imageDir + "/" + item.imageName);
        ImageView imageView = (ImageView) findViewById(R.id.index_picture);
        imageView.setImageBitmap(image);
    }

}
*/
