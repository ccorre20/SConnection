package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import co.edu.eafit.pi1.sconnection.connection.services.RegisterConnectionService;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;

public class Register extends AppCompatActivity implements Receiver {

    CSResultReceiver mReceiver;
    EditText username_text;
    RadioButton r1, r2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username_text = (EditText) findViewById(R.id.username_text);
        r1 = (RadioButton) findViewById(R.id.radioButton);
        r2 = (RadioButton) findViewById(R.id.radioButton2);
    }

    @Override
    protected void onResume(){
        super.onResume();
        username_text.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                username_text.getText().clear();
            }
        });
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        switch (resultCode){
            case 0:{
                break;
            }
            case 1:{
                this.finish();
                break;
            }
            case 2:{

                break;
            }
            case 3:{

                break;
            }
        }

    }

    public void doRegisterClick(View view){
        mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, RegisterConnectionService.class);
        intent.putExtra("username", username_text.getText().toString());
        intent.putExtra("mReceiver", mReceiver);
        String type = "user";
        if(r1.isChecked()){
            type = "user";
        } else if(r2.isChecked()) {
            type = "provider";
        }
        intent.putExtra("type", type);

        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
