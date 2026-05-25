CREATE DATABASE IF NOT EXISTS db_event_kampus;
USE db_event_kampus;

CREATE TABLE IF NOT EXISTS tabel_event (
    id_event VARCHAR(50) PRIMARY KEY,
    nama_event VARCHAR(100) NOT NULL,
    total_budget DOUBLE NOT NULL,
    tanggal_mulai VARCHAR(20),
    tanggal_selesai VARCHAR(20),
    waktu_mulai VARCHAR(20),
    waktu_selesai VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS tabel_panitia (
    id_panitia VARCHAR(50) PRIMARY KEY,
    nama_panitia VARCHAR(100) NOT NULL,
    max_capacity INT NOT NULL,
    current_workload INT NOT NULL DEFAULT 0,
    id_event VARCHAR(50),
    FOREIGN KEY (id_event) REFERENCES tabel_event(id_event) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tabel_divisi (
    id_divisi VARCHAR(50) PRIMARY KEY,
    nama_divisi VARCHAR(100) NOT NULL,
    allocated_budget DOUBLE NOT NULL,
    id_event VARCHAR(50),
    FOREIGN KEY (id_event) REFERENCES tabel_event(id_event) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tabel_tugas (
    id_tugas VARCHAR(50) PRIMARY KEY,
    nama_tugas VARCHAR(100) NOT NULL,
    difficulty INT NOT NULL,
    task_cost DOUBLE NOT NULL,
    id_event VARCHAR(50),
    id_panitia VARCHAR(50),
    id_divisi VARCHAR(50),
    deadline VARCHAR(20),
    priority VARCHAR(20) DEFAULT 'Sedang',
    status VARCHAR(20) DEFAULT 'Direncanakan',
    completed_at VARCHAR(30),
    FOREIGN KEY (id_event) REFERENCES tabel_event(id_event) ON DELETE CASCADE,
    FOREIGN KEY (id_panitia) REFERENCES tabel_panitia(id_panitia) ON DELETE SET NULL
);
