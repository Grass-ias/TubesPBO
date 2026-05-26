package eventplanner.division;

import eventplanner.model.Task;
import eventplanner.model.Committee;
import eventplanner.exception.OverloadException;
import eventplanner.exception.OverBudgetException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Subkelas yang merepresentasikan Divisi Keamanan.
 * Divisi ini berfokus pada beban kerja pengamanan panitia (Workload-focused).
 */
public class KeamananDivision extends Division {

    /**
     * Konstruktor untuk menginisialisasi Divisi Keamanan dengan anggaran tertentu.
     * 
     * @param allocatedBudget alokasi anggaran untuk divisi keamanan
     */
    public KeamananDivision(double allocatedBudget) {
        super("Divisi Keamanan", allocatedBudget);
    }

    /**
     * Mengeksekusi tugas keamanan dengan melakukan validasi kapasitas beban kerja panitia Keamanan.
     * Jika total beban kerja melebihi kapasitas maksimal, akan dilemparkan OverloadException.
     * 
     * @param task tugas keamanan yang akan dieksekusi
     * @param committee anggota panitia yang ditugaskan untuk tugas tersebut
     * @throws OverloadException jika kapasitas beban kerja panitia Keamanan sudah mencapai batas maksimal
     * @throws OverBudgetException dideklarasikan untuk memenuhi tanda tangan metode (tidak dilemparkan)
     */
    @Override
    public void eksekusiTugas(Task task, Committee committee) throws OverloadException, OverBudgetException {
        if (committee.getCurrentWorkload() + task.getDifficulty() > committee.getMaxCapacity()) {
            throw new OverloadException("Panitia Keamanan sudah mencapai batas maksimal jam jaga/shift.");
        }
        committee.tambahBebanKerja(task);
    }

    @Override
    public String buatLaporan() {
        return super.buatLaporan();
    }
}
