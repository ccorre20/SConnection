package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class User extends AppCompatActivity {

    private Button b, b2;
    private ProgressBar progressBar;
    private TextView progressTxt;
    private boolean listen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        b = (Button) findViewById(R.id.conn_Button);
        b2 = (Button) findViewById(R.id.button_cancel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressTxt = (TextView) findViewById(R.id.Progress_txt);
        listen = false;
    }

    public void connClickListener(View view){
        this.b.setEnabled(false);
        try{
            ConnServer();
        }catch (Exception e){
            progressTxt.setText(R.string.Error);
            progressBar.setProgress(0);
            b.setEnabled(true);
        }
    }

    public void cancelClickListener(View view){
        listen = false;
        this.b2.setEnabled(false);
        this.b2.setVisibility(View.INVISIBLE);
        progressTxt.setText(R.string.Error);
        progressBar.setProgress(0);
        b.setEnabled(true);
    }

    private boolean isCanceled(){
        return !this.listen;
    }

    private void ConnServer() throws Exception {
        this.b2.setVisibility(View.VISIBLE);
        this.b2.setEnabled(true);
        this.progressBar.setVisibility(View.VISIBLE);
        this.progressTxt.setText(R.string.creating_server);
        this.progressTxt.setVisibility(View.VISIBLE);
        ServerSocket ss;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        Socket s = null;

        try {
            ss = new ServerSocket(8888);
            this.progressBar.setProgress(20);
            this.progressTxt.setText(R.string.waiting_for_connection);
            listen = true;
        } catch (Exception e) {
            throw e;
        }

        try {
            s = ss.accept();
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
        } catch (Exception e) {
            //throw e;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (Exception e) {
                    //throw e;
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (Exception e) {
                    //throw e;
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (Exception e) {
                    //throw e;
                }
            }
        }
        this.progressTxt.setText("out!");
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
