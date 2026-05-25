# Panduan Proyek Event Planner Kampus

Dokumen ini menjelaskan cara kerja sistem, struktur folder, alur penggunaan, alur database, konsep OOP yang dipakai, dan skenario demo untuk presentasi.

## 1. Ringkasan Sistem

Event Planner Kampus adalah aplikasi desktop Java Swing untuk membantu panitia kampus mengelola persiapan event. Sistem ini menyimpan data ke MySQL melalui JDBC.

Fokus utama aplikasi:

- Membuat dan mengelola event kampus.
- Mengatur divisi event.
- Mengelola data panitia dan kapasitas kerja.
- Membuat backlog atau daftar rencana tugas.
- Mengeksekusi tugas dengan validasi budget dan kapasitas.
- Menampilkan dashboard progres event.
- Membuat laporan event.

## 2. Teknologi yang Digunakan

- Bahasa: Java
- GUI: Java Swing
- Database: MySQL
- Koneksi database: JDBC
- Connector: `lib/mysql-connector-j-9.7.0.jar`
- Main class: `eventplanner.main.MainGUI`

## 3. Struktur Folder Penting

```text
EventPlannerKampus/
├── src/
│   └── eventplanner/
│       ├── database/
│       │   ├── DatabaseConnection.java
│       │   ├── EventDAO.java
│       │   ├── DivisionDAO.java
│       │   ├── PanitiaDAO.java
│       │   └── TugasDAO.java
│       ├── division/
│       │   ├── Division.java
│       │   ├── AcaraDivision.java
│       │   ├── KonsumsiDivision.java
│       │   └── LogisticDivision.java
│       ├── exception/
│       │   ├── OverBudgetException.java
│       │   └── OverloadException.java
│       ├── gui/
│       │   └── MainFrame.java
│       ├── main/
│       │   ├── MainGUI.java
│       │   └── MainEPK.java
│       └── model/
│           ├── Event.java
│           ├── Committee.java
│           ├── Task.java
│           └── TaskRepository.java
├── lib/
│   └── mysql-connector-j-9.7.0.jar
├── database_setup.sql
├── README.md
└── PANDUAN_PROYEK.md
```

## 4. Cara Menjalankan Aplikasi

1. Pastikan MySQL sudah berjalan.
2. Buka file:

   `src/eventplanner/database/DatabaseConnection.java`

3. Sesuaikan user dan password MySQL:

```java
private static final String USER = "root";
private static final String PASSWORD = "password_mysql_kamu";
```

4. Jalankan main class:

```text
eventplanner.main.MainGUI
```

Atau dari terminal:

```powershell
java -cp "bin;lib\mysql-connector-j-9.7.0.jar" eventplanner.main.MainGUI
```

## 5. Cara Kerja Database

Database yang digunakan:

```sql
db_event_kampus
```

Tabel utama:

- `tabel_event`
- `tabel_divisi`
- `tabel_panitia`
- `tabel_tugas`

Aplikasi otomatis membuat database dan tabel saat pertama kali dijalankan melalui `DatabaseConnection.java`. Jika tabel lama belum punya kolom baru, aplikasi juga otomatis menambahkan kolom:

- `id_divisi`
- `deadline`
- `priority`
- `status`
- `completed_at`

Artinya, database lama tidak perlu dihapus.

## 6. Relasi Data

Relasi sistem:

- Satu event punya banyak divisi.
- Satu event punya banyak panitia.
- Satu event punya banyak tugas.
- Satu tugas bisa punya satu divisi penanggung jawab.
- Satu tugas bisa punya satu panitia sebagai PIC.

Secara sederhana:

```text
Event
├── Divisi
├── Panitia
└── Tugas
    ├── Divisi penanggung jawab
    └── PIC panitia
```

## 7. Alur Penggunaan Aplikasi

### 7.1 Membuat Event

Masuk ke tab `Event`, lalu isi:

