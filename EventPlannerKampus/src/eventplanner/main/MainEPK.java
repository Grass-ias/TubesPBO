package eventplanner.main;

import eventplanner.gui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Kelas Utama (Main Class) sebagai Entry Point untuk menjalankan aplikasi
 * Sistem Manajemen Event Kampus.
 */
public class MainEPK {
    /**
     * Metode utama untuk memanggil dan menampilkan GUI utama.
     * 
     * @param args argumen baris perintah
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
