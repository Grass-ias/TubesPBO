package eventplanner.database;

import eventplanner.model.Committee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PanitiaDAO {
    
    public void insertPanitia(Committee committee, String id_event) {
        String sql = "INSERT INTO tabel_panitia (id_panitia, nama_panitia, max_capacity, current_workload, id_event) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, committee.getCommitteeId());
            pstmt.setString(2, committee.getName());
            pstmt.setInt(3, committee.getMaxCapacity());
            pstmt.setInt(4, committee.getCurrentWorkload());
            pstmt.setString(5, id_event);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePanitia(Committee committee) {
        String sql = "UPDATE tabel_panitia SET nama_panitia = ?, max_capacity = ?, current_workload = ? WHERE id_panitia = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, committee.getName());
            pstmt.setInt(2, committee.getMaxCapacity());
            pstmt.setInt(3, committee.getCurrentWorkload());
            pstmt.setString(4, committee.getCommitteeId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePanitia(String id_panitia) {
        String sql = "DELETE FROM tabel_panitia WHERE id_panitia = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_panitia);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Committee> getPanitiaByEvent(String id_event) {
        List<Committee> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_panitia WHERE id_event = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_event);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Committee committee = new Committee();
                    committee.setCommitteeId(rs.getString("id_panitia"));
                    committee.setName(rs.getString("nama_panitia"));
                    committee.setMaxCapacity(rs.getInt("max_capacity"));
                    committee.setCurrentWorkload(rs.getInt("current_workload"));
                    list.add(committee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Committee getPanitiaById(String id_panitia) {
        String sql = "SELECT * FROM tabel_panitia WHERE id_panitia = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_panitia);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Committee committee = new Committee();
                    committee.setCommitteeId(rs.getString("id_panitia"));
                    committee.setName(rs.getString("nama_panitia"));
                    committee.setMaxCapacity(rs.getInt("max_capacity"));
                    committee.setCurrentWorkload(rs.getInt("current_workload"));
                    return committee;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Committee> getPanitiaForTaskExecution(String id_event, int difficulty) {
        List<Committee> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_panitia WHERE id_event = ? AND (current_workload + ?) <= max_capacity";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_event);
            pstmt.setInt(2, difficulty);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Committee committee = new Committee();
                    committee.setCommitteeId(rs.getString("id_panitia"));
                    committee.setName(rs.getString("nama_panitia"));
                    committee.setMaxCapacity(rs.getInt("max_capacity"));
                    committee.setCurrentWorkload(rs.getInt("current_workload"));
                    list.add(committee);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
