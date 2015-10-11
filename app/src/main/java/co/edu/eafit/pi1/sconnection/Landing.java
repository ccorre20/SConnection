package co.edu.eafit.pi1.sconnection;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import co.edu.eafit.pi1.sconnection.Connection.Services.LoginConnectionService;
import co.edu.eafit.pi1.sconnection.Connection.Utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.Connection.Utils.Receiver;

public class Landing extends AppCompatActivity implements Receiver {

    EditText uname;
    Button login;
    CSResultReceiver mReceiver;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        uname = (EditText)findViewById(R.id.editText_username);
        login  = (Button) findViewById(R.id.button_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
    }

    @Override
    protected void onResume(){
        super.onResume();
        uname.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                uname.getText().clear();
            }
        });
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Intent i = null;

        switch (resultCode){
            case LoginConnectionService.STATUS_RUNNING:{
                progressBar.setVisibility(View.VISIBLE);
                break;
            }
            case LoginConnectionService.STATUS_FINISHED:{
                String res = resultData.getString("user_t");
                if(res.equals("user")){
                    i = new Intent(this, User.class);
                    i.putExtra("username", uname.getText().toString());
                } else {
                    i = new Intent(this, Provider.class);
                    i.putExtra("username", uname.getText().toString());
                }
                progressBar.setVisibility(View.INVISIBLE);
                break;
            }
            case LoginConnectionService.STATUS_GENERAL_ERROR:{

                break;
            }
            case LoginConnectionService.STATUS_NAME_ERROR:{

                break;
            }
            case LoginConnectionService.STATUS_NETWORK_ERROR:{

                break;
            }
        }

        if(i != null){
            startActivity(i);
        }
    }

    public void userClick(View view){
        login.setEnabled(false);
        mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, LoginConnectionService.class);
        Log.d("NAME", uname.getText().toString());
        intent.putExtra("username", uname.getText().toString());
        intent.putExtra("mReceiver", mReceiver);

        startService(intent);
    }

    public void registerClick(View view){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    public void nameClick(View view){
        uname.getText().clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing, menu);
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
