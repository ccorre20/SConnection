package co.edu.eafit.pi1.sconnection;

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

    JSONObject jsonObject;
    TextView t1, t2;
    LatLng latLng;
    GoogleMap gmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_confirm);
        t1 = (TextView) findViewById(R.id.textView8);
        t2 = (TextView) findViewById(R.id.textView9);
        CSResultReceiver mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("json"));
            if(jsonObject != null) {
                t1.setText(jsonObject.getJSONObject("provider").getString("name"));
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
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, HttpRequest.class);
        intent.putExtra("url", "https://sc-b.herokuapp.com/api/v1/users/?");
        intent.putExtra("urlParams", "name=" + username + "&ranking=true");
        intent.putExtra("type", "GET");
        intent.putExtra("valuesToGet", new String[]{"rating"});
        intent.putExtra("mReceiver", mReceiver);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

    }

    private void updateGMap(){
        gmap.addMarker(new MarkerOptions()
                        .title("Lugar del servicio")
                        .position(latLng)
        );
        gmap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        if(latLng != null) {
            updateGMap();
        }
    }

}
