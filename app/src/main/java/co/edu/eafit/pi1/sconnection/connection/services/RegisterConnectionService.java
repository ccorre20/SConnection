package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.eafit.pi1.sconnection.connection.utils.NetworkOperationStatus;

/**
 * Created by tflr on 10/10/15.
 */
public class RegisterConnectionService extends IntentService {

    private StringBuffer url;
    private String uname;

    private static final String TAG = "RConnectionService";

    public RegisterConnectionService() {
        super(RegisterConnectionService.class.getName());
        url = new StringBuffer();
        url.append("https://sc-b.herokuapp.com/api/v1/users/?");
        uname = new String();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "SERVICE STARTED");

        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");
        uname = intent.getStringExtra("username");
        Bundle bundle = new Bundle();
        StringBuffer sb = new StringBuffer();
        sb.append("name="+intent.getStringExtra("username")+"&");
        sb.append("user_t="+intent.getStringExtra("type")+"&");
        sb.append("password="+intent.getStringExtra("password"));

        boolean result;

        if(!uname.isEmpty()){
            receiver.send(NetworkOperationStatus.STATUS_RUNNING.code, Bundle.EMPTY);
            try{
                result = sendPost(sb.toString());
                bundle.putBoolean("done", result);
                receiver.send(NetworkOperationStatus.STATUS_FINISHED.code, bundle);
            } catch (IOException e){
                receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
                e.printStackTrace();
            }
        } else {
            receiver.send(NetworkOperationStatus.STATUS_NAME_ERROR.code, Bundle.EMPTY);
        }
        Log.d(TAG, "SERVICE STOPPED");
        this.stopSelf();
    }

    private boolean sendPost (String urlParams) throws IOException{
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
