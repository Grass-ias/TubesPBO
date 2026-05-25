package eventplanner.database;

import eventplanner.division.AcaraDivision;
import eventplanner.division.Division;
import eventplanner.division.KonsumsiDivision;
import eventplanner.division.LogisticDivision;
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

    public void updateDivision(Division division) {
        String sql = "UPDATE tabel_divisi SET nama_divisi = ?, allocated_budget = ? WHERE id_divisi = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, division.getDivisionName());
            pstmt.setDouble(2, division.getAllocatedBudget());
            pstmt.setString(3, division.getDivisionId());
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
        String sql = "SELECT * FROM tabel_divisi WHERE id_event = ? ORDER BY nama_divisi";
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
                    } else {
                        div = new LogisticDivision(budget);
                    }
                    div.setDivisionId(id);
                    list.add(div);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
