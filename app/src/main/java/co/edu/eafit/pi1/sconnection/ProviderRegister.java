package co.edu.eafit.pi1.sconnection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.edu.eafit.pi1.sconnection.connection.services.HttpRequest;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;

public class ProviderRegister extends AppCompatActivity implements Receiver{

    EditText name;
    EditText description;
    EditText schedule;
    String   username;
    String   password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_register);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.provider_create_account_title);

        name        = (EditText) findViewById(R.id.complete_name_edit_text);
        description = (EditText) findViewById(R.id.description_edit_text);
        schedule    = (EditText) findViewById(R.id.schedule_edit_text);
        password    = getIntent().getExtras().getString("password");
        username    = getIntent().getExtras().getString("username");
    }

    public void doRegisterClick(View view){
        String name          = this.name.getText().toString();
        String description   = this.description.getText().toString();
        String schedule      = this.schedule.getText().toString();

        CSResultReceiver mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        String regex = "^([A-Z]([1|0][1-9]|[2][0-4])[-]([1|0][1-9]|[2][0-4]))$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(schedule);

        if(!matcher.matches()){
            AlertDialog alertDialog = createAlertDialog(R.string.alert_dialog_error_title,
                                                        R.string.alert_dialog_schedule_error);
            alertDialog.show();
            Intent intent = new Intent(this, ProviderRegister.class);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            startActivity(intent);
            this.finish();
        }

        if(!name.isEmpty() && !description.isEmpty() && !schedule.isEmpty() && !username.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, HttpRequest.class);
            intent.putExtra("url", "https://sc-b.herokuapp.com/api/v1/users/?");
            intent.putExtra("urlParams", "name=" + username + "&user_t=provider" +
                            "&password=" + password + "&description=" + description +
                            "&availability=" + schedule);
            intent.putExtra("type", "POST");
            intent.putExtra("mReceiver", mReceiver);
            startService(intent);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData){
        switch(resultCode){
            case 1: //STATUS_FINISHED
                if(resultData.getBoolean("result")){
                    AlertDialog alertDialog =
                            createAlertDialog(R.string.alert_dialog_register_successful_warning,
                                              R.string.alert_dialog_register_successful);
                    alertDialog.show();
                    break;  //If an error does not occur, then case 4 is not verified
                }
            case 4: //STATUS_GENERAL_ERROR
                AlertDialog alertDialog =
                        createAlertDialog(R.string.alert_dialog_register_successful_warning,
                                R.string.alert_dialog_register_fail);
                alertDialog.show();
        }
    }

    private AlertDialog createAlertDialog(int tittle, int message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        //Set the dialog characteristics
        alertBuilder.setMessage(message)
                    .setTitle(tittle);

        //Add the dialog buttons
        alertBuilder.setPositiveButton(R.string.accpt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        AlertDialog alertDialog = alertBuilder.create();

        return alertDialog;
    }

}
