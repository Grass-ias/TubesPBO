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
    private String tanggalMulai;
    private String tanggalSelesai;
    private String waktuMulai;
    private String waktuSelesai;
    
    // Atribut Koleksi
    private List<Division> daftarDivisi = new ArrayList<>();
    private List<Committee> daftarPanitia = new ArrayList<>();
    private List<Task> daftarTugas = new ArrayList<>();

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
     * Konstruktor berparameter untuk mendefinisikan rincian acara dengan ID yang di-generate otomatis.
     * 
     * @param eventName nama dari acara tersebut
     * @param totalBudget total anggaran yang dialokasikan untuk keseluruhan acara
     */
    public Event(String eventName, double totalBudget) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventName = eventName;
        this.totalBudget = totalBudget;
    }

    // Getters and Setters for Date and Time
    public String getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(String tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public String getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(String tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(String waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(String waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
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
     * Menambahkan tugas ke dalam daftar tugas acara.
     * 
     * @param tugas tugas yang akan ditambahkan
     */
    public void tambahTugas(Task tugas) {
        this.daftarTugas.add(tugas);
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
        sb.append("Jadwal            : ").append(nullToDash(tanggalMulai)).append(" s.d. ")
                .append(nullToDash(tanggalSelesai)).append("\n");
        sb.append("Waktu             : ").append(nullToDash(waktuMulai)).append(" - ")
                .append(nullToDash(waktuSelesai)).append("\n");
        sb.append("Sisa Dana Event   : ").append(String.format("Rp %,.0f", totalBudget)).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Ringkasan Operasional:\n");
        sb.append("Total Divisi      : ").append(daftarDivisi.size()).append("\n");
        sb.append("Total Panitia     : ").append(daftarPanitia.size()).append("\n");
        sb.append("Total Tugas       : ").append(daftarTugas.size()).append("\n");
        sb.append("Tugas Selesai     : ").append(hitungTugasSelesai()).append("\n");
        sb.append("--------------------------------------------------\n");
        sb.append("Detail Laporan per Divisi:\n");
        if (daftarDivisi.isEmpty()) {
            sb.append(" - (Belum ada divisi yang terdaftar)\n");
        } else {
            for (Division div : daftarDivisi) {
                sb.append(" - ").append(div.buatLaporan()).append("\n");
            }
        }
        sb.append("--------------------------------------------------\n");
        sb.append("Daftar Tugas:\n");
        if (daftarTugas.isEmpty()) {
            sb.append(" - (Belum ada tugas yang direncanakan)\n");
        } else {
            for (Task task : daftarTugas) {
                sb.append(" - ").append(task.getTaskName())
                        .append(" | Prioritas: ").append(task.getPriority())
                        .append(" | Deadline: ").append(nullToDash(task.getDeadline()))
                        .append(" | Status: ").append(task.getStatus())
                        .append("\n");
            }
        }
        sb.append("==================================================");
        return sb.toString();
    }

    private int hitungTugasSelesai() {
        int count = 0;
        for (Task task : daftarTugas) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
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

    public List<Task> getDaftarTugas() {
        return daftarTugas;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", totalBudget=" + totalBudget +
                ", jumlahDivisi=" + daftarDivisi.size() +
                ", jumlahPanitia=" + daftarPanitia.size() +
                ", jumlahTugas=" + daftarTugas.size() +
                '}';
    }
}
