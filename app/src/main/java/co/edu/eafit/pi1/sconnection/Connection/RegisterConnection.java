package co.edu.eafit.pi1.sconnection.Connection;

import android.app.AlertDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tflr on 10/10/15.
 */
public class RegisterConnection {

    private StringBuffer url;

    public RegisterConnection(){
        url = new StringBuffer();
        url.append("https://sc-b.herokuapp.com/api/v1/users/");
    }

    public String sendGet (String urlParams) throws Exception{
        StringBuffer urlS = new StringBuffer();
        urlS.append(this.url);
        urlS.append(urlParams);
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
            Exception e = new Exception("Could not get JSON");
            throw e;
        }

        return response;
    }

    public boolean sendPost (String urlParams) throws Exception{
        URL url = new URL(this.url.toString());
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        // Request Header
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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

    private String getJSON(InputStream inputStream) throws Exception{
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
