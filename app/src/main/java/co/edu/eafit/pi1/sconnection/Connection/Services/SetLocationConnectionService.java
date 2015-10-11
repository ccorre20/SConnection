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

import co.edu.eafit.pi1.sconnection.LocationManager.LocationServiceManager;

/**
 * Created by tflr on 10/11/15.
 */
public class SetLocationConnectionService extends IntentService {

    public static final int STATUS_RUNNING          = 0;
    public static final int STATUS_FINISHED         = 1;
    public static final int STATUS_NAME_ERROR       = 3;
    public static final int STATUS_GENERAL_ERROR    = 4;

    private final int FIVE_SECONDS = 5000;

    private String uname;
    private StringBuffer url;
    private Handler handler;
    private LocationServiceManager locationServiceManager;

    public SetLocationConnectionService(AppCompatActivity appCompatActivity){
        super(SetLocationConnectionService.class.getName());
        uname = new String();
        url = new StringBuffer();
        url.append("https://sc-b.herokuapp.com/api/v1/locations/");
        locationServiceManager = new LocationServiceManager(appCompatActivity);
        handler = new Handler();
    }

    public SetLocationConnectionService(){
        super(SetLocationConnectionService.class.getName());
        this.stopSelf();
    }

    @Override
    public void onHandleIntent(Intent intent){
        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");
        uname = intent.getStringExtra("username");
        StringBuffer postParams = new StringBuffer();
        postParams.append("name=" + uname);

        locationServiceManager.googleApiClient();
        while(!locationServiceManager.mGoogleApiClient.isConnected()){
            locationServiceManager.connect();
        }

        if (!uname.isEmpty()){
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            String [] location = locationServiceManager
                    .getCoordinates()
                    .substring(0,locationServiceManager.getCoordinates().length()-3)
                    .split("s");

            if (location.length == 2){
                postParams.append("&latitude=" + location[0] + "&longitude=" + location[1]);
                scheduleSendLocation(postParams.toString());
            }
            receiver.send(STATUS_FINISHED, Bundle.EMPTY);
        }else{
            receiver.send(STATUS_NAME_ERROR, Bundle.EMPTY);
        }
    }

    private void scheduleSendLocation(final String postParams){
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
    }


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
