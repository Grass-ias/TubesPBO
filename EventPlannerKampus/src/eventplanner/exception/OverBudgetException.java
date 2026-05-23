package eventplanner.exception;

/**
 * Pengecualian kustom yang dilemparkan ketika alokasi anggaran divisi
 * tidak mencukupi untuk membiayai tugas yang diajukan.
 */
public class OverBudgetException extends Exception {
    /**
     * Konstruktor default untuk OverBudgetException.
     */
    public OverBudgetException() {
        super("Budget tidak mencukupi");
    }

    /**
     * Konstruktor dengan pesan kustom untuk OverBudgetException.
     * 
     * @param message pesan detail kesalahan
     */
    public OverBudgetException(String message) {
        super(message);
    }
}
