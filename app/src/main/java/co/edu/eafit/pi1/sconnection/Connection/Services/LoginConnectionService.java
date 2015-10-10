package co.edu.eafit.pi1.sconnection.Connection.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.eafit.pi1.sconnection.Exceptions.NetworkException;

/**
 * Created by tflr on 10/10/15.
 */
public class LoginConnectionService extends IntentService {

    private StringBuffer url;
    private String uname;

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_NETWORK_ERROR = 2;
    public static final int STATUS_NAME_ERROR = 3;
    public static final int STATUS_GENERAL_ERROR = 4;

    private static final String TAG = "RConnectionService";

    public LoginConnectionService() {
        super(LoginConnectionService.class.getName());
        url = new StringBuffer();
        url.append("https://sc-b.herokuapp.com/api/v1/users/?login=true&name=");
        uname = new String();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "SERVICE STARTED");

        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");
        uname = intent.getStringExtra("username");
        Bundle bundle = new Bundle();

        String result = null;

        if(!uname.isEmpty()){
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try{
                result = sendGet(uname);
                bundle.putString("user_t", result);
                receiver.send(STATUS_FINISHED, bundle);
            } catch (IOException e){
                receiver.send(STATUS_GENERAL_ERROR, Bundle.EMPTY);
                e.printStackTrace();
            } catch (NetworkException e) {
                receiver.send(STATUS_NETWORK_ERROR, Bundle.EMPTY);
                e.printStackTrace();
            } catch (JSONException e) {
                receiver.send(STATUS_GENERAL_ERROR, Bundle.EMPTY);
                e.printStackTrace();
            }
        } else {
            receiver.send(STATUS_NAME_ERROR, Bundle.EMPTY);
        }
        Log.d(TAG, "SERVICE STOPPED");
        this.stopSelf();
    }

    private String sendGet (String uname) throws IOException, JSONException, NetworkException{
        StringBuffer urlS = new StringBuffer();
        urlS.append(this.url);
        urlS.append(uname);
        URL url = new URL(urlS.toString());
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        String response;

        // Request Header
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("http.agent", "");

        int responseCode = con.getResponseCode();

        // Parse JSON
        if(responseCode == 200 || responseCode == 201){
            String json = getJSON(con.getInputStream());
            JSONObject obj = new JSONObject(json);
            response = obj.getString("user_t");
        } else {
            NetworkException e = new NetworkException("Could not get JSON");
            throw e;
        }

        return response;
    }

    private String getJSON(InputStream inputStream) throws IOException {
        StringBuffer json = new StringBuffer();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStream));

        String input;
        while ((input = in.readLine()) != null){
            json.append(input);
        }
        in.close();

        return json.toString();
    }

}
