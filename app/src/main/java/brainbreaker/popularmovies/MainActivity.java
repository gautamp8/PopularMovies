package brainbreaker.popularmovies;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    GridView moviegrid;
    String[] movieimage ={"http://dl.ahang-film.com/vay/film/5/Interstellar%202014/interstellar.png",
            "http://dl.ahang-film.com/vay/film/5/Interstellar%202014/interstellar.png",
            "http://dl.ahang-film.com/vay/film/5/Interstellar%202014/interstellar.png",
            "http://dl.ahang-film.com/vay/film/5/Interstellar%202014/interstellar.png"};
    String[] web = {
            "Google",
            "Github",
            "Instagram",
            "Facebook",
    } ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomGrid adapter = new CustomGrid(MainActivity.this, web, movieimage);
        moviegrid=(GridView)findViewById(R.id.moviegrid);
        moviegrid.setAdapter(adapter);
        moviegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
