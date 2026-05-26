package eventplanner.division;

import eventplanner.model.Task;
import eventplanner.model.Committee;
import eventplanner.exception.OverloadException;
import eventplanner.exception.OverBudgetException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Subkelas yang merepresentasikan Divisi Dokumentasi.
 * Divisi ini berfokus pada validasi hibrida (gabungan anggaran divisi dan kapasitas beban kerja panitia).
 */
public class DokumentasiDivision extends Division {

    /**
     * Konstruktor untuk menginisialisasi Divisi Dokumentasi dengan anggaran tertentu.
     * 
     * @param allocatedBudget alokasi anggaran untuk divisi dokumentasi
     */
    public DokumentasiDivision(double allocatedBudget) {
        super("Divisi Dokumentasi", allocatedBudget);
    }

    /**
     * Mengeksekusi tugas dokumentasi dengan melakukan validasi anggaran divisi dan kapasitas beban kerja panitia.
     * Jika anggaran tidak mencukupi, akan dilemparkan OverBudgetException.
     * Jika beban kerja melebihi kapasitas maksimal panitia, akan dilemparkan OverloadException.
     * 
     * @param task tugas dokumentasi yang akan dieksekusi
     * @param committee anggota panitia yang ditugaskan untuk tugas tersebut
     * @throws OverloadException jika kapasitas beban kerja panitia Dokumentasi tidak mencukupi
     * @throws OverBudgetException jika anggaran divisi Dokumentasi tidak mencukupi
     */
    @Override
    public void eksekusiTugas(Task task, Committee committee) throws OverloadException, OverBudgetException {
        double sisaAnggaran = new eventplanner.database.DivisionDAO().getSisaAnggaranDivisi(this.divisionId);
        if (sisaAnggaran < 0) {
            sisaAnggaran = this.allocatedBudget;
        }
        if (task.getTaskCost() > sisaAnggaran) {
            throw new OverBudgetException("Anggaran divisi Dokumentasi tidak mencukupi.");
        }
        if (committee.getCurrentWorkload() + task.getDifficulty() > committee.getMaxCapacity()) {
            throw new OverloadException("Kapasitas panitia Dokumentasi tidak mencukupi.");
        }
        
        committee.tambahBebanKerja(task);
    }

    @Override
    public String buatLaporan() {
        return super.buatLaporan();
    }
}