- Nama Event
- Budget Awal Event
- Tanggal Mulai
- Tanggal Selesai
- Waktu Mulai
- Waktu Selesai

Setelah event dibuat, event akan muncul di sidebar kiri.

### 7.2 Membuat Divisi

Masuk ke tab `Divisi`, pilih jenis divisi:

- Divisi Acara
- Divisi Konsumsi
- Divisi Logistik

Lalu isi budget divisi.

Saat divisi ditambahkan, budget divisi akan mengurangi sisa dana event. Jika budget event tidak cukup, sistem menolak input.

### 7.3 Membuat Panitia

Masuk ke tab `Panitia`, isi:

- Nama panitia
- Kapasitas beban kerja

Kapasitas ini dipakai untuk validasi saat tugas dieksekusi.

### 7.4 Membuat Tugas

Masuk ke tab `Tugas`, isi:

- Nama tugas
- Divisi penanggung jawab
- Prioritas
- Deadline
- Beban kerja
- Estimasi biaya

Tugas yang baru dibuat berstatus `Direncanakan`.

### 7.5 Mengeksekusi Tugas

Masuk ke tab `Eksekusi & Laporan`, lalu pilih:

- Tugas
- Divisi eksekusi
- PIC panitia

Klik `Tandai Selesai`.

Sistem akan mengecek:

- Apakah kapasitas panitia masih cukup.
- Apakah budget divisi masih cukup.
- Apakah tugas belum selesai.
- Apakah divisi eksekusi sesuai dengan divisi penanggung jawab tugas.

Jika lolos validasi:

- Status tugas berubah menjadi `Selesai`.
- Panitia mendapat tambahan beban kerja.
- Budget divisi berkurang sesuai biaya tugas.
- Dashboard diperbarui.

## 8. Logika Validasi

### Budget Event

Saat divisi dibuat, budget divisi mengurangi sisa dana event.

Jika:

```text
budget divisi > sisa dana event
```

maka sistem menolak penambahan divisi.

### Budget Divisi

Untuk Divisi Konsumsi dan Divisi Logistik, biaya tugas mengurangi budget divisi.

Jika:

```text
biaya tugas > sisa budget divisi
```

maka sistem menolak eksekusi tugas.

### Kapasitas Panitia

Saat tugas dieksekusi, sistem mengecek:

```text
beban panitia saat ini + beban tugas <= kapasitas maksimal
```

Jika tidak cukup, sistem menolak eksekusi tugas.

### Status Tugas

Tugas punya dua status utama:

- `Direncanakan`
- `Selesai`

Tugas yang sudah selesai tidak bisa dieksekusi ulang.

## 9. Konsep OOP yang Dipakai

### Class dan Object

Contoh class:

- `Event`
- `Task`
- `Committee`
- `Division`

Setiap data di aplikasi direpresentasikan sebagai object.

### Inheritance

`Division` adalah class abstrak. Class turunannya:

- `AcaraDivision`
- `KonsumsiDivision`
- `LogisticDivision`

### Polymorphism

Setiap divisi punya implementasi `eksekusiTugas()` sendiri.

Contoh:

- Divisi Acara fokus ke validasi kapasitas panitia.
- Divisi Konsumsi fokus ke validasi budget konsumsi.
- Divisi Logistik fokus ke validasi budget logistik.

### Encapsulation

Atribut class dibuat private/protected dan diakses melalui getter-setter.

Contoh:

```java
private String taskName;

public String getTaskName() {
    return taskName;
}
```

### Exception Handling

Sistem menggunakan custom exception:

- `OverBudgetException`
- `OverloadException`

Exception ini dipakai untuk menangani kesalahan logika seperti budget tidak cukup atau panitia overload.

## 10. Fungsi Setiap Tab GUI

### Dashboard

Menampilkan:

- Dana event tersisa.
- Progres tugas.
- Beban panitia.
- Sisa dana divisi.
- Biaya yang sudah tereksekusi.
- Tugas berikutnya.
- Checklist kesiapan demo.

### Event

