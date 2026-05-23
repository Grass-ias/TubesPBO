package eventplanner.division;

/**
 * Antarmuka (Interface) yang mendefinisikan kontrak untuk komponen
 * yang memiliki kemampuan menghasilkan laporan status.
 */
public interface Reportable {
    /**
     * Menghasilkan laporan status dalam bentuk teks (String).
     * 
     * @return laporan status komponen
     */
    String buatLaporan();
}
