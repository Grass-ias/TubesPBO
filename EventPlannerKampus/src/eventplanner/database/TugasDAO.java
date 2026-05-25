package eventplanner.database;

import eventplanner.model.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TugasDAO {
    
    public void insertTugas(Task task, String id_event) {
        String sql = "INSERT INTO tabel_tugas (id_tugas, nama_tugas, difficulty, task_cost, id_event, id_panitia, id_divisi, deadline, priority, status, completed_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTaskId());
            pstmt.setString(2, task.getTaskName());
            pstmt.setInt(3, task.getDifficulty());
            pstmt.setDouble(4, task.getTaskCost());
            pstmt.setString(5, id_event);
            setNullableString(pstmt, 6, task.getCommitteeId());
            setNullableString(pstmt, 7, task.getDivisionId());
            setNullableString(pstmt, 8, task.getDeadline());
            pstmt.setString(9, task.getPriority());
            pstmt.setString(10, task.getStatus());
            setNullableString(pstmt, 11, task.getCompletedAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTugas(Task task) {
        String sql = "UPDATE tabel_tugas SET nama_tugas = ?, difficulty = ?, task_cost = ?, id_panitia = ?, id_divisi = ?, deadline = ?, priority = ?, status = ?, completed_at = ? WHERE id_tugas = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTaskName());
            pstmt.setInt(2, task.getDifficulty());
            pstmt.setDouble(3, task.getTaskCost());
            setNullableString(pstmt, 4, task.getCommitteeId());
            setNullableString(pstmt, 5, task.getDivisionId());
            setNullableString(pstmt, 6, task.getDeadline());
            pstmt.setString(7, task.getPriority());
            pstmt.setString(8, task.getStatus());
            setNullableString(pstmt, 9, task.getCompletedAt());
            pstmt.setString(10, task.getTaskId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTugas(String id_tugas) {
        String sql = "DELETE FROM tabel_tugas WHERE id_tugas = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_tugas);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getTugasByEvent(String id_event) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_tugas WHERE id_event = ? ORDER BY status = 'Selesai', deadline IS NULL, deadline, priority";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_event);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setTaskId(rs.getString("id_tugas"));
                    task.setTaskName(rs.getString("nama_tugas"));
                    task.setDifficulty(rs.getInt("difficulty"));
                    task.setTaskCost(rs.getDouble("task_cost"));
                    task.setCommitteeId(rs.getString("id_panitia"));
                    task.setDivisionId(rs.getString("id_divisi"));
                    task.setDeadline(rs.getString("deadline"));
                    task.setPriority(rs.getString("priority"));
                    task.setStatus(rs.getString("status"));
                    task.setCompletedAt(rs.getString("completed_at"));
                    list.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void assignPanitiaToTugas(String id_tugas, String id_panitia) {
        String sql = "UPDATE tabel_tugas SET id_panitia = ? WHERE id_tugas = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (id_panitia == null) {
                pstmt.setNull(1, Types.VARCHAR);
            } else {
                pstmt.setString(1, id_panitia);
            }
            pstmt.setString(2, id_tugas);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void completeTugas(Task task, String id_panitia, String completedAt) {
        String sql = "UPDATE tabel_tugas SET id_panitia = ?, id_divisi = ?, status = ?, completed_at = ? WHERE id_tugas = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setNullableString(pstmt, 1, id_panitia);
            setNullableString(pstmt, 2, task.getDivisionId());
            pstmt.setString(3, "Selesai");
            setNullableString(pstmt, 4, completedAt);
            pstmt.setString(5, task.getTaskId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getUnassignedTugasForCommittee(String id_event, int remainingCapacity) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_tugas WHERE id_event = ? AND (status IS NULL OR status <> 'Selesai') AND difficulty <= ? ORDER BY deadline IS NULL, deadline, priority";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_event);
            pstmt.setInt(2, remainingCapacity);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setTaskId(rs.getString("id_tugas"));
                    task.setTaskName(rs.getString("nama_tugas"));
                    task.setDifficulty(rs.getInt("difficulty"));
                    task.setTaskCost(rs.getDouble("task_cost"));
                    task.setCommitteeId(rs.getString("id_panitia"));
                    task.setDivisionId(rs.getString("id_divisi"));
                    task.setDeadline(rs.getString("deadline"));
                    task.setPriority(rs.getString("priority"));
                    task.setStatus(rs.getString("status"));
                    task.setCompletedAt(rs.getString("completed_at"));
                    list.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void setNullableString(PreparedStatement pstmt, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            pstmt.setNull(index, Types.VARCHAR);
        } else {
            pstmt.setString(index, value);
        }
    }
}
