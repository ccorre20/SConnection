package co.edu.eafit.pi1.sconnection;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class UserServiceDetail extends Activity implements OnMapReadyCallback {

    TextView t1, t2, t3, t4;
    String json;
    JSONObject jsonObject;
    LatLng location;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_service_detail);
        t1 = (TextView)findViewById(R.id.user_service_detail_user_id_txt);
        t2 = (TextView)findViewById(R.id.user_service_detail_provider_id_txt);
        t3 = (TextView)findViewById(R.id.user_service_detail_service_type_txt);
        t4 = (TextView)findViewById(R.id.user_service_detail_service_status_txt);
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
}
