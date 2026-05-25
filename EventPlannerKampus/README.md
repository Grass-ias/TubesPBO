# Event Planner Kampus

Aplikasi Java Swing untuk mengelola persiapan event kampus berbasis MySQL.

## Fitur Utama

- Dashboard event aktif: sisa dana, progres tugas, beban kerja panitia, status persiapan.
- Manajemen event: tambah, edit, hapus event lengkap dengan tanggal dan jam.
- Manajemen divisi: Divisi Acara, Divisi Konsumsi, dan Divisi Logistik dengan kontrol budget.
- Backlog tugas: prioritas, deadline, estimasi biaya, divisi penanggung jawab, PIC, dan status.
- Manajemen panitia: kapasitas kerja, beban berjalan, dan indikator utilisasi.
- Eksekusi tugas: validasi kapasitas panitia dan budget divisi sebelum tugas ditandai selesai.
- Laporan event: ringkasan event, divisi, panitia, tugas, prioritas, deadline, dan status.
- Database auto-upgrade: kolom baru untuk tugas dibuat otomatis saat aplikasi dijalankan.

## Cara Menjalankan

1. Pastikan MySQL sudah berjalan.
2. Sesuaikan `USER` dan `PASSWORD` di:

   `src/eventplanner/database/DatabaseConnection.java`

3. Jalankan main class:

   `eventplanner.main.MainGUI`

Atau dari terminal di folder project:

```powershell
java -cp "build\classes;lib\mysql-connector-j-9.7.0.jar" eventplanner.main.MainGUI
```

## Alur Demo Singkat

1. Buat event baru dari tab Event.
2. Tambahkan tiga divisi: Acara, Konsumsi, dan Logistik.
3. Tambahkan beberapa panitia dengan kapasitas berbeda.
4. Tambahkan beberapa tugas dengan prioritas dan deadline.
5. Buka Dashboard untuk menunjukkan progres dan status persiapan.
6. Buka Eksekusi & Laporan, pilih tugas dan PIC, lalu tandai selesai.
7. Tunjukkan perubahan progress, budget divisi, dan beban panitia.
8. Klik Buat Laporan Event.

## Catatan Database

Aplikasi otomatis membuat database `db_event_kampus` dan tabel yang dibutuhkan jika belum ada.
Untuk setup manual, lihat `database_setup.sql`.

## Panduan Lengkap

Penjelasan detail tentang cara kerja sistem, relasi database, konsep OOP, alur GUI, dan skenario demo ada di:

`PANDUAN_PROYEK.md`
