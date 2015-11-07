package co.edu.eafit.pi1.sconnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProviderSearchDetail extends Activity implements OnMapReadyCallback{

    JSONObject jsonObject;
    TextView t1, t2;
    LatLng location;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_provider_search_detail);
        t1 = (TextView) findViewById(R.id.user_provider_detail_provider_id_txt);
        t2 = (TextView) findViewById(R.id.user_provider_detail_service_status_txt);
        try {
            jsonObject = new JSONObject(getIntent().getStringExtra("json"));
            t1.setText(jsonObject.getString("name"));
            t2.setText(jsonObject.getString("avail").equals("true") ? "Disponible" : "No Disponible");
            jsonObject = jsonObject.getJSONObject("location");
            location = new LatLng(
                    Double.parseDouble(jsonObject.getString("latitude")),
                    Double.parseDouble(jsonObject.getString("longitude"))
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.user_provider_detail_fragment);
        mapFragment.getMapAsync(this);
    }

    public void onChooseProviderClick(View view){
        Intent providerNameData = new Intent();
        providerNameData.putExtra("providerName", t1.getText().toString());
        setResult(RESULT_OK, providerNameData);
        this.finish();
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
