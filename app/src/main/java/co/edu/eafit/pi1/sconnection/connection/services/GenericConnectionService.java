package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import co.edu.eafit.pi1.sconnection.connection.utils.NetworkOperationStatus;
import co.edu.eafit.pi1.sconnection.exceptions.NetworkException;

/**
 * Created by ccr185 on 10/30/15.
 */
public class GenericConnectionService extends IntentService{

    //TODO Fix everything here so that we can use one service instead of a million.

    public GenericConnectionService(){
        super(GenericConnectionService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");
        if (receiver != null) {
            String httpUrl = intent.getStringExtra("url");
            String httpMethod = intent.getStringExtra("httpMethod");
            String httpParams = intent.getStringExtra("httpParams");

            Bundle bundle = new Bundle();
            ArrayList<String> result = null;

            receiver.send(NetworkOperationStatus.STATUS_RUNNING.code, Bundle.EMPTY);
            if (httpMethod != null){
                if (httpMethod.equals("GET")){
                    try {
                        result = sendGet(httpUrl, httpParams);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else if (httpMethod.equals("POST")){
                    try {
                        result = sendPost(httpUrl, httpParams);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    receiver.send(NetworkOperationStatus.STATUS_NETWORK_ERROR.code, Bundle.EMPTY);
                }
            }
        }
    }

    private ArrayList<String> sendGet(String httpUrl, String httpParams) throws IOException, JSONException {
        //TODO finish method
        URL url = new URL(httpUrl+httpParams);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("http.agent", "");

        int responseCode = connection.getResponseCode();

        ArrayList<String> arrayList = new ArrayList<>();
        if(responseCode == 200 || responseCode == 201){
            String json = getJSON(connection.getInputStream());
            JSONObject obj = new JSONObject(json);
        }
        return null;
    }

    private ArrayList<String> sendPost(String httpUrl, String httpParams) throws IOException{
        //TODO finish method
        URL url = new URL(httpUrl+httpParams);
        return null;
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
