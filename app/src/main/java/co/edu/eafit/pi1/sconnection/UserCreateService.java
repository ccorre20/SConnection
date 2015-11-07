package co.edu.eafit.pi1.sconnection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import co.edu.eafit.pi1.sconnection.connection.services.SetServiceService;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;


public class UserCreateService extends Activity implements Receiver,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    final int   SEARCH_PROVIDER_REQUEST_CODE = 1;

    String      uname;
    String      password;
    TextView    provider;
    EditText    longitude;
    EditText    latitude;
    EditText    message;
    EditText    type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_service);
        uname       = getIntent().getStringExtra("username");
        provider    = (TextView)findViewById(R.id.user_create_service_provider_text_view);
        longitude   = (EditText)findViewById(R.id.user_create_service_longitude_edittext);
        latitude    = (EditText)findViewById(R.id.user_create_service_latitude_edittext);
        message     = (EditText)findViewById(R.id.user_create_service_message_edittext);
        type        = (EditText)findViewById(R.id.user_create_service_type_edittext);
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

    public void onAutoClick(View view){

    }

    public void onSendClick(View view){
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SetServiceService.class);
        intent.putExtra("uname", uname);
        intent.putExtra("provider", provider.getText().toString());
        intent.putExtra("longitude", longitude.getText().toString());
        intent.putExtra("latitude", latitude.getText().toString());
        intent.putExtra("message", message.getText().toString());
        intent.putExtra("type", type.getText().toString());
        startService(intent);
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
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
