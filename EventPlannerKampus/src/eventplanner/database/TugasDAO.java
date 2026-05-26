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
        String sql = "INSERT INTO tabel_tugas (id_tugas, nama_tugas, difficulty, task_cost, id_event, id_panitia, id_divisi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTaskId());
            pstmt.setString(2, task.getTaskName());
            pstmt.setInt(3, task.getDifficulty());
            pstmt.setDouble(4, task.getTaskCost());
            pstmt.setString(5, id_event);
            pstmt.setNull(6, Types.VARCHAR); // Set unassigned initially
            pstmt.setString(7, task.getIdDivisi());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTugas(Task task) {
        String sql = "UPDATE tabel_tugas SET nama_tugas = ?, difficulty = ?, task_cost = ?, id_divisi = ? WHERE id_tugas = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTaskName());
            pstmt.setInt(2, task.getDifficulty());
            pstmt.setDouble(3, task.getTaskCost());
            pstmt.setString(4, task.getIdDivisi());
            pstmt.setString(5, task.getTaskId());
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
        String sql = "SELECT * FROM tabel_tugas WHERE id_event = ?";
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
                    task.setIdPanitia(rs.getString("id_panitia"));
                    task.setIdDivisi(rs.getString("id_divisi"));
                    list.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void assignPanitiaToTugas(String id_tugas, String id_panitia) {
        if (id_panitia != null) {
            String checkSql = "SELECT id_panitia FROM tabel_tugas WHERE id_tugas = ?";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, id_tugas);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String existingPanitia = rs.getString("id_panitia");
                        if (existingPanitia != null && !existingPanitia.equals(id_panitia)) {
                            throw new RuntimeException("Peringatan Sistem: Tugas ini sudah dialokasikan ke panitia lain!");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String sql = "UPDATE tabel_tugas SET id_panitia = ? WHERE id_tugas = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (id_panitia == null) {
                pstmt.setNull(1, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(1, id_panitia);
            }
            pstmt.setString(2, id_tugas);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getUnassignedTugasForCommittee(String id_event, int remainingCapacity) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_tugas WHERE id_event = ? AND id_panitia IS NULL AND difficulty <= ?";
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
                    task.setIdPanitia(rs.getString("id_panitia"));
                    task.setIdDivisi(rs.getString("id_divisi"));
                    list.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Task> getUnassignedTugasByEvent(String id_event) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_tugas WHERE id_event = ? AND id_panitia IS NULL";
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
                    task.setIdPanitia(rs.getString("id_panitia"));
                    task.setIdDivisi(rs.getString("id_divisi"));
                    list.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Task> getTugasByPanitia(String id_panitia) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_tugas WHERE id_panitia = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_panitia);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setTaskId(rs.getString("id_tugas"));
                    task.setTaskName(rs.getString("nama_tugas"));
                    task.setDifficulty(rs.getInt("difficulty"));
                    task.setTaskCost(rs.getDouble("task_cost"));
                    task.setIdPanitia(rs.getString("id_panitia"));
                    task.setIdDivisi(rs.getString("id_divisi"));
                    list.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
