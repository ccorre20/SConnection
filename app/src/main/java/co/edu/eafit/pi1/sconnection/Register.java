package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
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
    EditText passwd;
    RadioButton userRButton, providerRButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username_text = (EditText) findViewById(R.id.username_text);
        passwd = (EditText) findViewById(R.id.password_edittext);
        userRButton = (RadioButton) findViewById(R.id.radioButton);
        providerRButton = (RadioButton) findViewById(R.id.radioButton2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.register_title);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        intent.putExtra("password", passwd.getText().toString());
        intent.putExtra("mReceiver", mReceiver);
        if(userRButton.isChecked()){
            intent.putExtra("type", "user");
            startService(intent);
        } else if(providerRButton.isChecked()) {
            Intent providerRegisterIntent = new Intent(this, ProviderRegister.class);
            providerRegisterIntent.putExtra("username", username_text.getText().toString());
            providerRegisterIntent.putExtra("password", passwd.getText().toString());
            startActivity(providerRegisterIntent);
        }
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
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
