package co.edu.eafit.pi1.sconnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import co.edu.eafit.pi1.sconnection.connection.services.HttpRequest;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;

public class UserServiceDetail extends Activity implements OnMapReadyCallback, Receiver {

    TextView t1, t2, t3, t4;
    JSONObject jsonObject;
    LatLng location;
    GoogleMap googleMap;
    Button rateButton;
    RatingBar ratingBar;
    boolean isProvider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_service_detail);
        t1 = (TextView)findViewById(R.id.user_service_detail_user_id_txt);
        t2 = (TextView)findViewById(R.id.user_service_detail_provider_id_txt);
        t3 = (TextView)findViewById(R.id.user_service_detail_service_type_txt);
        t4 = (TextView)findViewById(R.id.user_service_detail_service_status_txt);
        rateButton = (Button)findViewById(R.id.rate_button);
        ratingBar = (RatingBar)findViewById(R.id.service_ratingBar);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("json"));
            t1.setText(jsonObject.getJSONObject("user").getString("name"));
            t2.setText(jsonObject.getJSONObject("provider").getString("name"));
            t3.setText(jsonObject.getString("s_t"));
            t4.setText(jsonObject.getString("s_status"));
            location = new LatLng(
                    Double.parseDouble(jsonObject.getString("latitude")),
                    Double.parseDouble(jsonObject.getString("longitude"))
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.user_service_detail_fragment);
        mapFragment.getMapAsync(this);

        if(getIntent().getBooleanExtra("isProvider", false)){
            ratingBar.setEnabled(false);
            ratingBar.setVisibility(View.INVISIBLE);
            rateButton.setEnabled(false);
            rateButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    private void setMarker(){
        if(location != null && googleMap != null){
            googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title("Lugar del Servicio")
            );
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setMarker();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch(resultCode){
            case 1: //STATUS_FINISHED
                if(resultData.getBoolean("result")){
                    AlertDialog alertDialog =
                            createAlertDialog(R.string.alert_dialog_register_successful_warning,
                                    R.string.alert_dialog_rate_successful);
                    alertDialog.show();
                    break;  //If an error does not occur, then case 4 is not verified
                }
            case 4: //STATUS_GENERAL_ERROR
                AlertDialog alertDialog =
                        createAlertDialog(R.string.alert_dialog_register_successful_warning,
                                R.string.alert_dialog_rate_fail);
                alertDialog.show();

        }
    }

    public void onRateClick(View view){
        CSResultReceiver mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        float rating = ratingBar.getRating();
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, HttpRequest.class);
        intent.putExtra("url", "https://sc-b.herokuapp.com/api/v1/users/?");
        intent.putExtra("urlParams", "provider=" + t2.getText().toString() + "&rating=" + rating);
        intent.putExtra("type", "POST");
        intent.putExtra("mReceiver", mReceiver);
        startService(intent);
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
