package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.eafit.pi1.sconnection.connection.utils.NetworkOperationStatus;
import co.edu.eafit.pi1.sconnection.exceptions.NetworkException;

/**
 * Created by tflr on 11/4/15.
 */
public class HttpRequest extends IntentService {

    StringBuffer url;
    String       urlParams;
    String       type;
    String[]     valuesToGet;
    Bundle       bundle;

    public HttpRequest(){
        super(HttpRequest.class.getName());

        url         = new StringBuffer();
        urlParams   = new String();
        type        = new String();
        bundle      = new Bundle();
    }
    @Override
    protected void onHandleIntent(Intent intent){
        final ResultReceiver receiver   = intent.getParcelableExtra("mReceiver");
        urlParams                       = intent.getStringExtra("urlParams");
        type                            = intent.getStringExtra("type");

        url.append(intent.getStringExtra("url"));

        if(receiver != null)
            receiver.send(NetworkOperationStatus.STATUS_RUNNING.code, Bundle.EMPTY);

        try{
            if(receiver != null && type.equals("GET")){
                valuesToGet = intent.getStringArrayExtra("valuesToGet");
                if(valuesToGet.length > 0) {
                    String result[] = sendGetRequest();
                    bundle.putStringArray("result", result);
                    receiver.send(NetworkOperationStatus.STATUS_FINISHED.code, bundle);
                } else {
                    receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
                }
            } else if(type.equals("POST")){
                boolean result = sendPostRequest();
                if(receiver != null) {
                    bundle.putBoolean("result", result);
                    receiver.send(NetworkOperationStatus.STATUS_FINISHED.code, bundle);
                }
            } else if(receiver != null){
                receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
            }
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
        this.stopSelf();
    }

    private String[] sendGetRequest() throws IOException, JSONException, NetworkException {
        StringBuffer urlS   = new StringBuffer();
        String response[]   = new String[valuesToGet.length];

        urlS.append(this.url);
        urlS.append(this.urlParams);
        URL url = new URL(urlS.toString());

        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

        // Request Header
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        httpURLConnection.setRequestProperty("http.agent", "");

        int responseCode = httpURLConnection.getResponseCode();

        // Parse JSON
        if(responseCode == 200 || responseCode == 201){
            String json = getJSON(httpURLConnection.getInputStream());
            JSONObject obj = new JSONObject(json);

            for(int i=0; i<valuesToGet.length; ++i){
                response[i] = obj.getString(valuesToGet[i]);
            }

        } else {
            NetworkException e = new NetworkException("Could not get JSON");
            throw e;
        }

        return response;
    }

    private boolean sendPostRequest() throws IOException {
        URL url = new URL(this.url.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

        // Request Header
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("http.agent", "");

        //Send post request
        httpURLConnection.setDoOutput(true);
        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
        dos.writeBytes(urlParams);
        dos.flush();
        dos.close();

        int responseCode = httpURLConnection.getResponseCode();

        // Return true if request was done successfully
        return responseCode == 200 || responseCode == 201;
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
