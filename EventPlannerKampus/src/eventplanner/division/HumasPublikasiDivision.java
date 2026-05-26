package eventplanner.division;

import eventplanner.model.Task;
import eventplanner.model.Committee;
import eventplanner.exception.OverloadException;
import eventplanner.exception.OverBudgetException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Subkelas yang merepresentasikan Divisi Humas & Publikasi.
 * Divisi ini berfokus pada manajemen kapasitas tenaga panitia (Workload-focused).
 */
public class HumasPublikasiDivision extends Division {

    /**
     * Konstruktor untuk menginisialisasi Divisi Humas & Publikasi dengan anggaran tertentu.
     * 
     * @param allocatedBudget alokasi anggaran untuk divisi Humas & Publikasi
     */
    public HumasPublikasiDivision(double allocatedBudget) {
        super("Divisi Humas & Publikasi", allocatedBudget);
    }

    /**
     * Mengeksekusi tugas publikasi dengan melakukan validasi kapasitas beban kerja panitia Humas.
     * Jika total beban kerja melebihi kapasitas maksimal, akan dilemparkan OverloadException.
     * 
     * @param task tugas publikasi yang akan dieksekusi
     * @param committee anggota panitia yang ditugaskan untuk tugas tersebut
     * @throws OverloadException jika kapasitas beban kerja panitia Humas tidak mencukupi
     * @throws OverBudgetException dideklarasikan untuk memenuhi tanda tangan metode (tidak dilemparkan)
     */
    @Override
    public void eksekusiTugas(Task task, Committee committee) throws OverloadException, OverBudgetException {
        if (committee.getCurrentWorkload() + task.getDifficulty() > committee.getMaxCapacity()) {
            throw new OverloadException("Kapasitas panitia Humas tidak mencukupi untuk tugas publikasi ini.");
        }
        committee.tambahBebanKerja(task);
    }

    @Override
    public String buatLaporan() {
        return super.buatLaporan();
    }
}
