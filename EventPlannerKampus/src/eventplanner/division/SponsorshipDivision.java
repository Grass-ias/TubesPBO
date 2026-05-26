package eventplanner.division;

import eventplanner.model.Task;
import eventplanner.model.Committee;
import eventplanner.exception.OverloadException;
import eventplanner.exception.OverBudgetException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Subkelas yang merepresentasikan Divisi Sponsorship.
 * Divisi ini bertugas mencari dana sehingga memiliki kebijakan biaya nol (Zero-Cost Policy).
 */
public class SponsorshipDivision extends Division {

    /**
     * Konstruktor untuk menginisialisasi Divisi Sponsorship dengan anggaran tertentu.
     * 
     * @param allocatedBudget alokasi anggaran untuk divisi sponsorship
     */
    public SponsorshipDivision(double allocatedBudget) {
        super("Divisi Sponsorship", allocatedBudget);
    }

    /**
     * Mengeksekusi tugas sponsorship dengan melakukan validasi biaya tugas dan kapasitas beban kerja panitia.
     * Tugas sponsorship tidak boleh menggunakan anggaran operasional (Biaya harus Rp 0).
     * Jika biaya tugas lebih dari Rp 0, akan dilemparkan OverBudgetException.
     * Jika beban kerja melebihi kapasitas maksimal panitia, akan dilemparkan OverloadException.
     * 
     * @param task tugas sponsorship yang akan dieksekusi
     * @param committee anggota panitia yang ditugaskan untuk tugas tersebut
     * @throws OverloadException jika kapasitas beban kerja panitia Sponsorship tidak mencukupi
     * @throws OverBudgetException jika tugas sponsorship menggunakan anggaran operasional (Biaya > Rp 0)
     */
    @Override
    public void eksekusiTugas(Task task, Committee committee) throws OverloadException, OverBudgetException {
        if (task.getTaskCost() > 0) {
            throw new OverBudgetException("Tugas Sponsorship tidak boleh menggunakan anggaran operasional (Biaya harus Rp 0).");
        }
        if (committee.getCurrentWorkload() + task.getDifficulty() > committee.getMaxCapacity()) {
            throw new OverloadException("Kapasitas panitia Sponsorship tidak mencukupi.");
        }
        
        committee.tambahBebanKerja(task);
    }

    @Override
    public String buatLaporan() {
        return super.buatLaporan();
    }
}
