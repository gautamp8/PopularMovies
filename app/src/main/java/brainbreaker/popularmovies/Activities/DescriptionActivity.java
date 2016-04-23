package brainbreaker.popularmovies.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import brainbreaker.popularmovies.R;

public class DescriptionActivity extends AppCompatActivity {
    String TAG = "Description Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Log.e(TAG,"In onCreate Start");
        Intent intent = getIntent();
        String movietitle = intent.getStringExtra("MovieTitle");

        /** CHANGING THE TITLE OF ACTION BAR AND ENABLING THE BACK BUTTON**/
        getSupportActionBar().setTitle(movietitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /** WHEN CLICKED ON BACK BUTTON **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // BACK CLICKED. GO TO HOME.
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //FINISH THE CURRENT ACTIVITY
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
