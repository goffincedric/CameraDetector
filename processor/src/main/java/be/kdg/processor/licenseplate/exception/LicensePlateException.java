package be.kdg.processor.licenseplate.exception;

/**
 * @author Cédric Goffin
 * 23/10/2018 13:17
 */
public class LicensePlateException extends Throwable {
    public LicensePlateException(String message) {
        super(message);
    }

    public LicensePlateException(String message, Throwable cause) {
        super(message, cause);
    }
}
