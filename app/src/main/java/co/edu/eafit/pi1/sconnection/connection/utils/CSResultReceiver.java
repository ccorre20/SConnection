package co.edu.eafit.pi1.sconnection.connection.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by ccr185 on 10/10/15.
 */
public class CSResultReceiver extends ResultReceiver{
    private Receiver mReceiver;

    public CSResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
