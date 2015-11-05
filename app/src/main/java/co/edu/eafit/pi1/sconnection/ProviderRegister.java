package co.edu.eafit.pi1.sconnection;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProviderRegister extends Activity {

    EditText name;
    EditText description;
    EditText schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_register);

        name        = (EditText) findViewById(R.id.complete_name_edit_text);
        description = (EditText) findViewById(R.id.description_edit_text);
        schedule    = (EditText) findViewById(R.id.schedule_edit_text);

    }

    public void doRegisterClick(View view){
        String regex = "^([A-Z]([1]\\d?|[2][0-4])[-]([1]\\d?|[2][0-4]))$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(schedule.getText());

        if(matcher.matches()){}
    }

}
