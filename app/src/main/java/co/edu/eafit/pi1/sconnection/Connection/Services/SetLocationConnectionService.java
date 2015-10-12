package co.edu.eafit.pi1.sconnection.Connection.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.eafit.pi1.sconnection.Connection.Utils.NetworkOperationStatus;
import co.edu.eafit.pi1.sconnection.Extras.ActivityExtra;
import co.edu.eafit.pi1.sconnection.LocationManager.LocationServiceManager;

/**
 * Created by tflr on 10/11/15.
 */
public class SetLocationConnectionService extends IntentService {

    private final int FIVE_SECONDS = 5000;

    private String uname;
    private StringBuffer url;
    private Handler handler;
    private LocationServiceManager locationServiceManager;

    public SetLocationConnectionService(){
        super(SetLocationConnectionService.class.getName());
        uname = new String();
        url = new StringBuffer();
        url.append("https://sc-b.herokuapp.com/api/v1/locations/");
        handler = new Handler();
    }

    @Override
    public void onHandleIntent(Intent intent){
        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");

        ActivityExtra appCompatActivity = (ActivityExtra)
                                            intent.getParcelableExtra("appCompatActivity");
        locationServiceManager = new LocationServiceManager(appCompatActivity.getAppCompatActivity());

        uname = intent.getStringExtra("username");
        StringBuffer postParams = new StringBuffer();
        postParams.append("name=" + uname);

        locationServiceManager.googleApiClient();
        while(!locationServiceManager.mGoogleApiClient.isConnected()){
            locationServiceManager.connect();
        }

        if (!uname.isEmpty()){
            receiver.send(NetworkOperationStatus.STATUS_RUNNING.code, Bundle.EMPTY);
            String [] location = locationServiceManager
                    .getCoordinates()
                    .substring(0,locationServiceManager.getCoordinates().length()-3)
                    .split("s");

            if (location.length == 2){
                postParams.append("&latitude=" + location[0] + "&longitude=" + location[1]);
                Bundle bundle = new Bundle();
                bundle.putString("latitude", location[0]);
                bundle.putString("longitude", location[1]);
                receiver.send(NetworkOperationStatus.STATUS_FINISHED.code, bundle);
                try {
                    sendPost(postParams.toString());
                } catch(IOException ioe){
                    receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
                    ioe.printStackTrace();
                }
            }
        }else{
            receiver.send(NetworkOperationStatus.STATUS_NAME_ERROR.code, Bundle.EMPTY);
        }
    }

    /*private void scheduleSendLocation(final String postParams){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    sendPost(postParams);
                    handler.postDelayed(this, FIVE_SECONDS);
                } catch(IOException ioe){
                    handler.removeCallbacksAndMessages(null);
                    ioe.printStackTrace();
                }
            }
        }, FIVE_SECONDS);
    } */


    private boolean sendPost (String urlParams) throws IOException {
        URL url = new URL(this.url.toString());
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        // Request Header
        con.setRequestMethod("POST");
        con.setRequestProperty("http.agent", "");

        //Send post request
        con.setDoOutput(true);
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
        dos.writeBytes(urlParams);
        dos.flush();
        dos.close();

        int responseCode = con.getResponseCode();

        // Return true if request was done successfully
        return responseCode == 200 || responseCode == 201;
    }
}
