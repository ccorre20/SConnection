package co.edu.eafit.pi1.sconnection.exceptions;

/**
 * Created by ccr185 on 10/10/15.
 */
public class NetworkException extends Exception{

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

}
