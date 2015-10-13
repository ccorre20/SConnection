package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.eafit.pi1.sconnection.connection.utils.NetworkOperationStatus;
import co.edu.eafit.pi1.sconnection.exceptions.NetworkException;

/**
 * Created by tflr on 10/11/15.
 */
public class GetLocationConnectionService extends IntentService{

    private StringBuffer    url;
    private String          uname;
    private Handler         handler;

    public GetLocationConnectionService(){
        super(GetLocationConnectionService.class.getName());

        url = new StringBuffer();
        uname = new String();
        handler = new Handler();

        url.append("https://sc-b.herokuapp.com/api/v1/locations/?name=");
    }

    @Override
    protected void onHandleIntent(Intent intent){

        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");
        uname = intent.getStringExtra("username");
        Bundle bundle = new Bundle();

        if(!uname.isEmpty()){
            receiver.send(NetworkOperationStatus.STATUS_RUNNING.code, Bundle.EMPTY);
            try {
                String[] result = sendGet(uname);
                bundle.putString("longitude", result[0]);
                bundle.putString("latitude", result[1]);
                receiver.send(NetworkOperationStatus.STATUS_FINISHED.code, bundle);
            } catch (IOException e){
                receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
                e.printStackTrace();
            } catch (NetworkException e) {
                receiver.send(NetworkOperationStatus.STATUS_NETWORK_ERROR.code, Bundle.EMPTY);
                e.printStackTrace();
            } catch (JSONException e) {
                receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
                e.printStackTrace();
            }
            scheduleGetLocation(uname, receiver);
        } else {
            receiver.send(NetworkOperationStatus.STATUS_NAME_ERROR.code, Bundle.EMPTY);
        }

        this.stopSelf();
    }

    private void scheduleGetLocation(final String postParams,
                                     final ResultReceiver receiver){
        /*
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bundle = new Bundle();
                    String[] result = sendGet(postParams);
                    bundle.putString("longitude", result[0]);
                    bundle.putString("latitude", result[1]);
                    receiver.send(NetworkOperationStatus.STATUS_FINISHED.code, bundle);
                    handler.postDelayed(this, FIVE_SECONDS);
                } catch (IOException e) {
                    receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
                    handler.removeCallbacksAndMessages(null);
                    e.printStackTrace();
                } catch (NetworkException e) {
                    receiver.send(NetworkOperationStatus.STATUS_NETWORK_ERROR.code, Bundle.EMPTY);
                    handler.removeCallbacksAndMessages(null);
                    e.printStackTrace();
                } catch (JSONException e) {
                    receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
                    handler.removeCallbacksAndMessages(null);
                    e.printStackTrace();
                }
            }
        }, FIVE_SECONDS);
        */
        try {
            Bundle bundle = new Bundle();
            String[] result = sendGet(postParams);
            bundle.putString("longitude", result[0]);
            bundle.putString("latitude", result[1]);
            receiver.send(NetworkOperationStatus.STATUS_FINISHED.code, bundle);
        }catch(Exception e){e.printStackTrace();}
    }

    private String[] sendGet (String uname) throws IOException, JSONException, NetworkException{
        StringBuffer urlS = new StringBuffer();
        urlS.append(this.url);
        urlS.append(uname);
        URL url = new URL(urlS.toString());
        String[] response = new String[2];
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

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
            response[0] = obj.getString("longitude");
            response[1] = obj.getString("latitude");
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
