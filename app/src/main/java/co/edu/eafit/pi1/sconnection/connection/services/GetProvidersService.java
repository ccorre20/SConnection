package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import co.edu.eafit.pi1.sconnection.connection.utils.NetworkOperationStatus;
import co.edu.eafit.pi1.sconnection.exceptions.NetworkException;

/**
 * Created by ccr185 on 10/11/15.
 */
public class GetProvidersService extends IntentService{
    private StringBuffer url;

    private static final String TAG = "RConnectionService";

    public GetProvidersService() {
        super(GetProvidersService.class.getName());
        url = new StringBuffer();
        url.append("https://sc-b.herokuapp.com/api/v1/users/?only=provider");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "SERVICE STARTED");

        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");
        Bundle bundle = new Bundle();

        ArrayList<String> result = null;

        receiver.send(NetworkOperationStatus.STATUS_RUNNING.code, Bundle.EMPTY);
        try{
            result = sendGet();
            bundle.putStringArrayList("providers", result);
            receiver.send(NetworkOperationStatus.STATUS_FINISHED.code, bundle);
        } catch (IOException | JSONException e){
            receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
            e.printStackTrace();
        } catch (NetworkException e) {
            receiver.send(NetworkOperationStatus.STATUS_NETWORK_ERROR.code, Bundle.EMPTY);
            e.printStackTrace();
        }

        Log.d(TAG, "SERVICE STOPPED");
        this.stopSelf();
    }

    private ArrayList<String> sendGet () throws IOException, JSONException, NetworkException{
        StringBuffer urlS = new StringBuffer();
        urlS.append(this.url);
        URL url = new URL(urlS.toString());
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        // Request Header
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("http.agent", "");

        int responseCode = con.getResponseCode();

        // Parse JSON

        ArrayList<String> arrayList = new ArrayList<>();
        if(responseCode == 200 || responseCode == 201){
            String json = getJSON(con.getInputStream());
            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("users");

            for(int i = 0; i < array.length(); i++){
                arrayList.add(array.getJSONObject(i).toString());
            }

        } else {
            NetworkException e = new NetworkException("Could not get JSON");
            throw e;
        }

        return arrayList;
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
