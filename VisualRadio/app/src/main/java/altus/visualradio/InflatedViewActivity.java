package altus.visualradio;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by altus on 2015/01/12.
 */
public class InflatedViewActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inflated_view_layout);
        expandedTextFill();
    }

    public void expandedTextFill(){
        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString(MainListingActivity.TITLE_KEY());
        TextView headerText = (TextView) findViewById(R.id.text_title);
        headerText.setText(title);
    }

}
