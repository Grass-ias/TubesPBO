package eventplanner.exception;

/**
 * Pengecualian kustom yang dilemparkan ketika beban kerja panitia
 * melebihi kapasitas maksimum yang telah ditentukan.
 */
public class OverloadException extends Exception {
    /**
     * Konstruktor default untuk OverloadException.
     */
    public OverloadException() {
        super("Kapasitas beban kerja panitia telah melebihi batas maksimal!");
    }

    /**
     * Konstruktor dengan pesan kustom untuk OverloadException.
     * 
     * @param message pesan detail kesalahan
     */
    public OverloadException(String message) {
        super(message);
    }
}
