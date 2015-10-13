package co.edu.eafit.pi1.sconnection.connection.services;

import android.app.IntentService;
import android.content.Intent;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ccr185 on 10/12/15.
 */
public class SetServiceService extends IntentService{

    private StringBuffer url;

    public SetServiceService() {
        super(SetServiceService.class.getName());
        url = new StringBuffer();
        url.append("https://sc-b.herokuapp.com/api/v1/services/");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String uname        = intent.getStringExtra("uname");
        String provider     = intent.getStringExtra("provider");
        String latitude     = intent.getStringExtra("latitude");
        String longitude    = intent.getStringExtra("longitude");
        String message      = intent.getStringExtra("message");

        try {
            sendPost("name=" + uname + "&provider=" + provider +
                    "&latitude=" + latitude + "&longitude=" + longitude + "&message=" + message);
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        this.stopSelf();
    }

    private boolean sendPost (String urlParams) throws IOException {
        URL url = new URL(this.url.toString());
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        // Request Header
        con.setRequestMethod("POST");
        con.setRequestProperty("http.agent", "");

        // Send post request
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
