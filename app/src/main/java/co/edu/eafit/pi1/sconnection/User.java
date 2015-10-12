package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class User extends AppCompatActivity implements OnMapReadyCallback {

    private Button b, b2, b3;
    private GoogleMap map;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        b = (Button) findViewById(R.id.user_search_button);
        b2 = (Button) findViewById(R.id.user_view_services_button);
        b3 = (Button) findViewById(R.id.user_create_service_button);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(this);
        username = getIntent().getStringExtra("name");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public void onProviderSearchClick(View view){
        Intent i = new Intent(this, UserProviderSearch.class);
        startActivity(i);
    }

    public void onServiceListClick(View view){
        Intent i = new Intent(this, UserServiceList.class);
        i.putExtra("username", username);
        startActivity(i);
    }

    public void onServiceCreateClick(View view){

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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
