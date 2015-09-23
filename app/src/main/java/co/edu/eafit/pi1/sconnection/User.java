package co.edu.eafit.pi1.sconnection;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import co.edu.eafit.pi1.sconnection.LocationManager.LocationServiceManager;

public class User extends AppCompatActivity implements OnMapReadyCallback {

    private Button b, b2;
    private ProgressBar progressBar;
    private TextView progressTxt, resultTxt;
    private getDataTask task;
    private LatLng user, provider;
    private GoogleMap map;
    private LocationServiceManager locationServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        b = (Button) findViewById(R.id.conn_Button);
        b2 = (Button) findViewById(R.id.button_cancel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressTxt = (TextView) findViewById(R.id.Progress_txt);
        resultTxt = (TextView) findViewById(R.id.textView2);
        task = null;
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_frag);
        mapFragment.getMapAsync(this);
        user = null;
        provider = null;
    }

    public void setProvider(LatLng provider){
        this.provider = provider;
    }

    @Override
    protected void onStart(){
        locationServiceManager = new LocationServiceManager(this);
        locationServiceManager.googleApiClient();
        locationServiceManager.connect();
        super.onStart();
    }

    public void mapClickListener(View view){
        if(user != null) {
            map.addMarker(new MarkerOptions()
                    .title("User")
                    .position(user));
        }else{
            while(!locationServiceManager.mGoogleApiClient.isConnected()){
                 locationServiceManager.connect();
            }
            String [] locUser = locationServiceManager
                            .getCoordinates()
                            .substring(0,locationServiceManager.getCoordinates().length()-3)
                            .split("s");
            if(locUser.length == 2){
                user = new LatLng(Double.parseDouble(locUser[0]),Double.parseDouble(locUser[1]));
                mapClickListener(view);
            }else{
                user = null;
            }
        }
        if(provider != null) {
            map.addMarker(new MarkerOptions()
                    .title("Provider")
                    .position(provider));
        }

        if(user != null && provider != null){
            float [] dist = new float[1];
            Location.distanceBetween(
                    user.latitude,
                    user.longitude,
                    provider.latitude,
                    provider.longitude,
                    dist);
            resultTxt.setText(Float.toString(dist[0]));
        }
    }

    public void connClickListener(View view){
        task = new getDataTask(
                progressBar,
                progressTxt,
                resultTxt,
                b,
                b2,
                this);
        task.execute();
    }

    public void cancelClickListener(View view){
        if(task != null) {
            task.cancel(true);
            task = null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
    }

    private class getDataTask extends AsyncTask<Void, Integer, Void> {

        private LatLng provider;
        private ProgressBar progressBar;
        private TextView progressTxt, resultTxt;
        private Button b, b2;
        private int status;
        private String result, publish;
        private User user;

        public getDataTask(
                ProgressBar progressBar,
                TextView progressTxt,
                TextView resultTxt,
                Button b,
                Button b2,
                User user){
            this.progressBar = progressBar;
            this.progressTxt = progressTxt;
            this.resultTxt = resultTxt;
            provider = null;
            this.b = b;
            this.b2 = b2;
            status = 0;
            this.user = user;
        }

        protected void onPreExecute() {
            b.setEnabled(false);
            b2.setVisibility(View.VISIBLE);
            b2.setEnabled(true);
            progressBar.setVisibility(View.VISIBLE);
            progressTxt.setVisibility(View.VISIBLE);
            progressBar.setEnabled(true);
            progressTxt.setEnabled(true);
        }

        protected Void doInBackground(Void... v) {
            publishProgress(0);
            ServerSocket ss = null;
            DataInputStream dis = null;
            Socket s = null;
            try {
                ss = new ServerSocket(8888);
                publishProgress(25);
            } catch (Exception e) {
                Log.v("test", e.getMessage());
            }
            try {
                if(ss != null) {
                    ss.setSoTimeout(30000);
                }else{
                    return null;
                }
                s = ss.accept();
                dis = new DataInputStream(s.getInputStream());
                publishProgress(50);
                result = "";
                publish = "";
                while((result = dis.readUTF()) != null){
                    if(isCancelled()){
                        result = "";
                        publish = "";
                        return null;
                    }
                    publish += result;
                    Log.v("publish", result);
                    if(result.contains("sss")){
                        break;
                    }
                }
                publishProgress(75);
            } catch (SocketTimeoutException e) {
                Log.v("test", "timeout");
                this.cancel(true);
            }catch (Exception e){
                Log.v("test", e.toString());
                this.cancel(true);
            } finally {
                if(s != null){
                    try{
                        s.close();
                    } catch (Exception e){
                        //Log.v("test", e.getMessage());
                    }
                }
                if(dis != null){
                    try{
                        dis.close();
                    } catch(Exception e){
                        //Log.v("test", e.getMessage());
                    }
                }
                if(ss != null) {
                    try {
                        ss.close();
                    } catch (Exception e) {
                        //Log.v("test", e.getMessage());
                    }
                }
            }
            return null;
        }

        protected void onCancelled() {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setEnabled(false);
            progressTxt.setText("Cancelled");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.v("test", e.getMessage());
            }
            progressTxt.setEnabled(false);
            progressTxt.setVisibility(View.INVISIBLE);
            b.setEnabled(true);
            b2.setEnabled(false);
            b2.setVisibility(View.INVISIBLE);
        }

        protected void onProgressUpdate(Integer... progress){
            progressBar.setProgress(progress[0]);
            switch (status){
                case 0:
                {
                    progressTxt.setText(R.string.creating_server);
                    status++;
                    break;
                }
                case 1:
                {
                    progressTxt.setText(R.string.waiting_for_connection);
                    status++;
                    break;
                }
                case 2:
                {
                    progressTxt.setText(R.string.recieveing_data);
                    status++;
                    break;
                }
                case 3:
                {
                    progressTxt.setText(R.string.connection_complete);
                    status++;
                    break;
                }
            }
        }


        protected void onPostExecute(Void v){
            progressBar.setProgress(100);
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setEnabled(false);
            progressTxt.setText("Done");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.v("test", e.getMessage());
            }
            progressTxt.setEnabled(false);
            progressTxt.setVisibility(View.INVISIBLE);
            b.setEnabled(true);
            b2.setEnabled(false);
            b2.setVisibility(View.INVISIBLE);
            Log.v("test", publish);

            resultTxt.setText("GEOLOCALIZADO");
            publish = publish.substring(0, publish.length()-3);
            String [] parts = publish.split("s");
            String lat = parts[0];
            String lng = parts[1];
            provider = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            user.setProvider(provider);
        }
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
