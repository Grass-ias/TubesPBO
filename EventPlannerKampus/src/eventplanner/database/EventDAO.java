package eventplanner.database;

import eventplanner.model.Event;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    public void insertEvent(Event event) {
        String eventSql = "INSERT INTO tabel_event (id_event, nama_event, total_budget, tanggal_mulai, tanggal_selesai, waktu_mulai, waktu_selesai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String divisionSql = "INSERT INTO tabel_divisi (id_divisi, nama_divisi, allocated_budget, id_event) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmtEvent = conn.prepareStatement(eventSql)) {
                pstmtEvent.setString(1, event.getEventId());
                pstmtEvent.setString(2, event.getEventName());
                pstmtEvent.setDouble(3, event.getTotalBudget());
                pstmtEvent.setString(4, event.getTanggalMulai());
                pstmtEvent.setString(5, event.getTanggalSelesai());
                pstmtEvent.setString(6, event.getWaktuMulai());
                pstmtEvent.setString(7, event.getWaktuSelesai());
                pstmtEvent.executeUpdate();
            }
            
            String[] divisionNames = {
                "Divisi Acara",
                "Divisi Humas & Publikasi",
                "Divisi Perlengkapan & Logistik",
                "Divisi Konsumsi",
                "Divisi Keamanan",
                "Divisi Dokumentasi",
                "Divisi Sponsorship"
            };
            
            try (PreparedStatement pstmtDiv = conn.prepareStatement(divisionSql)) {
                for (String name : divisionNames) {
                    pstmtDiv.setString(1, java.util.UUID.randomUUID().toString());
                    pstmtDiv.setString(2, name);
                    pstmtDiv.setDouble(3, 0.0);
                    pstmtDiv.setString(4, event.getEventId());
                    pstmtDiv.addBatch();
                }
                pstmtDiv.executeBatch();
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateEvent(Event event) {
        String sql = "UPDATE tabel_event SET nama_event = ?, total_budget = ?, tanggal_mulai = ?, tanggal_selesai = ?, waktu_mulai = ?, waktu_selesai = ? WHERE id_event = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getEventName());
            pstmt.setDouble(2, event.getTotalBudget());
            pstmt.setString(3, event.getTanggalMulai());
            pstmt.setString(4, event.getTanggalSelesai());
            pstmt.setString(5, event.getWaktuMulai());
            pstmt.setString(6, event.getWaktuSelesai());
            pstmt.setString(7, event.getEventId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(String id_event) {
        String sql = "DELETE FROM tabel_event WHERE id_event = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_event);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Event> getAllEvents() {
        List<Event> list = new ArrayList<>();
        String sql = "SELECT * FROM tabel_event";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getString("id_event"));
                event.setEventName(rs.getString("nama_event"));
                event.setTotalBudget(rs.getDouble("total_budget"));
                event.setTanggalMulai(rs.getString("tanggal_mulai"));
                event.setTanggalSelesai(rs.getString("tanggal_selesai"));
                event.setWaktuMulai(rs.getString("waktu_mulai"));
                event.setWaktuSelesai(rs.getString("waktu_selesai"));
                list.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Event getEventById(String id_event) {
        String sql = "SELECT * FROM tabel_event WHERE id_event = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_event);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Event event = new Event();
                    event.setEventId(rs.getString("id_event"));
                    event.setEventName(rs.getString("nama_event"));
                    event.setTotalBudget(rs.getDouble("total_budget"));
                    event.setTanggalMulai(rs.getString("tanggal_mulai"));
                    event.setTanggalSelesai(rs.getString("tanggal_selesai"));
                    event.setWaktuMulai(rs.getString("waktu_mulai"));
                    event.setWaktuSelesai(rs.getString("waktu_selesai"));
                    return event;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getEventCount() {
        String sql = "SELECT COUNT(*) AS total FROM tabel_event";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
