package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import co.edu.eafit.pi1.sconnection.connection.services.HttpRequest;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;

public class ProviderProfile extends AppCompatActivity implements Receiver{

    String username;
    Bundle extra;
    TextView userProvider;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_profile);

        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        CSResultReceiver mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        extra = getIntent().getExtras();
        if (extra != null)
            username = extra.getString("username");

        userProvider = (TextView)findViewById(R.id.unameProvider);
        userProvider.setText(username);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, HttpRequest.class);
        intent.putExtra("url", "https://sc-b.herokuapp.com/api/v1/users/?");
        intent.putExtra("urlParams", "name=" + username + "&ranking=true");
        intent.putExtra("type", "GET");
        intent.putExtra("valuesToGet", new String[]{"rating"});
        intent.putExtra("mReceiver", mReceiver);
        startService(intent);

        addListenerOnRatingBar();
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_provider_profile, menu);
        return true;
    }

    public void onReceiveResult(int resultCode, Bundle resultData){
        switch(resultCode){
            case 1: //STATUS_FINISHED
                String[] result = resultData.getStringArray("result");
                ratingBar.setRating(Float.parseFloat(result[0]));
            case 4: //STATUS_GENERAL_ERROR

        }
    }

    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                Toast.makeText(getApplicationContext(), String.valueOf(rating), Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}