package co.edu.eafit.pi1.sconnection;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class Provider extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        ClientSender clientSender = new ClientSender("10.0.2.2",8880);
        clientSender.setMessage("Hola-mundo---");
        clientSender.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ClientSender extends AsyncTask<Void, Void, Void>{

        private String serverAddress;
        private int serverPort;
        private TextView text;
        private String message;
        private String response;

        public ClientSender(String serverAddress, int serverPort){
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.response = "";
        }

        public void setMessage(String message){this.message = message;}

        @Override
        public void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... v){
            Socket s = null;
            DataOutputStream dos = null;
            PrintWriter out = null;
            try{
                s = new Socket(serverAddress, serverPort);
                dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(message);
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                if(s != null){
                    try{s.close();}
                    catch (IOException ioe){ioe.printStackTrace(); response = ioe.toString();}
                }
                if (dos != null){
                    try{dos.close();}
                    catch (IOException ioe){ioe.printStackTrace(); response = ioe.toString();}
                }
            }
            return null;
        }

    }
}
