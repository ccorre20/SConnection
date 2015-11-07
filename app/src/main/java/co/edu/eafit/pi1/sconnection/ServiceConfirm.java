package co.edu.eafit.pi1.sconnection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.eafit.pi1.sconnection.connection.services.HttpRequest;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;

public class ServiceConfirm extends AppCompatActivity implements OnMapReadyCallback, Receiver{

    JSONObject  jsonObject;
    TextView    t1, t2;
    LatLng      latLng;
    GoogleMap   gmap;
    String      username;
    String      providername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_confirm);
        t1 = (TextView) findViewById(R.id.textView8);
        t2 = (TextView) findViewById(R.id.textView9);
        username = getIntent().getStringExtra("username");
        CSResultReceiver mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("json"));
            if(jsonObject != null) {
                providername = jsonObject.getJSONObject("provider").getString("name");
                t1.setText(providername);
                t2.setText(jsonObject.getString("s_date"));
                latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
                        Double.parseDouble(jsonObject.getString("longitude")));
                MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
                mapFragment.getMapAsync(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void registerConfirm(View view){
        CSResultReceiver mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, HttpRequest.class);
        intent.putExtra("url", "https://sc-b.herokuapp.com/api/v1/service_statuses/?");
        intent.putExtra("urlParams", "name=" + username + "&other=" + providername);
        intent.putExtra("type", "POST");
        intent.putExtra("mReceiver", mReceiver);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData){
        switch(resultCode){
            case 1: //STATUS_FINISHED
                if(resultData.getBoolean("result")){
                    AlertDialog alertDialog =
                            createAlertDialog(R.string.alert_dialog_register_successful_warning,
                                    R.string.alert_dialog_confirmation_successful);
                    alertDialog.show();
                    break;  //If an error does not occur, then case 4 is not verified
                }
            case 4: //STATUS_GENERAL_ERROR
                AlertDialog alertDialog =
                        createAlertDialog(R.string.alert_dialog_register_successful_warning,
                                R.string.alert_dialog_confirmation_fail);
                alertDialog.show();
        }
    }

    private void updateGMap(){
        gmap.addMarker(new MarkerOptions()
                        .title("Lugar del servicio")
                        .position(latLng)
        );
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        if(latLng != null) {
            updateGMap();
        }
    }

    private AlertDialog createAlertDialog(int tittle, int message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        //Set the dialog characteristics
        alertBuilder.setMessage(message)
                .setTitle(tittle);

        //Add the dialog buttons
        alertBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();

        return alertDialog;
    }

}
