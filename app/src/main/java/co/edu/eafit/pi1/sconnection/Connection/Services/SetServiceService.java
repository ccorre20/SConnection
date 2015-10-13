package co.edu.eafit.pi1.sconnection.Connection.Services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ccr185 on 10/12/15.
 */
public class SetServiceService extends IntentService{


    public SetServiceService(String name) {
        super(SetServiceService.class.getName());

    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
