package co.edu.eafit.pi1.sconnection.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import co.edu.eafit.pi1.sconnection.Connection.Services.SetArrivalConfirmation;
import co.edu.eafit.pi1.sconnection.R;

public class ConfirmArrival extends Activity {

    private EditText uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_arrival);

        uname = (EditText)findViewById(R.id.confirm_user_edittext);
    }

    public void acceptOnClick(View view){
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SetArrivalConfirmation.class);
        intent.putExtra("uname", uname.getText().toString());
        startService(intent);
        this.finish();
    }

    public void cancelOnClick(View view){
        this.finish();
    }

}
