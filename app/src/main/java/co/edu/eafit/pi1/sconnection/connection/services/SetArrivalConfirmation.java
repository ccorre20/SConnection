package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import co.edu.eafit.pi1.sconnection.connection.utils.NetworkOperationStatus;


/**
 * Created by tflr on 10/13/15.
 */
public class SetArrivalConfirmation extends IntentService{

    private StringBuffer    url;
    private StringBuffer    postParams;
    private String          uname;

    public SetArrivalConfirmation(){
        super(SetArrivalConfirmation.class.getName());
        url = new StringBuffer("https://sc-b.herokuapp.com/api/v1/services/");
    }

    @Override
    public void onHandleIntent(Intent intent){
        uname = intent.getStringExtra("uname");

        final ResultReceiver receiver = intent.getParcelableExtra("mReceiver");
        receiver.send(NetworkOperationStatus.STATUS_RUNNING.code, Bundle.EMPTY);

        String result;
        Bundle bundle = new Bundle();
        try{
            result = sendGet(uname);
            if(result.equals("ERROR")){
                receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
            }else{
                bundle.putString("providers", result);
                receiver.send(-1, bundle);
            }
        } catch (IOException e){
            receiver.send(NetworkOperationStatus.STATUS_GENERAL_ERROR.code, Bundle.EMPTY);
            e.printStackTrace();
        }

        this.stopSelf();
    }

    private String sendGet (String urlParams) throws IOException {
        StringBuffer urlS = new StringBuffer();
        urlS.append(this.url);
        urlS.append("?name="+urlParams);
        URL url = new URL(urlS.toString());
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        // Request Header
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("http.agent", "");

        int responseCode = con.getResponseCode();

        String json =  getJSON(con.getInputStream());

        // Return true if request was done successfully
        if(responseCode == 200 || responseCode == 201){
            return json;
        }else{
            return "ERROR";
        }
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
