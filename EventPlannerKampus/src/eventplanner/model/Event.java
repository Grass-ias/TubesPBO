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
        double totalAllocated = 0;
        for (Division div : daftarDivisi) {
            double budget = new eventplanner.database.DivisionDAO().getBudgetById(div.getDivisionId());
            if (budget < 0) {
                budget = div.getAllocatedBudget();
            }
            totalAllocated += budget;
        }
        double sisaBudget = totalBudget - totalAllocated;

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("               LAPORAN ACARA KAMPUS               \n");
        sb.append("==================================================\n");
        sb.append("Nama Event        : ").append(eventName).append("\n");
        sb.append("Sisa Total Budget : ").append(String.format("Rp %,.0f", sisaBudget)).append("\n");
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
        sb.append("Laporan Kontribusi Panitia:\n");
        if (daftarPanitia.isEmpty()) {
            sb.append(" - (Belum ada panitia yang terdaftar)\n");
        } else {
            for (Committee c : daftarPanitia) {
                int count = 0;
                for (Task t : daftarTugas) {
                    if (t.getIdPanitia() != null && t.getIdPanitia().equals(c.getCommitteeId())) {
                        count++;
                    }
                }
                sb.append(" - ").append(c.getName())
                  .append(": Mengerjakan ").append(count).append(" Tugas (Total Beban: ")
                  .append(c.getCurrentWorkload()).append("/").append(c.getMaxCapacity()).append(")\n");
            }
        }
        sb.append("==================================================");
        return sb.toString();
    }

    /**
     * Menghitung status event secara real-time berdasarkan tanggal dan waktu saat ini.
     * 
     * @return "Preparation", "On-going", atau "Finished"
     */
    public String getStatus() {
        if (tanggalMulai == null || tanggalSelesai == null || waktuMulai == null || waktuSelesai == null ||
            tanggalMulai.isEmpty() || tanggalSelesai.isEmpty() || waktuMulai.isEmpty() || waktuSelesai.isEmpty()) {
            return "Preparation";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            java.time.LocalDateTime waktuMulaiEvent = java.time.LocalDateTime.parse(tanggalMulai.trim() + " " + waktuMulai.trim(), formatter);
            java.time.LocalDateTime waktuSelesaiEvent = java.time.LocalDateTime.parse(tanggalSelesai.trim() + " " + waktuSelesai.trim(), formatter);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();

            if (now.isBefore(waktuMulaiEvent)) {
                return "Preparation";
            } else if (!now.isBefore(waktuMulaiEvent) && !now.isAfter(waktuSelesaiEvent)) {
                return "On-going";
            } else {
                return "Finished";
            }
        } catch (Exception e) {
            System.err.println("Gagal mengurai tanggal event: " + eventName + ". " + e.getMessage());
            return "Preparation";
        }
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
