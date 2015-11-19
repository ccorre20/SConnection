package co.edu.eafit.pi1.sconnection.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.eafit.pi1.sconnection.R;
import co.edu.eafit.pi1.sconnection.ServiceConfirm;
import co.edu.eafit.pi1.sconnection.UserServiceDetail;
import co.edu.eafit.pi1.sconnection.connection.services.GetServiceListService;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;

public class ConfirmArrival extends AppCompatActivity implements Receiver{

    String username;
    ProgressBar progressBar;
    ListView listView;
    RadioButton r1, r2, r3;
    ArrayAdapter<String> arrayAdapter;
    CSResultReceiver mReceiver;
    RadioGroup rg;
    String only;
    ArrayList<String> objs;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_arrival);
        username = getIntent().getStringExtra("username");
        listView = (ListView) findViewById(R.id.listView2);
        mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        only = "sent";
    }

    private void go() {
        Intent i = new Intent(Intent.ACTION_SYNC, null, this, GetServiceListService.class);
        i.putExtra("username", username);
        i.putExtra("mReceiver", mReceiver);
        i.putExtra("only", only);
        startService(i);
    }

    public void onItemClick(View view) {
        String s = ((TextView) view).getText().toString().split("\n")[1];
        if (!s.equals("No hay servicios")) {
            Intent i = null;
            for (String j : objs) {
                if (j.contains(s)) {
                    i = new Intent(this, ServiceConfirm.class);
                    i.putExtra("json", j);
                    i.putExtra("username", username);
                    break;
                }
            }
            if (i != null) {
                startActivity(i);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        go();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case 0: {
                listView.setEnabled(false);
                break;
            }
            case 1: {
                ArrayList<String> prov_names = new ArrayList<>();
                objs = resultData.getStringArrayList("services");
                JSONObject o = null;
                for (String s : objs) {
                    try {
                        o = new JSONObject(s);
                        prov_names.add(o.getString("message")
                                + "\n"
                                + o.getJSONObject("provider").getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!prov_names.isEmpty()) {
                    arrayAdapter = new ArrayAdapter<String>(
                            this,
                            R.layout.list_item3,
                            R.id.textView11,
                            prov_names);
                    listView.setAdapter(arrayAdapter);
                } else {
                    prov_names.add("No hay servicios");
                    arrayAdapter = new ArrayAdapter<String>(
                            this,
                            R.layout.list_item3,
                            R.id.textView11,
                            prov_names);
                    listView.setAdapter(arrayAdapter);
                }
                listView.setVisibility(View.VISIBLE);
                listView.setEnabled(true);
                break;
            }
            case 2: {

                break;
            }
            case 3: {

                break;
            }
            case 4: {

                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_service_list, menu);
        return true;
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
