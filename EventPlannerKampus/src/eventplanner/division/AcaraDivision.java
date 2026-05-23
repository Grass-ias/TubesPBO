package eventplanner.division;

import eventplanner.model.Task;
import eventplanner.model.Committee;
import eventplanner.exception.OverloadException;
import eventplanner.exception.OverBudgetException;

/**
 * Subkelas yang merepresentasikan Divisi Acara.
 * Divisi ini berfokus pada manajemen kapasitas dan beban kerja panitia.
 */
public class AcaraDivision extends Division {

    /**
     * Konstruktor untuk menginisialisasi Divisi Acara dengan anggaran tertentu.
     * 
     * @param allocatedBudget alokasi anggaran untuk divisi acara
     */
    public AcaraDivision(double allocatedBudget) {
        super("Divisi Acara", allocatedBudget);
    }

    /**
     * Mengeksekusi tugas dengan melakukan validasi kapasitas beban kerja panitia.
     * Divisi Acara tidak memotong anggaran divisi untuk pelaksanaan tugas.
     * 
     * @param task tugas yang akan dieksekusi
     * @param committee anggota panitia yang ditugaskan
     * @throws OverloadException jika kapasitas beban kerja panitia tidak mencukupi
     * @throws OverBudgetException dideklarasikan untuk memenuhi tanda tangan metode (tidak dilemparkan)
     */
    @Override
    public void eksekusiTugas(Task task, Committee committee) throws OverloadException, OverBudgetException {
        // Validasi kapasitas beban kerja
        if (committee.getCurrentWorkload() + task.getDifficulty() > committee.getMaxCapacity()) {
            throw new OverloadException("Kapasitas panitia tidak mencukupi untuk tugas: " + task.getTaskName());
        }
        // Menambahkan beban kerja pada panitia
        committee.tambahBebanKerja(task);
    }

    @Override
    public String buatLaporan() {
        return super.buatLaporan() + " | Tipe: Acara (Validasi beban kerja)";
    }
}
