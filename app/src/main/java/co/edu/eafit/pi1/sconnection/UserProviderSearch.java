package co.edu.eafit.pi1.sconnection;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.edu.eafit.pi1.sconnection.connection.services.GetProvidersService;
import co.edu.eafit.pi1.sconnection.connection.utils.CSResultReceiver;
import co.edu.eafit.pi1.sconnection.connection.utils.Receiver;

public class UserProviderSearch extends AppCompatActivity implements Receiver {

    final int CHOOSE_PROVIDER_REQUEST_CODE = 2;

    CSResultReceiver mReceiver;
    ProgressBar progressBar;
    ArrayAdapter<String> arrayAdapter;
    ListView lv;
    EditText editText;
    ArrayList<String> objs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_provider_search);
        mReceiver = new CSResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        lv = (ListView) findViewById(R.id.listView);
        lv.setBackgroundColor(getResources().getColor(R.color.white));
        editText = (EditText)findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                UserProviderSearch.this.arrayAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Elige un proveedor");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, GetProvidersService.class);
        intent.putExtra("mReceiver", mReceiver);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CHOOSE_PROVIDER_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String providerNameData = data.getStringExtra("providerName");
                Intent intent = new Intent();
                intent.putExtra("providerName", providerNameData);
                setResult(RESULT_OK, intent);
                this.finish();
            }
        }
    }

    public void onProviderClick(View view){
        String s = ((TextView)view).getText().toString();
        if (!s.equals("No hay servicios")){
            Intent i = null;
            for (String j: objs){
                if(j.contains(s)){
                    i = new Intent(this, UserProviderSearchDetail.class);
                    i.putExtra("json", j);
                    break;
                }
            }
            if (i !=  null){
                startActivityForResult(i, CHOOSE_PROVIDER_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode){
            case 0:{
                progressBar.setVisibility(View.VISIBLE);
                break;
            }
            case 1:{
                ArrayList<String> prov_names = new ArrayList<>();
                objs = resultData.getStringArrayList("providers");
                JSONObject o = null;
                for(String s:objs){
                    try {
                        o = new JSONObject(s);
                        prov_names.add(o.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(!prov_names.isEmpty()){
                    arrayAdapter = new ArrayAdapter<String>(
                            this,
                            R.layout.list_item2,
                            R.id.provider_detail,
                            prov_names);
                    lv.setAdapter(arrayAdapter);
                }
                progressBar.setVisibility(View.INVISIBLE);
                lv.setVisibility(View.VISIBLE);
                break;
            }
            case 2:{

                break;
            }
            case 3:{

                break;
            }
            case 4:{

                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_provider_search, menu);
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
