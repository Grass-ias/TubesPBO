package eventplanner.database;

import eventplanner.division.AcaraDivision;
import eventplanner.division.Division;
import eventplanner.division.KonsumsiDivision;
import eventplanner.division.LogisticDivision;
import eventplanner.division.HumasPublikasiDivision;
import eventplanner.division.KeamananDivision;
import eventplanner.division.DokumentasiDivision;
import eventplanner.division.SponsorshipDivision;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DivisionDAO {
    
    public void insertDivision(Division division, String id_event) {
        String sql = "INSERT INTO tabel_divisi (id_divisi, nama_divisi, allocated_budget, id_event) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, division.getDivisionId());
            pstmt.setString(2, division.getDivisionName());
            pstmt.setDouble(3, division.getAllocatedBudget());
            pstmt.setString(4, id_event);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDivision(String id_divisi) {
        String sql = "DELETE FROM tabel_divisi WHERE id_divisi = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_divisi);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Division> getDivisionsByEvent(String id_event) {
        List<Division> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_divisi WHERE id_event = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_event);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id_divisi");
                    String name = rs.getString("nama_divisi");
                    double budget = rs.getDouble("allocated_budget");
                    
                    Division div;
                    if (name.contains("Acara")) {
                        div = new AcaraDivision(budget);
                    } else if (name.contains("Konsumsi")) {
                        div = new KonsumsiDivision(budget);
                    } else if (name.contains("Humas") || name.contains("Publikasi")) {
                        div = new HumasPublikasiDivision(budget);
                    } else if (name.contains("Keamanan")) {
                        div = new KeamananDivision(budget);
                    } else if (name.contains("Dokumentasi")) {
                        div = new DokumentasiDivision(budget);
                    } else if (name.contains("Sponsorship")) {
                        div = new SponsorshipDivision(budget);
                    } else {
                        div = new LogisticDivision(budget);
                    }
                    div.setDivisionId(id);
                    div.setDivisionName(name);
                    list.add(div);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateDanaDivisi(String idDivisi, double danaBaru) {
        String sql = "UPDATE tabel_divisi SET allocated_budget = ? WHERE id_divisi = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, danaBaru);
            pstmt.setString(2, idDivisi);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Division getDivisionById(String id_divisi) {
        String sql = "SELECT * FROM tabel_divisi WHERE id_divisi = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_divisi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id_divisi");
                    String name = rs.getString("nama_divisi");
                    double budget = rs.getDouble("allocated_budget");
                    
                    Division div;
                    if (name.contains("Acara")) {
                        div = new AcaraDivision(budget);
                    } else if (name.contains("Konsumsi")) {
                        div = new KonsumsiDivision(budget);
                    } else if (name.contains("Humas") || name.contains("Publikasi")) {
                        div = new HumasPublikasiDivision(budget);
                    } else if (name.contains("Keamanan")) {
                        div = new KeamananDivision(budget);
                    } else if (name.contains("Dokumentasi")) {
                        div = new DokumentasiDivision(budget);
                    } else if (name.contains("Sponsorship")) {
                        div = new SponsorshipDivision(budget);
                    } else {
                        div = new LogisticDivision(budget);
                    }
                    div.setDivisionId(id);
                    div.setDivisionName(name);
                    return div;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getBudgetById(String id_divisi) {
        String sql = "SELECT allocated_budget FROM tabel_divisi WHERE id_divisi = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_divisi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("allocated_budget");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1.0;
    }

    public double getSisaAnggaranDivisi(String idDivisi) {
        String sql = "SELECT (d.allocated_budget - COALESCE((SELECT SUM(task_cost) FROM tabel_tugas WHERE id_divisi = d.id_divisi AND id_panitia IS NOT NULL), 0)) AS sisa_anggaran " +
                     "FROM tabel_divisi d " +
                     "WHERE d.id_divisi = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idDivisi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("sisa_anggaran");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1.0;
    }
}
