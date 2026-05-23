package eventplanner.model;

import eventplanner.exception.OverloadException;

/**
 * Objek Java Biasa (POJO) yang merepresentasikan anggota Panitia dalam sistem perencana acara.
 * Mengelola kapasitas kerja dan tugas yang diberikan kepada anggota terkait.
 */
public class Committee {
    private String committeeId;
    private String name;
    private int maxCapacity;
    private int currentWorkload;

    /**
     * Konstruktor default.
     */
    public Committee() {
    }

    /**
     * Konstruktor untuk inisialisasi awal panitia dengan beban kerja nol.
     * 
     * @param committeeId ID panitia
     * @param name nama anggota panitia
     * @param maxCapacity kapasitas beban kerja maksimal yang diperbolehkan
     */
    public Committee(String committeeId, String name, int maxCapacity) {
        this.committeeId = committeeId;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.currentWorkload = 0;
    }

    /**
     * Konstruktor lengkap untuk inisialisasi panitia beserta beban kerja saat ini.
     * 
     * @param committeeId ID panitia
     * @param name nama anggota panitia
     * @param maxCapacity kapasitas beban kerja maksimal
     * @param currentWorkload beban kerja saat ini
     */
    public Committee(String committeeId, String name, int maxCapacity, int currentWorkload) {
        this.committeeId = committeeId;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.currentWorkload = currentWorkload;
    }

    // Pengakses (Getter) dan Pengubah (Setter)

    public String getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(String committeeId) {
        this.committeeId = committeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentWorkload() {
        return currentWorkload;
    }

    public void setCurrentWorkload(int currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    /**
     * Menambahkan beban kerja panitia berdasarkan tingkat kesulitan tugas yang diberikan.
     * 
     * @param task objek tugas yang ditambahkan
     * @throws OverloadException jika penambahan beban kerja melebihi kapasitas maksimal
     */
    public void tambahBebanKerja(Task task) throws OverloadException {
        if (this.currentWorkload + task.getDifficulty() > this.maxCapacity) {
            throw new OverloadException("Kapasitas beban kerja melebihi batas maksimal!");
        }
        this.currentWorkload += task.getDifficulty();
    }

    @Override
    public String toString() {
        return "Committee{" +
                "committeeId='" + committeeId + '\'' +
                ", name='" + name + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", currentWorkload=" + currentWorkload +
                '}';
    }
}
