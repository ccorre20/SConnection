package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import co.edu.eafit.pi1.sconnection.connection.services.GetServiceListService;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;

public class UserServiceList extends AppCompatActivity implements Receiver {

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
    boolean isProvider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_service_list);
        username = getIntent().getStringExtra("username");
        progressBar = (ProgressBar) findViewById(R.id.user_service_list_bar);
        listView = (ListView) findViewById(R.id.user_service_list_view);
        r1 = (RadioButton) findViewById(R.id.user_service_list_radio_sent);
        r2 = (RadioButton) findViewById(R.id.user_service_list_radio_completed);
        r3 = (RadioButton) findViewById(R.id.user_service_list_radio_all);
        mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        rg = (RadioGroup) findViewById(R.id.user_service_list_group);
        MOnCheckedChangeListener mOnCheckedChangeListener = new MOnCheckedChangeListener(this);
        rg.setOnCheckedChangeListener(mOnCheckedChangeListener);
        only = "sent";

        if(getIntent().getBooleanExtra("provider", false)){
            isProvider = true;
        }
    }

    public void refresh(){
        if(r1.isChecked()){
            only = "sent";
        } else if (r2.isChecked()){
            only = "done";
        } else {
            only = "all";
        }
        go();
    }

    private void go(){
        Intent i = new Intent(Intent.ACTION_SYNC, null, this, GetServiceListService.class);
        i.putExtra("username", username);
        i.putExtra("mReceiver", mReceiver);
        i.putExtra("only",only);
        startService(i);
    }

    public void onItemClick(View view){
        String s = ((TextView)view).getText().toString();
        if (!s.equals("No hay servicios")){
            Intent i = null;
            for (String j: objs){
                if(j.contains(s)){
                    i = new Intent(this, UserServiceDetail.class);
                    i.putExtra("json", j);
                    i.putExtra("isProvider", isProvider);
                    break;
                }
            }
            if (i !=  null){
                startActivity(i);
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        go();
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case 0: {
                listView.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                break;
            }
            case 1: {
                ArrayList<String> prov_names = new ArrayList<>();
                objs = resultData.getStringArrayList("services");
                JSONObject o = null;
                for (String s : objs) {
                    try {
                        o = new JSONObject(s);
                        prov_names.add(o.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!prov_names.isEmpty()) {
                    arrayAdapter = new ArrayAdapter<String>(
                            this,
                            R.layout.list_item,
                            R.id.Desc,
                            prov_names);
                    listView.setAdapter(arrayAdapter);
                }else{
                    prov_names.add("No hay servicios");
                    arrayAdapter = new ArrayAdapter<String>(
                            this,
                            R.layout.list_item,
                            R.id.Desc,
                            prov_names);
                    listView.setAdapter(arrayAdapter);
                }
                progressBar.setVisibility(View.INVISIBLE);
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

    private class MOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        UserServiceList p;

        public MOnCheckedChangeListener(UserServiceList p){
            this.p = p;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            p.refresh();
        }
    }

}
