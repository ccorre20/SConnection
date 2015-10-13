package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by tflr on 10/13/15.
 */
public class SetArrivalConfirmation extends IntentService{

    private StringBuffer    url;
    private StringBuffer    postParams;
    private String          uname;

    public SetArrivalConfirmation(){
        super(SetArrivalConfirmation.class.getName());
        url = new StringBuffer("https://sc-b.herokuapp.com/api/v1/service_statuses/");
    }

    @Override
    public void onHandleIntent(Intent intent){
        uname = intent.getStringExtra("uname");

        if(!uname.isEmpty()){
            postParams = new StringBuffer("name=" + uname + "&status=true");
            try {
                sendPost(postParams.toString());
            } catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        this.stopSelf();
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
