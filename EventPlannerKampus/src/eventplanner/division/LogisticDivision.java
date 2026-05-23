package eventplanner.division;

import eventplanner.model.Task;
import eventplanner.model.Committee;
import eventplanner.exception.OverloadException;
import eventplanner.exception.OverBudgetException;

/**
 * Subkelas yang merepresentasikan Divisi Logistik.
 * Divisi ini berfokus pada validasi kecukupan anggaran logistik sebelum tugas dilaksanakan.
 */
public class LogisticDivision extends Division {

    /**
     * Konstruktor untuk menginisialisasi Divisi Logistik dengan anggaran tertentu.
     * 
     * @param allocatedBudget alokasi anggaran untuk divisi logistik
     */
    public LogisticDivision(double allocatedBudget) {
        super("Divisi Logistik", allocatedBudget);
    }

    /**
     * Mengeksekusi tugas dengan mengurangi anggaran divisi logistik dan menugaskannya kepada panitia.
     * 
     * @param task tugas logistik yang akan dieksekusi
     * @param committee anggota panitia yang ditugaskan
     * @throws OverloadException jika beban kerja panitia melampaui batas kapasitas
     * @throws OverBudgetException jika anggaran divisi logistik tidak mencukupi
     */
    @Override
    public void eksekusiTugas(Task task, Committee committee) throws OverloadException, OverBudgetException {
        if (task.getTaskCost() > this.allocatedBudget) {
            throw new OverBudgetException("Budget tidak mencukupi");
        }
        
        // Menugaskan tugas kepada panitia (dapat melemparkan OverloadException)
        committee.tambahBebanKerja(task);
        
        // Pengurangan anggaran divisi
        this.allocatedBudget -= task.getTaskCost();
    }

    @Override
    public String buatLaporan() {
        return super.buatLaporan() + " | Tipe: Logistik (Validasi anggaran)";
    }
}
