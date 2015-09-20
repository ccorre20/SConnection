package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class User extends AppCompatActivity {

    private Button b, b2;
    private ProgressBar progressBar;
    private TextView progressTxt;
    private getDataTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        b = (Button) findViewById(R.id.conn_Button);
        b2 = (Button) findViewById(R.id.button_cancel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressTxt = (TextView) findViewById(R.id.Progress_txt);
        task = null;
    }

    public void connClickListener(View view){
        task = new getDataTask(progressBar, progressTxt, b, b2);
        task.execute();
    }

    public void cancelClickListener(View view){
        if(task != null) {
            task.cancel(true);
            task = null;
        }
    }

    private class getDataTask extends AsyncTask<Void, Integer, Void> {

        private ProgressBar progressBar;
        private TextView progressTxt;
        private Button b, b2;
        private int status;
        private String result, publish;

        public getDataTask(ProgressBar progressBar, TextView progressTxt, Button b, Button b2){
            this.progressBar = progressBar;
            this.progressTxt = progressTxt;
            this.b = b;
            this.b2 = b2;
            status = 0;
        }

        protected void onPreExecute() {
            b.setEnabled(false);
            b2.setVisibility(View.VISIBLE);
            b2.setEnabled(true);
            progressBar.setVisibility(View.VISIBLE);
            progressTxt.setVisibility(View.VISIBLE);
            progressBar.setEnabled(true);
            progressTxt.setEnabled(true);
        }

        protected Void doInBackground(Void... v) {
            publishProgress(0);
            ServerSocket ss = null;
            DataInputStream dis = null;
            DataOutputStream dos = null;
            Socket s = null;
            try {
                ss = new ServerSocket(8888);
                publishProgress(25);
            } catch (Exception e) {
                Log.v("test", e.getMessage());
            }
            try {
                ss.setSoTimeout(5000);
                s = ss.accept();
                dis = new DataInputStream(s.getInputStream());
                publishProgress(50);
                result = "";
                publish = "";
                while((result = dis.readUTF()) != null){
                    if(isCancelled()){
                        result = "";
                        publish = "";
                        return null;
                    }
                    publish += result;
                    if(result.contains("---")){
                        break;
                    }
                }
                publishProgress(75);
            } catch (SocketTimeoutException e) {
                Log.v("test", "timeout");
                this.cancel(true);
            }catch (Exception e){
                Log.v("test", e.getMessage());
                this.cancel(true);
            } finally {
                if(s != null){
                    try{
                        s.close();
                    } catch (Exception e){
                        //Log.v("test", e.getMessage());
                    }
                }
                if(dis != null){
                    try{
                        dis.close();
                    } catch(Exception e){
                        //Log.v("test", e.getMessage());
                    }
                }
                if(ss != null) {
                    try {
                        ss.close();
                    } catch (Exception e) {
                        //Log.v("test", e.getMessage());
                    }
                }
            }
            return null;
        }

        protected void onCancelled() {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setEnabled(false);
            progressTxt.setText("Cancelled");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.v("test", e.getMessage());
            }
            progressTxt.setEnabled(false);
            progressTxt.setVisibility(View.INVISIBLE);
            b.setEnabled(true);
            b2.setEnabled(false);
            b2.setVisibility(View.INVISIBLE);
        }

        protected void onProgressUpdate(Integer... progress){
            progressBar.setProgress(progress[0]);
            switch (status){
                case 0:
                {
                    progressTxt.setText(R.string.creating_server);
                    status++;
                    break;
                }
                case 1:
                {
                    progressTxt.setText(R.string.waiting_for_connection);
                    status++;
                    break;
                }
                case 2:
                {
                    progressTxt.setText(R.string.recieveing_data);
                    status++;
                    break;
                }
                case 3:
                {
                    progressTxt.setText(R.string.connection_complete);
                    status++;
                    break;
                }
            }
        }


        protected void onPostExecute(Void v){
            progressBar.setProgress(100);
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setEnabled(false);
            progressTxt.setText("Done");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.v("test", e.getMessage());
            }
            progressTxt.setEnabled(false);
            progressTxt.setVisibility(View.INVISIBLE);
            b.setEnabled(true);
            b2.setEnabled(false);
            b2.setVisibility(View.INVISIBLE);

            Log.v("test", publish);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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
}
