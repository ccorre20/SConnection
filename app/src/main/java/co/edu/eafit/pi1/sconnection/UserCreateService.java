package co.edu.eafit.pi1.sconnection;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import co.edu.eafit.pi1.sconnection.connection.services.HttpRequest;
import co.edu.eafit.pi1.sconnection.connection.services.SetServiceService;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;


public class UserCreateService extends AppCompatActivity implements Receiver,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    final int   SEARCH_PROVIDER_REQUEST_CODE = 1;

    private String              uname;
    private TextView            provider;
    private EditText            message;
    private EditText            type;
    private TextView            addressTV;
    private ActionBar           actionBar;
    private GoogleApiClient     mGoogleApiClient;
    private Location            mLastLocation;
    private Geocoder            geocoder;
    private double              longitude;
    private double              latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_service);
        uname       = getIntent().getStringExtra("username");
        provider    = (TextView)findViewById(R.id.user_create_service_provider_text_view);
        addressTV   = (TextView)findViewById(R.id.user_create_service_address_textview);
        message     = (EditText)findViewById(R.id.user_create_service_message_edittext);
        type        = (EditText)findViewById(R.id.user_create_service_type_edittext);
        geocoder    = new Geocoder(this, Locale.ENGLISH);
        actionBar   = getSupportActionBar();

        actionBar.setTitle(R.string.user_create_service_button);
        actionBar.setDisplayHomeAsUpEnabled(true);

        buildGoogleApiClient();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SEARCH_PROVIDER_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                provider.setText(data.getStringExtra("providerName"));
            }
        }
    }

    public void onSendClick(View view){

        CSResultReceiver mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        Intent i = new Intent(Intent.ACTION_SYNC, null, this, HttpRequest.class);
        i.putExtra("url", "https://sc-b.herokuapp.com/api/v1/services/?");
        i.putExtra("urlParams", "name=" + uname + "&provider=" + provider.getText().toString() +
                "&type=" + type.getText().toString() + "&latitude=" + String.valueOf(latitude) +
                "&longitude=" + String.valueOf(longitude) + "&message=" + message.getText().toString());
        i.putExtra("type", "POST"); // fixed
        i.putExtra("mReceiver", mReceiver);
        startService(i);
    }

    public void onSearchProviderClick(View view){
        Intent intent = new Intent(this, UserProviderSearch.class);
        startActivityForResult(intent, SEARCH_PROVIDER_REQUEST_CODE);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;
        Toast toast;
        switch (resultCode){
            case 0:
                text = "Enviando";
                toast = Toast.makeText(context, text, duration);
                toast.show();
                break;
            case 1:  //STATUS_FINISHED
                text = "Enviado";
                toast = Toast.makeText(context, text, duration);
                toast.show();
                this.finish();
                break;
        }
    }

    /**************************** Google Play services *******************************************/
    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude     = mLastLocation.getLatitude();
            longitude    = mLastLocation.getLongitude();
            String address = getAddressFromCoordinates(latitude, longitude);
            addressTV.setText(address);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    /**************************** /Google Play services ******************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_create_service, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private String getAddressFromCoordinates(double latitude, double longitude){
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses != null){
                Address address = addresses.get(0);
                StringBuffer strAddress = new StringBuffer();
                for(int i=0; i<address.getMaxAddressLineIndex(); ++i){
                    strAddress.append(address.getAddressLine(i));
                }
                return strAddress.toString();
            }else{
                return "No puede obtenerse direccion";
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
            return "No puede obtenerse direccion";
        }
    }

}
