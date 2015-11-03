package co.edu.eafit.pi1.sconnection.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import co.edu.eafit.pi1.sconnection.R;
import co.edu.eafit.pi1.sconnection.connection.services.SetArrivalConfirmation;

public class ConfirmArrival extends AppCompatActivity {

    private EditText uname;
    ResultReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_arrival);
        Intent intent = getIntent();
        receiver = intent.getParcelableExtra("mReceiver");
        uname = (EditText)findViewById(R.id.confirm_user_edittext);
    }

    public void acceptOnClick(View view){
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SetArrivalConfirmation.class);
        intent.putExtra("uname", uname.getText().toString());
        intent.putExtra("mReceiver", receiver);
        startService(intent);
        this.finish();
    }

    public void cancelOnClick(View view){
        this.finish();
    }

}
