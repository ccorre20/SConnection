package co.edu.eafit.pi1.sconnection;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceConfirm extends AppCompatActivity implements OnMapReadyCallback{

    JSONObject jsonObject;
    TextView t1, t2;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_confirm);
        t1 = (TextView) findViewById(R.id.textView8);
        t2 = (TextView) findViewById(R.id.textView9);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("json"));
            if(jsonObject != null) {
                t1.setText(jsonObject.getJSONObject("provider").getString("name"));
                t2.setText(jsonObject.getString("s_date"));
                latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
                        Double.parseDouble(jsonObject.getString("longitude")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
            .title("Lugar del servicio")
            .position(latLng)
            );
    }
}
