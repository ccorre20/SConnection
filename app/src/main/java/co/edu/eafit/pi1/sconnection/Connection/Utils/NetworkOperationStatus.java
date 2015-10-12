package co.edu.eafit.pi1.sconnection.Connection.Utils;

/**
 * Created by ccr185 on 10/11/15.
 */
public enum NetworkOperationStatus {
    STATUS_RUNNING(0),
    STATUS_FINISHED(1),
    STATUS_NETWORK_ERROR(2),
    STATUS_NAME_ERROR(3),
    STATUS_GENERAL_ERROR(4);

    public int code;

    private NetworkOperationStatus(int val){
        this.code = val;
    }
}
