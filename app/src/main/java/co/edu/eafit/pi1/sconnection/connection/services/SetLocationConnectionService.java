package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.eafit.pi1.sconnection.connection.utils.NetworkOperationStatus;

/**
 * Created by tflr on 10/11/15.
 */
public class SetLocationConnectionService extends IntentService {

    private final int FIVE_SECONDS = 5000;

    private String uname;
    private StringBuffer url;
    private Handler handler;

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
        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");

        uname = intent.getStringExtra("username");
        StringBuffer postParams = new StringBuffer();
        postParams.append("name=" + uname);


        if (!uname.isEmpty()){
            receiver.send(NetworkOperationStatus.STATUS_RUNNING.code, Bundle.EMPTY);

            if (longitude != null && latitude != null){
                postParams.append("&latitude=" + latitude + "&longitude=" + longitude);
                Bundle bundle = new Bundle();
                bundle.putString("latitude", latitude);
                bundle.putString("longitude", longitude);
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

        this.stopSelf();
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
