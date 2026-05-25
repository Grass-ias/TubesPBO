package eventplanner.division;

import eventplanner.model.Task;
import eventplanner.model.Committee;
import eventplanner.exception.OverloadException;
import eventplanner.exception.OverBudgetException;

/**
 * Kelas abstrak yang merepresentasikan suatu divisi dalam sistem perencana acara kampus.
 * Mengimplementasikan antarmuka Reportable untuk menyediakan laporan berkala divisi.
 */
public abstract class Division implements Reportable {
    protected String divisionId;
    protected String divisionName;
    protected double allocatedBudget;

    /**
     * Konstruktor untuk menginisialisasi nama divisi dan alokasi anggaran awal.
     * 
     * @param divisionName nama dari divisi terkait
     * @param allocatedBudget jumlah anggaran yang dialokasikan untuk divisi
     */
    public Division(String divisionName, double allocatedBudget) {
        this.divisionId = java.util.UUID.randomUUID().toString();
        this.divisionName = divisionName;
        this.allocatedBudget = allocatedBudget;
    }

    // Pengakses (Getter) dan Pengubah (Setter)

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public double getAllocatedBudget() {
        return allocatedBudget;
    }

    public void setAllocatedBudget(double allocatedBudget) {
        this.allocatedBudget = allocatedBudget;
    }

    /**
     * Metode abstrak untuk melakukan eksekusi tugas tertentu oleh anggota panitia yang ditunjuk.
     * 
     * @param task objek tugas yang akan dieksekusi
     * @param committee anggota panitia yang ditugaskan
     * @throws OverloadException jika beban kerja panitia melampaui batas maksimal
     * @throws OverBudgetException jika anggaran divisi tidak mencukupi
     */
    public abstract void eksekusiTugas(Task task, Committee committee) throws OverloadException, OverBudgetException;

    /**
     * Implementasi dasar untuk membuat laporan divisi yang berisi nama divisi dan sisa anggaran.
     */
    @Override
    public String buatLaporan() {
        return "Laporan Divisi: " + divisionName + " | Sisa Anggaran: " + allocatedBudget;
    }
}
