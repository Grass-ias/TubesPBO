package eventplanner.model;

import eventplanner.division.Division;
import eventplanner.exception.OverBudgetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Objek Java Biasa (POJO) yang merepresentasikan sebuah Acara (Event) dalam sistem.
 * Menerapkan konsep Agregasi/Komposisi dengan menyimpan daftar divisi dan panitia.
 */
public class Event {
    private String eventId;
    private String eventName;
    private double totalBudget;
    
    // Atribut Koleksi
    private List<Division> daftarDivisi = new ArrayList<>();
    private List<Committee> daftarPanitia = new ArrayList<>();

    /**
     * Konstruktor default.
     */
    public Event() {
    }

    /**
     * Konstruktor berparameter untuk mendefinisikan rincian acara.
     * 
     * @param eventId ID unik acara
     * @param eventName nama dari acara tersebut
     * @param totalBudget total anggaran yang dialokasikan untuk keseluruhan acara
     */
    public Event(String eventId, String eventName, double totalBudget) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.totalBudget = totalBudget;
    }

    /**
     * Menambahkan divisi ke dalam daftar divisi acara.
     * Mengurangi total budget event sesuai dengan alokasi budget divisi.
     * 
     * @param divisi divisi yang akan ditambahkan
     * @throws OverBudgetException jika total budget tidak mencukupi
     */
    public void tambahDivisi(Division divisi) throws OverBudgetException {
        if (this.totalBudget < divisi.getAllocatedBudget()) {
            throw new OverBudgetException("OverBudget: Anggaran total event tidak mencukupi untuk divisi '" 
                    + divisi.getDivisionName() + "' (Alokasi divisi: " + divisi.getAllocatedBudget() 
                    + ", Sisa budget event: " + this.totalBudget + ")");
        }
        this.totalBudget -= divisi.getAllocatedBudget();
        this.daftarDivisi.add(divisi);
    }

    /**
     * Menambahkan panitia ke dalam daftar panitia acara.
     * 
     * @param panitia anggota panitia yang akan ditambahkan
     */
    public void tambahPanitia(Committee panitia) {
        this.daftarPanitia.add(panitia);
    }

    /**
     * Membuat laporan acara secara komprehensif, merangkum data event
     * dan laporan masing-masing divisi secara polimorfik.
     * 
     * @return String laporan acara
     */
    public String buatLaporanAcara() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("               LAPORAN ACARA KAMPUS               \n");
        sb.append("==================================================\n");
        sb.append("Nama Event        : ").append(eventName).append("\n");
        sb.append("Sisa Total Budget : ").append(totalBudget).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Detail Laporan per Divisi:\n");
        if (daftarDivisi.isEmpty()) {
            sb.append(" - (Belum ada divisi yang terdaftar)\n");
        } else {
            for (Division div : daftarDivisi) {
                sb.append(" - ").append(div.buatLaporan()).append("\n");
            }
        }
        sb.append("==================================================");
        return sb.toString();
    }

    // Pengakses (Getter) dan Pengubah (Setter)

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public List<Division> getDaftarDivisi() {
        return daftarDivisi;
    }

    public List<Committee> getDaftarPanitia() {
        return daftarPanitia;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", totalBudget=" + totalBudget +
                ", jumlahDivisi=" + daftarDivisi.size() +
                ", jumlahPanitia=" + daftarPanitia.size() +
                '}';
    }
}
