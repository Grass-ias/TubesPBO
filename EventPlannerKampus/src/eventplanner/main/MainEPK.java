package eventplanner.main;

import eventplanner.model.*;
import eventplanner.division.*;
import eventplanner.exception.*;
import java.util.List;

/**
 * Kelas Utama (Main Class) untuk memverifikasi dan menjalankan fungsionalitas
 * dari seluruh arsitektur sistem Campus Event Planner.
 */
public class MainEPK {
    /**
     * Metode utama untuk mengeksekusi skenario uji coba sistem.
     * 
     * @param args argumen baris perintah (tidak digunakan)
     */
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Verifikasi Sistem Campus Event Planner (EPK)   ");
        System.out.println("==================================================\n");

        // 1. Inisialisasi Acara dan Repositori Tugas
        System.out.println("--- 1. Pengujian Event dan TaskRepository ---");
        Event event = new Event("E01", "Dies Natalis Campus Festival", 10000000.0);
        System.out.println("Acara Berhasil Dibuat: " + event);

        TaskRepository<Task> taskRepo = new TaskRepository<>();

        Task task1 = new Task("T01", "Penyusunan Rundown Acara", 3, 0.0); // Tugas gratis (Acara)
        Task task2 = new Task("T02", "Sewa Sound System & Stage", 2, 3500000.0); // Tugas logistik
        Task task3 = new Task("T03", "Pemesanan Catering Konsumsi", 4, 2000000.0); // Tugas konsumsi
        Task task4 = new Task("T04", "Dekorasi Panggung Utama", 1, 5000000.0); // Tugas logistik (Biaya besar)

        taskRepo.addTask(task1);
        taskRepo.addTask(task2);
        taskRepo.addTask(task3);
        taskRepo.addTask(task4);

        System.out.println("Tugas-tugas berhasil ditambahkan ke TaskRepository.");
        List<Task> seluruhTugas = taskRepo.getAllTasks();
        System.out.println("Seluruh tugas yang diambil dari repositori:");
        for (Task t : seluruhTugas) {
            System.out.println(" - " + t);
        }
        System.out.println();

        // 2. Inisialisasi Divisi
        System.out.println("--- 2. Pengujian Divisi & Reportable (buatLaporan) ---");
        Division acaraDiv = new AcaraDivision(1000000.0);
        Division logisticDiv = new LogisticDivision(4000000.0); // Anggaran terbatas
        Division konsumsiDiv = new KonsumsiDivision(5000000.0); // Anggaran memadai

        System.out.println(acaraDiv.buatLaporan());
        System.out.println(logisticDiv.buatLaporan());
        System.out.println(konsumsiDiv.buatLaporan());
        System.out.println();

        // 3. Inisialisasi Anggota Panitia (Committee)
        System.out.println("--- 3. Pengujian Beban Kerja Panitia (Committee) ---");
        Committee comm1 = new Committee("C01", "Budi (Staf Acara)", 5); // Kapasitas maksimum 5
        Committee comm2 = new Committee("C02", "Siti (Staf Logistik)", 3); // Kapasitas maksimum 3
        Committee comm3 = new Committee("C03", "Andi (Staf Konsumsi)", 2); // Kapasitas kecil 2
        System.out.println(comm1);
        System.out.println(comm2);
        System.out.println(comm3);
        System.out.println();

        // 4. Pengujian Eksekusi Tugas dan Polimorfisme
        System.out.println("--- 4. Eksekusi Tugas (Polimorfisme & Validasi Pengecualian) ---");

        // Skenario A: Divisi Acara mengeksekusi task1 (kesulitan 3). Panitia comm1
        // berkapasitas 5. Harus sukses.
        try {
            System.out.println("Mengeksekusi Tugas: " + task1.getTaskName() + " (Kesulitan: " + task1.getDifficulty()
                    + ") via " + acaraDiv.getDivisionName());
            acaraDiv.eksekusiTugas(task1, comm1);
            System.out.println("Sukses! Status Panitia terbaru: " + comm1);
        } catch (OverloadException e) {
            System.out.println("Gagal karena kelebihan beban kerja! Kesalahan: " + e.getMessage());
        } catch (OverBudgetException e) {
            System.out.println("Gagal karena kelebihan anggaran! Kesalahan: " + e.getMessage());
        }
        System.out.println();

