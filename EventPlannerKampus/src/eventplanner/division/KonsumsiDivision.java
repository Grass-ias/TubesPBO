package eventplanner.division;

import eventplanner.model.Task;
import eventplanner.model.Committee;
import eventplanner.exception.OverloadException;
import eventplanner.exception.OverBudgetException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Subkelas yang merepresentasikan Divisi Konsumsi.
 * Divisi ini berfokus pada validasi kecukupan anggaran konsumsi sebelum tugas dilaksanakan.
 */
public class KonsumsiDivision extends Division {

    /**
     * Konstruktor untuk menginisialisasi Divisi Konsumsi dengan anggaran tertentu.
     * 
     * @param allocatedBudget alokasi anggaran untuk divisi konsumsi
     */
    public KonsumsiDivision(double allocatedBudget) {
        super("Divisi Konsumsi", allocatedBudget);
    }

    /**
     * Mengeksekusi tugas dengan mengurangi anggaran divisi konsumsi dan menugaskannya kepada panitia.
     * 
     * @param task tugas konsumsi yang akan dieksekusi
     * @param committee anggota panitia yang ditugaskan
     * @throws OverloadException jika beban kerja panitia melampaui batas kapasitas
     * @throws OverBudgetException jika anggaran divisi konsumsi tidak mencukupi
     */
    @Override
    public void eksekusiTugas(Task task, Committee committee) throws OverloadException, OverBudgetException {
        double sisaAnggaran = new eventplanner.database.DivisionDAO().getSisaAnggaranDivisi(this.divisionId);
        if (sisaAnggaran < 0) {
            sisaAnggaran = this.allocatedBudget;
        }
        if (task.getTaskCost() > sisaAnggaran) {
            throw new OverBudgetException("Budget tidak mencukupi");
        }
        
        // Menugaskan tugas kepada panitia (dapat melemparkan OverloadException)
        committee.tambahBebanKerja(task);
    }

    @Override
    public String buatLaporan() {
        return super.buatLaporan();
    }
}
