package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;

public class User extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Button              b, b2, b3;
    private GoogleMap           map;
    private String              username;
    private Bundle              extra;
    private CSResultReceiver    mReceiver;
    private Location            lastKnownLocation;
    private LocationRequest     mLocationRequest;
    private Location            previous;
    private String              latitude;
    private String              longitude;
    private Handler             handler;
    private Button              arrived;
    private boolean             mResolvingError = false;
    private final int           REQUEST_RESOLVE_ERROR = 1001;
    public GoogleApiClient      mGoogleApiClient;

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
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
        Intent i = new Intent(this, UserCreateService.class);
        i.putExtra("username", username);
        startActivity(i);
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
