package co.edu.eafit.pi1.sconnection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.edu.eafit.pi1.sconnection.connection.services.HttpRequest;

public class ProviderRegister extends Activity {

    EditText name;
    EditText description;
    EditText schedule;
    String   username;
    String   password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_register);

        name        = (EditText) findViewById(R.id.complete_name_edit_text);
        description = (EditText) findViewById(R.id.description_edit_text);
        schedule    = (EditText) findViewById(R.id.schedule_edit_text);
        username    = getIntent().getExtras().getString("username");
        password    = getIntent().getExtras().getString("password");
    }

    public void doRegisterClick(View view){
        String name          = this.name.getText().toString();
        String description   = this.description.getText().toString();
        String schedule      = this.schedule.getText().toString();

        String regex = "^([A-Z]([1]\\d?|[2][0-4])[-]([1]\\d?|[2][0-4]))$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(schedule);

        if(!matcher.matches()){
            AlertDialog alertDialog = createAlertDialog();
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
            intent.putExtra("urlParams", "name=" + name + "&user_t=" + username +
                            "&password=" + password + "&description=" + description +
                            "&availability=" + schedule);
            intent.putExtra("type", "POST");
            startService(intent);
        }
    }

    private AlertDialog createAlertDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getBaseContext());

        //Set the dialog characteristics
        alertBuilder.setMessage(R.string.alert_dialog_schedule_error)
                    .setTitle(R.string.alert_dialog_error_title);

        //Add the dialog buttons
        alertBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        AlertDialog alertDialog = alertBuilder.create();

        return alertDialog;
    }

}