Untuk membuat, mengedit, dan menghapus event.

### Tugas

Untuk membuat, mengedit, dan menghapus backlog tugas.

### Divisi

Untuk membuat, mengedit, dan menghapus divisi event.

### Panitia

Untuk membuat, mengedit, dan menghapus data panitia.

### Eksekusi & Laporan

Untuk menandai tugas sebagai selesai dan membuat laporan event.

## 11. Alur Data dari GUI ke Database

Contoh saat membuat tugas:

```text
User isi form tugas
        ↓
MainFrame membaca input
        ↓
Input divalidasi
        ↓
Object Task dibuat
        ↓
TugasDAO.insertTugas()
        ↓
Data masuk ke tabel_tugas
        ↓
Tabel GUI dan dashboard refresh
```

Contoh saat eksekusi tugas:

```text
User pilih tugas, divisi, dan PIC
        ↓
MainFrame memanggil division.eksekusiTugas()
        ↓
Validasi budget dan kapasitas
        ↓
TugasDAO.completeTugas()
        ↓
PanitiaDAO.updatePanitia()
        ↓
DivisionDAO.updateDivision()
        ↓
Dashboard dan laporan diperbarui
```

## 12. Query untuk Mengecek Data di Workbench

```sql
USE db_event_kampus;

SELECT * FROM tabel_event;
SELECT * FROM tabel_divisi;
SELECT * FROM tabel_panitia;
SELECT * FROM tabel_tugas;
```

Query ringkasan:

```sql
SELECT 
    e.nama_event,
    COUNT(DISTINCT d.id_divisi) AS total_divisi,
    COUNT(DISTINCT p.id_panitia) AS total_panitia,
    COUNT(DISTINCT t.id_tugas) AS total_tugas
FROM tabel_event e
LEFT JOIN tabel_divisi d ON e.id_event = d.id_event
LEFT JOIN tabel_panitia p ON e.id_event = p.id_event
LEFT JOIN tabel_tugas t ON e.id_event = t.id_event
GROUP BY e.id_event, e.nama_event;
```

## 13. Skenario Demo untuk Presentasi

1. Buka aplikasi.
2. Tunjukkan sidebar dan dashboard.
3. Buat event baru, misalnya `Festival Teknologi Kampus`.
4. Tambahkan divisi:
   - Divisi Acara
   - Divisi Konsumsi
   - Divisi Logistik
5. Tambahkan panitia dengan kapasitas berbeda.
6. Tambahkan tugas:
   - Susun rundown
   - Pesan konsumsi
   - Siapkan panggung
7. Tunjukkan tab Dashboard.
8. Eksekusi satu atau dua tugas.
9. Tunjukkan bahwa:
   - Progres tugas naik.
   - Beban panitia naik.
   - Budget divisi berkurang.
10. Klik `Buat Laporan Event`.
11. Buka Workbench dan tampilkan data di tabel.

## 14. Troubleshooting

### Error Access denied for user

Penyebab: user/password MySQL tidak sesuai.

Solusi: cek `USER` dan `PASSWORD` di `DatabaseConnection.java`.

### Error Communications link failure

Penyebab: MySQL belum berjalan.

Solusi: jalankan service MySQL, lalu buka aplikasi lagi.

### Connector MySQL tidak terbaca

Pastikan file ini ada:

```text
lib/mysql-connector-j-9.7.0.jar
```

### Tabel tidak muncul di Workbench

Klik refresh di bagian `Schemas`, lalu cek database:

```sql
USE db_event_kampus;
SHOW TABLES;
```

## 15. Catatan Pengembangan

Beberapa hal yang bisa dikembangkan lagi:

- Login admin.
- Export laporan ke PDF.
- Kalender visual event.
- Filter tugas berdasarkan divisi/status.
- Role panitia.
- Riwayat perubahan data.

Versi saat ini sudah mencakup alur utama event planner: perencanaan event, pengaturan tim, perencanaan tugas, eksekusi, dashboard, dan laporan.