        // Skenario B: Divisi Logistik mengeksekusi task2 (biaya 3,500,000). Anggaran
        // 4,000,000. Harus sukses.
        try {
            System.out.println("Mengeksekusi Tugas: " + task2.getTaskName() + " (Biaya: " + task2.getTaskCost()
                    + ") via " + logisticDiv.getDivisionName());
            logisticDiv.eksekusiTugas(task2, comm2);
            System.out.println("Sukses!");
            System.out.println("Status Divisi terbaru: " + logisticDiv.buatLaporan());
            System.out.println("Status Panitia terbaru: " + comm2);
        } catch (OverloadException e) {
            System.out.println("Gagal karena kelebihan beban kerja! Kesalahan: " + e.getMessage());
        } catch (OverBudgetException e) {
            System.out.println("Gagal karena kelebihan anggaran! Kesalahan: " + e.getMessage());
        }
        System.out.println();

        // Skenario C: Kesalahan Anggaran Lebih (OverBudgetException) pada Divisi
        // Logistik.
        // Mencoba mengeksekusi task4 (biaya 5,000,000) pada sisa anggaran (500,000).
        // Harus memicu OverBudgetException.
        try {
            System.out.println("Mengeksekusi Tugas: " + task4.getTaskName() + " (Biaya: " + task4.getTaskCost()
                    + ") via " + logisticDiv.getDivisionName());
            logisticDiv.eksekusiTugas(task4, comm2);
            System.out.println("Sukses!");
        } catch (OverloadException e) {
            System.out.println("Gagal karena kelebihan beban kerja! Kesalahan: " + e.getMessage());
        } catch (OverBudgetException e) {
            System.out.println("Gagal (Diharapkan)! Jenis Kesalahan: OverBudgetException | Pesan: " + e.getMessage());
        }
        System.out.println();

        // Skenario D: Kesalahan Kapasitas Kerja Berlebih (OverloadException) pada
        // Divisi Konsumsi.
        // Mencoba mengeksekusi task3 (kesulitan 4) dengan panitia comm3 (kapasitas 2).
        // Harus memicu OverloadException.
        try {
            System.out.println("Mengeksekusi Tugas: " + task3.getTaskName() + " (Kesulitan: " + task3.getDifficulty()
                    + ") via " + konsumsiDiv.getDivisionName());
            konsumsiDiv.eksekusiTugas(task3, comm3);
            System.out.println("Sukses!");
        } catch (OverloadException e) {
            System.out.println("Gagal (Diharapkan)! Jenis Kesalahan: OverloadException | Pesan: " + e.getMessage());
        } catch (OverBudgetException e) {
            System.out.println("Gagal karena kelebihan anggaran! Kesalahan: " + e.getMessage());
        }
        System.out.println();

        // Skenario E: Divisi Acara mencoba mengeksekusi tugas baru yang melebihi sisa
        // kapasitas panitia comm1.
        // Beban kerja comm1 saat ini adalah 3. Maksimum adalah 5. Mencoba menambahkan
        // taskE dengan kesulitan 3. Harus memicu OverloadException.
        Task taskE = new Task("T05", "Koordinasi Pemateri Luar", 3, 0.0);
        try {
            System.out.println("Mengeksekusi Tugas: " + taskE.getTaskName() + " (Kesulitan: " + taskE.getDifficulty()
                    + ") via " + acaraDiv.getDivisionName());
            acaraDiv.eksekusiTugas(taskE, comm1);
            System.out.println("Sukses!");
        } catch (OverloadException e) {
            System.out.println("Gagal (Diharapkan)! Jenis Kesalahan: OverloadException | Pesan: " + e.getMessage());
        } catch (OverBudgetException e) {
            System.out.println("Gagal karena kelebihan anggaran! Kesalahan: " + e.getMessage());
        }
        System.out.println();

        // 5. Laporan Akhir Divisi
        System.out.println("--- 5. Laporan Akhir Seluruh Divisi ---");
        System.out.println(acaraDiv.buatLaporan());
        System.out.println(logisticDiv.buatLaporan());
        System.out.println(konsumsiDiv.buatLaporan());
        System.out.println("==================================================");
    }
}
