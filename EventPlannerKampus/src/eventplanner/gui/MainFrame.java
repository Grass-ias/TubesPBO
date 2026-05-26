package eventplanner.gui;

import eventplanner.division.AcaraDivision;
import eventplanner.division.Division;
import eventplanner.division.KonsumsiDivision;
import eventplanner.division.LogisticDivision;
import eventplanner.exception.OverBudgetException;
import eventplanner.exception.OverloadException;
import eventplanner.model.Committee;
import eventplanner.model.Event;
import eventplanner.model.Task;
import eventplanner.database.*;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MainFrame extends JFrame {

    private static final Color BACKGROUND = new Color(244, 247, 251);
    private static final Color SURFACE = Color.WHITE;
    private static final Color PRIMARY = new Color(33, 103, 166);
    private static final Color PRIMARY_DARK = new Color(24, 68, 122);
    private static final Color ACCENT = new Color(33, 150, 136);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(219, 226, 236);

    // Global State In-Memory
    private final List<Event> daftarEventGlobal = new ArrayList<>();
    private final List<Event> daftarEventTampil = new ArrayList<>();
    private Event eventAktif = null;

    // DAOs
    private final EventDAO eventDAO = new EventDAO();
    private final DivisionDAO divisionDAO = new DivisionDAO();
    private final PanitiaDAO panitiaDAO = new PanitiaDAO();
    private final TugasDAO tugasDAO = new TugasDAO();

    // Event Date & Time Fields
    private JTextField txtTanggalMulai;
    private JTextField txtTanggalSelesai;
    private JTextField txtWaktuMulai;
    private JTextField txtWaktuSelesai;

    // Left Panel Components (Master View)
    private JTextField txtNewEventName;
    private JTextField txtNewEventBudget;
    private JButton btnCreateEvent;
    private JList<String> lstEvents;
    private DefaultListModel<String> eventListModel;
    private boolean isUpdatingDropdown = false;

    // Right Panel Components (Detail View)
    // Event Tab
    private JTextField txtEventId;
    private JTextField txtEventName;
    private JTextField txtEventBudget;
    private JButton btnAddEvent;

    // Task Tab
    private JTextField txtTaskId;
    private JTextField txtTaskName;
    private JTextField txtDifficulty;
    private JTextField txtTaskCost;
    private JButton btnAddTask;

    // Division Tab
    private JComboBox<String> cmbDivisionType;
    private JTextField txtDivisionBudget;
    private JButton btnAddDivision;

    // Committee Tab
    private JTextField txtCommitteeId;
    private JTextField txtCommitteeName;
    private JTextField txtMaxCapacity;
    private JButton btnAddCommittee;

    // Execution Tab
    private JButton btnExecute;
    private JButton btnReport;

    private JComboBox<String> cmbStatusFilter;
    private JPanel panelDetailEvent;
    private JLabel lblDetailNameAndBudget;
    private JLabel lblDetailDates;
    private JLabel lblDetailTimes;
    private JLabel lblDetailStatus;

    private DefaultTableModel taskTableModel;
    private DefaultTableModel divisionTableModel;
    private DefaultTableModel committeeTableModel;
    private JTable taskTable;
    private JTable divisionTable;
    private JTable committeeTable;

    private JComboBox<Division> cmbExecuteDivision;
    private JComboBox<Task> cmbExecuteTask;
    private JComboBox<Committee> cmbExecuteCommittee;

    private JComboBox<Division> cmbTaskDivision;
    private JComboBox<Committee> cmbTaskCommittee;

    private JTextArea txtOutput;

    private JLabel lblEventCount;
    private JLabel lblTaskCount;
    private JLabel lblDivisionCount;
    private JLabel lblCommitteeCount;

    public MainFrame() {
        // Initialize DatabaseConnection first
        eventplanner.database.DatabaseConnection.getInstance();

        // Initialize the event count label
        lblEventCount = new JLabel("0");

        // Initialize the event list model first so it is ready
        eventListModel = new DefaultListModel<>();
        lstEvents = new JList<>(eventListModel);

        configureLook();
        configureWindow();

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(BACKGROUND);
        rootPanel.add(createSidebar(), BorderLayout.WEST);
        rootPanel.add(createMainContent(), BorderLayout.CENTER);

        setContentPane(rootPanel);

        // Load initial events and trigger selection
        refreshEventList("All");
    }

    private void configureLook() {
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND);
        UIManager.put("TabbedPane.focus", BACKGROUND);
        UIManager.put("Table.showGrid", false);
    }

    private void configureWindow() {
        setTitle("Sistem Manajemen Event Kampus");
        setSize(1120, 720);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(24, 20, 24, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel appName = new JLabel("Sistem Manajemen");
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        sidebar.add(appName, gbc);

        JLabel appSubtitle = new JLabel("Event Kampus");
        appSubtitle.setForeground(new Color(186, 230, 253));
        appSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        sidebar.add(appSubtitle, gbc);

        JLabel lblEventListTitle = new JLabel("DAFTAR EVENT KAMPUS");
        lblEventListTitle.setForeground(new Color(186, 230, 253));
        lblEventListTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 6, 0);
        sidebar.add(lblEventListTitle, gbc);

        // Panel Total Event (relocated to left panel)
        JPanel pnlTotalEvent = new JPanel(new BorderLayout(8, 0));
        pnlTotalEvent.setBackground(new Color(39, 86, 145));
        pnlTotalEvent.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel lblTotalEventTitle = new JLabel("Total Event:");
        lblTotalEventTitle.setForeground(new Color(219, 234, 254));
        lblTotalEventTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lblEventCount.setForeground(Color.WHITE);
        lblEventCount.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblEventCount.setHorizontalAlignment(SwingConstants.RIGHT);

        pnlTotalEvent.add(lblTotalEventTitle, BorderLayout.WEST);
        pnlTotalEvent.add(lblEventCount, BorderLayout.EAST);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 12, 0);
        sidebar.add(pnlTotalEvent, gbc);

        JLabel lblFilterStatus = new JLabel("FILTER STATUS");
        lblFilterStatus.setForeground(new Color(186, 230, 253));
        lblFilterStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 6, 0);
        sidebar.add(lblFilterStatus, gbc);

        cmbStatusFilter = new JComboBox<>(new String[]{"All", "Preparation", "On-going", "Finished"});
        styleComboBox(cmbStatusFilter);
        cmbStatusFilter.setBackground(new Color(39, 86, 145));
        cmbStatusFilter.setForeground(Color.WHITE);
        cmbStatusFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(PRIMARY);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(new Color(39, 86, 145));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        cmbStatusFilter.addActionListener(e -> {
            String selectedStatus = (String) cmbStatusFilter.getSelectedItem();
            if (selectedStatus != null) {
                refreshEventList(selectedStatus);
            }
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 14, 0);
        sidebar.add(cmbStatusFilter, gbc);

        lstEvents.setBackground(new Color(39, 86, 145));
        lstEvents.setForeground(Color.WHITE);
        lstEvents.setSelectionBackground(PRIMARY);
        lstEvents.setSelectionForeground(Color.WHITE);
        lstEvents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Listener for JList selection
        lstEvents.addListSelectionListener(selectionEvent -> {
            if (!selectionEvent.getValueIsAdjusting() && !isUpdatingDropdown) {
                int index = lstEvents.getSelectedIndex();
                if (index >= 0 && index < daftarEventTampil.size()) {
                    updateEventSelection(daftarEventTampil.get(index));
                } else {
                    updateEventSelection(null);
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(lstEvents);
        listScrollPane.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246)));
        listScrollPane.getViewport().setBackground(new Color(39, 86, 145));
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 16, 0);
        sidebar.add(listScrollPane, gbc);

        JPanel eventActionPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        eventActionPanel.setBackground(PRIMARY_DARK);

        JButton btnEditEvent = new JButton("Edit Nama");
        JButton btnDeleteEvent = new JButton("Hapus Event");

        btnEditEvent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEditEvent.setForeground(Color.WHITE);
        btnEditEvent.setBackground(new Color(39, 86, 145));
        btnEditEvent.setFocusPainted(false);
        btnEditEvent.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        btnDeleteEvent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeleteEvent.setForeground(Color.WHITE);
        btnDeleteEvent.setBackground(new Color(220, 38, 38));
        btnDeleteEvent.setFocusPainted(false);
        btnDeleteEvent.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        btnEditEvent.addActionListener(e -> {
            if (eventAktif == null) {
                showWarning("Pilih event yang ingin diubah terlebih dahulu!");
                return;
            }
            String input = JOptionPane.showInputDialog(this, 
                "Masukkan nama baru untuk event '" + eventAktif.getEventName() + "':", 
                "Edit Nama Event", 
                JOptionPane.QUESTION_MESSAGE);
            if (input == null) return;
            String newName = input.trim();
            if (newName.isEmpty()) {
                showWarning("Nama event tidak boleh kosong!");
                return;
            }
            
            // Execute SQL update
            String sql = "UPDATE tabel_event SET nama_event = ? WHERE id_event = ?";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newName);
                pstmt.setString(2, eventAktif.getEventId());
                pstmt.executeUpdate();
                
                // Update local memory and refresh
                eventAktif.setEventName(newName);
                String selectedStatus = cmbStatusFilter != null ? (String) cmbStatusFilter.getSelectedItem() : "All";
                refreshEventList(selectedStatus != null ? selectedStatus : "All");
                showInfo("Nama event berhasil diperbarui!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showWarning("Gagal memperbarui nama event di database.");
            }
        });

        btnDeleteEvent.addActionListener(e -> {
            if (eventAktif == null) {
                showWarning("Pilih event yang ingin dihapus terlebih dahulu!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus event '" + eventAktif.getEventName() + "'?\nSemua divisi, panitia, dan tugas yang terkait akan ikut terhapus.", 
                "Konfirmasi Hapus Event", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                // Execute SQL delete
                String sql = "DELETE FROM tabel_event WHERE id_event = ?";
                try (Connection conn = DatabaseConnection.getInstance().getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, eventAktif.getEventId());
                    pstmt.executeUpdate();
                    
                    // Clear selected event, reset right panel, and refresh list
                    updateEventSelection(null);
                    String selectedStatus = cmbStatusFilter != null ? (String) cmbStatusFilter.getSelectedItem() : "All";
                    refreshEventList(selectedStatus != null ? selectedStatus : "All");
                    showInfo("Event berhasil dihapus!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showWarning("Gagal menghapus event dari database.");
                }
            }
        });

        eventActionPanel.add(btnEditEvent);
        eventActionPanel.add(btnDeleteEvent);

        gbc.gridy = 7;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 16, 0);
        sidebar.add(eventActionPanel, gbc);

        JLabel footer = new JLabel("<html>Gunakan menu navigasi untuk memilih Event aktif.</html>");
        footer.setForeground(new Color(203, 213, 225));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridy = 8;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        sidebar.add(footer, gbc);

        return sidebar;
    }

    private JLabel createSummaryLabel() {
        JLabel label = new JLabel("0");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 25));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JPanel createSummaryPanel(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(new Color(39, 86, 145));
        panel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(219, 234, 254));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createMetricsBar() {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 12);

        lblTaskCount = createSummaryLabel();
        lblDivisionCount = createSummaryLabel();
        lblCommitteeCount = createSummaryLabel();

        // Style value labels to have primary color since they are on a white surface card background
        lblTaskCount.setForeground(PRIMARY);
        lblDivisionCount.setForeground(PRIMARY);
        lblCommitteeCount.setForeground(PRIMARY);

        gbc.gridx = 0;
        bar.add(createHorizontalSummaryPanel("Total Tugas", lblTaskCount), gbc);
        gbc.gridx = 1;
        bar.add(createHorizontalSummaryPanel("Total Divisi", lblDivisionCount), gbc);
        gbc.gridx = 2;
        bar.add(createHorizontalSummaryPanel("Total Panitia", lblCommitteeCount), gbc);

        return bar;
    }

    private JPanel createHorizontalSummaryPanel(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(24, 26, 24, 26));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Sistem Manajemen Event Kampus");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Kelola penugasan operasional, divisi departemen, dan kapasitas beban kerja panitia secara terintegrasi.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel titleStack = new JPanel(new BorderLayout(0, 4));
        titleStack.setOpaque(false);
        titleStack.add(title, BorderLayout.NORTH);
        titleStack.add(subtitle, BorderLayout.SOUTH);
        headerPanel.add(titleStack, BorderLayout.WEST);

        JPanel topContainer = new JPanel(new BorderLayout(0, 16));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(createMetricsBar(), BorderLayout.SOUTH);

        panel.add(topContainer, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BACKGROUND);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.addTab("Event", createEventPanel());
        tabbedPane.addTab("Penugasan Operasional", createTaskPanel());
        tabbedPane.addTab("Alokasi Dana Divisi", createDivisionPanel());
        tabbedPane.addTab("Panitia", createCommitteePanel());
        tabbedPane.addTab("Eksekusi", createExecutionPanel());

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEventPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Inisialisasi Event Baru");

        txtNewEventName = createTextField();
        txtNewEventBudget = createTextField();
        txtTanggalMulai = createTextField();
        txtTanggalSelesai = createTextField();
        txtWaktuMulai = createTextField();
        txtWaktuSelesai = createTextField();
        btnCreateEvent = createPrimaryButton("Registrasi Event");

        addFormRow(formPanel, 0, "Nama Event", txtNewEventName);
        addFormRow(formPanel, 1, "Alokasi Anggaran Utama (Rp)", txtNewEventBudget);
        addFormRow(formPanel, 2, "Tanggal Mulai (YYYY-MM-DD)", txtTanggalMulai);
        addFormRow(formPanel, 3, "Tanggal Selesai (YYYY-MM-DD)", txtTanggalSelesai);
        addFormRow(formPanel, 4, "Waktu Mulai (HH:MM)", txtWaktuMulai);
        addFormRow(formPanel, 5, "Waktu Selesai (HH:MM)", txtWaktuSelesai);
        addFormButton(formPanel, 6, btnCreateEvent);

        btnCreateEvent.addActionListener(e -> {
            String name = txtNewEventName.getText().trim();
            String budgetStr = txtNewEventBudget.getText().trim();
            String tglMulai = txtTanggalMulai.getText().trim();
            String tglSelesai = txtTanggalSelesai.getText().trim();
            String wktMulai = txtWaktuMulai.getText().trim();
            String wktSelesai = txtWaktuSelesai.getText().trim();

            if (name.isEmpty() || budgetStr.isEmpty() || tglMulai.isEmpty() || tglSelesai.isEmpty() || wktMulai.isEmpty() || wktSelesai.isEmpty()) {
                showWarning("Semua field event wajib diisi!");
                return;
            }

            try {
                double budget = Double.parseDouble(budgetStr);
                if (budget < 0) {
                    showWarning("Alokasi Anggaran Utama tidak boleh bernilai negatif!");
                    return;
                }

                Event newEvent = new Event(name, budget);
                newEvent.setTanggalMulai(tglMulai);
                newEvent.setTanggalSelesai(tglSelesai);
                newEvent.setWaktuMulai(wktMulai);
                newEvent.setWaktuSelesai(wktSelesai);

                // Save to database
                eventDAO.insertEvent(newEvent);

                // Reload list of events from database
                daftarEventGlobal.clear();
                daftarEventGlobal.addAll(eventDAO.getAllEvents());

                // Refresh according to selected status filter
                String selectedStatus = cmbStatusFilter != null ? (String) cmbStatusFilter.getSelectedItem() : "All";
                refreshEventList(selectedStatus != null ? selectedStatus : "All");

                // Reset fields
                txtNewEventName.setText("");
                txtNewEventBudget.setText("");
                txtTanggalMulai.setText("");
                txtTanggalSelesai.setText("");
                txtWaktuMulai.setText("");
                txtWaktuSelesai.setText("");

                // Find and select the new event in the JList
                int selectIdx = -1;
                for (int i = 0; i < daftarEventTampil.size(); i++) {
                    if (daftarEventTampil.get(i).getEventId().equals(newEvent.getEventId())) {
                        selectIdx = i;
                        break;
                    }
                }
                if (selectIdx != -1) {
                    lstEvents.setSelectedIndex(selectIdx);
                } else {
                    lstEvents.clearSelection();
                    updateEventSelection(null);
                }

                showInfo("Event baru '" + name + "' berhasil diregistrasi!");
            } catch (NumberFormatException ex) {
                showWarning("Alokasi Anggaran Utama harus berupa angka!");
            }
        });

        // Build Dashboard Card panelDetailEvent
        panelDetailEvent = new JPanel(new GridBagLayout());
        panelDetailEvent.setBackground(SURFACE);
        panelDetailEvent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(30, 40, 30, 40)
        ));

        lblDetailNameAndBudget = new JLabel("Pilih Event untuk Melihat Rincian");
        lblDetailNameAndBudget.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDetailNameAndBudget.setForeground(PRIMARY);

        lblDetailDates = new JLabel("Tanggal: -");
        lblDetailDates.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblDetailDates.setForeground(TEXT);

        lblDetailTimes = new JLabel("Waktu: -");
        lblDetailTimes.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDetailTimes.setForeground(MUTED);

        lblDetailStatus = new JLabel("Status: -");
        lblDetailStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetailStatus.setForeground(MUTED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        panelDetailEvent.add(lblDetailNameAndBudget, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        panelDetailEvent.add(lblDetailDates, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 14, 0);
        panelDetailEvent.add(lblDetailTimes, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        panelDetailEvent.add(lblDetailStatus, gbc);

        Dimension preferredSize = formPanel.getLayout().preferredLayoutSize(formPanel);
        formPanel.setPreferredSize(new Dimension(315, preferredSize.height));
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.WEST);

        JPanel detailContainer = new JPanel(new BorderLayout(0, 14));
        detailContainer.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel("Rincian Event Terpilih");
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        detailContainer.add(titleLabel, BorderLayout.NORTH);
        detailContainer.add(panelDetailEvent, BorderLayout.CENTER);

        panel.add(detailContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTaskPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Penugasan Operasional");

        txtTaskName = createTextField();
        txtDifficulty = createTextField();
        txtTaskCost = createTextField();
        
        cmbTaskDivision = new JComboBox<>();
        cmbTaskCommittee = new JComboBox<>();
        styleComboBox(cmbTaskDivision);
        styleComboBox(cmbTaskCommittee);
        
        btnAddTask = createPrimaryButton("Tambah Penugasan");

        addFormRow(formPanel, 0, "Nama Tugas", txtTaskName);
        addFormRow(formPanel, 1, "Pilih Divisi", cmbTaskDivision);
        addFormRow(formPanel, 2, "Kapasitas Beban Kerja", txtDifficulty);
        addFormRow(formPanel, 3, "Biaya Operasional (Rp)", txtTaskCost);
        addFormButton(formPanel, 4, btnAddTask);

        taskTableModel = createTableModel(new Object[]{"No.", "Nama Tugas", "Divisi", "Kapasitas Beban", "Biaya"});
        taskTable = createTable(taskTableModel);
        taskTable.setAutoCreateRowSorter(true);

        btnAddTask.addActionListener(e -> {
            if (eventAktif == null) return;
            try {
                String name = txtTaskName.getText().trim();
                Division division = (Division) cmbTaskDivision.getSelectedItem();
                int difficulty = Integer.parseInt(txtDifficulty.getText().trim());
                double cost = Double.parseDouble(txtTaskCost.getText().trim());
                
                if (name.isEmpty()) {
                    showWarning("Nama tugas wajib diisi!");
                    return;
                }
                if (division == null) {
                    showWarning("Pilih divisi untuk tugas ini!");
                    return;
                }

                // Assert biaya >= 0
                assert cost >= 0 : "Biaya operasional tidak boleh negatif";

                Task task = new Task(name, difficulty, cost);
                task.setIdDivisi(division.getDivisionId());

                // Simpan Tugas ke Database
                tugasDAO.insertTugas(task, eventAktif.getEventId());
                
                // Refresh data dari database
                updateEventSelection(eventAktif);

                clearFields(txtTaskName, txtDifficulty, txtTaskCost);
                showInfo("Tugas operasional berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                showWarning("Kapasitas beban kerja dan biaya operasional harus berupa angka!");
            } catch (AssertionError ex) {
                showWarning("Kesalahan Validasi: " + ex.getMessage());
            }
        });

        JButton btnEditTask = createPrimaryButton("Edit");
        JButton btnDeleteTask = createSecondaryButton("Hapus Tugas");
        btnEditTask.setPreferredSize(new Dimension(100, 36));
        btnDeleteTask.setPreferredSize(new Dimension(100, 36));

        btnDeleteTask.addActionListener(evt -> {
            if (eventAktif == null) return;
            int selectedViewRow = taskTable.getSelectedRow();
            if (selectedViewRow == -1) {
                showWarning("Pilih penugasan yang ingin dihapus terlebih dahulu!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus penugasan ini?", "Konfirmasi Hapus Tugas", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = taskTable.convertRowIndexToModel(selectedViewRow);
                Task task = eventAktif.getDaftarTugas().get(modelRow);
                
                // Cek apakah tugas tersebut sedang diambil oleh panitia
                if (task.getIdPanitia() != null) {
                    String idPanitia = task.getIdPanitia();
                    Committee committee = panitiaDAO.getPanitiaById(idPanitia);
                    if (committee != null) {
                        // Kurangi current_workload-nya di tabel_panitia
                        committee.setCurrentWorkload(Math.max(0, committee.getCurrentWorkload() - task.getDifficulty()));
                        panitiaDAO.updatePanitia(committee);
                    }
                }
                
                // Jalankan query DELETE FROM tabel_tugas WHERE id_tugas = ?
                tugasDAO.deleteTugas(task.getTaskId());
                
                // Refresh data dari database (refresh tabel tugas, tabel panitia, dan dropdown eksekusi)
                updateEventSelection(eventAktif);
                showInfo("Penugasan berhasil dihapus!");
            }
        });

        btnEditTask.addActionListener(evt -> {
            if (eventAktif == null) return;
            int selectedViewRow = taskTable.getSelectedRow();
            if (selectedViewRow == -1) {
                showWarning("Pilih penugasan yang ingin diubah terlebih dahulu!");
                return;
            }
            int modelRow = taskTable.convertRowIndexToModel(selectedViewRow);
            Task task = eventAktif.getDaftarTugas().get(modelRow);

            JTextField editNameField = new JTextField(task.getTaskName());
            JComboBox<Division> editDivisionCmb = new JComboBox<>();
            for (Division div : eventAktif.getDaftarDivisi()) {
                editDivisionCmb.addItem(div);
                if (div.getDivisionId().equals(task.getIdDivisi())) {
                    editDivisionCmb.setSelectedItem(div);
                }
            }
            editDivisionCmb.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Division) {
                        setText(((Division) value).getDivisionName());
                    }
                    return this;
                }
            });
            
            JTextField editDifficultyField = new JTextField(String.valueOf(task.getDifficulty()));
            JTextField editCostField = new JTextField(String.valueOf(task.getTaskCost()));

            Object[] message = {
                "Nama Tugas:", editNameField,
                "Divisi:", editDivisionCmb,
                "Kapasitas Beban Kerja:", editDifficultyField,
                "Biaya Operasional (Rp):", editCostField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ubah Penugasan", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String newName = editNameField.getText().trim();
                    Division newDiv = (Division) editDivisionCmb.getSelectedItem();
                    int newDifficulty = Integer.parseInt(editDifficultyField.getText().trim());
                    double newCost = Double.parseDouble(editCostField.getText().trim());

                    if (newName.isEmpty()) {
                        showWarning("Nama tugas tidak boleh kosong!");
                        return;
                    }
                    if (newDiv == null) {
                        showWarning("Divisi wajib dipilih!");
                        return;
                    }

                    // Assert biaya >= 0
                    assert newCost >= 0 : "Biaya operasional tidak boleh negatif";

                    task.setTaskName(newName);
                    task.setIdDivisi(newDiv.getDivisionId());
                    task.setDifficulty(newDifficulty);
                    task.setTaskCost(newCost);

                    // Update database
                    tugasDAO.updateTugas(task);

                    // Refresh data dari database
                    updateEventSelection(eventAktif);
                    showInfo("Penugasan berhasil diubah!");
                } catch (NumberFormatException ex) {
                    showWarning("Kapasitas beban kerja dan biaya operasional harus berupa angka!");
                } catch (AssertionError ex) {
                    showWarning("Kesalahan Validasi: " + ex.getMessage());
                }
            }
        });

        Dimension preferredSize = formPanel.getLayout().preferredLayoutSize(formPanel);
        formPanel.setPreferredSize(new Dimension(315, preferredSize.height));

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Penugasan Operasional", taskTable, btnEditTask, btnDeleteTask), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDivisionPanel() {
        JPanel panel = createTabPanel();

        divisionTableModel = createTableModel(new Object[]{"ID Divisi", "No", "Nama Divisi", "Anggaran Awal", "Sisa Anggaran"});
        divisionTable = createTable(divisionTableModel);
        divisionTable.getColumnModel().getColumn(0).setMinWidth(0);
        divisionTable.getColumnModel().getColumn(0).setMaxWidth(0);
        divisionTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        divisionTable.setAutoCreateRowSorter(true);

        JButton btnEditDana = createPrimaryButton("Edit Dana");
        btnEditDana.setPreferredSize(new Dimension(120, 36));

        btnEditDana.addActionListener(evt -> {
            if (eventAktif == null) return;
            if (divisionTable.getSelectedRow() == -1) {
                showWarning("Pilih divisi yang ingin diubah terlebih dahulu!");
                return;
            }
            int rowTerpilih = divisionTable.convertRowIndexToModel(divisionTable.getSelectedRow());
            String idDivisi = (String) divisionTableModel.getValueAt(rowTerpilih, 0);
            
            double currentBudget = divisionDAO.getBudgetById(idDivisi);
            Division division = divisionDAO.getDivisionById(idDivisi);
            if (division == null) {
                showWarning("Divisi tidak ditemukan di database!");
                return;
            }

            String input = JOptionPane.showInputDialog(this, 
                "Masukkan anggaran baru untuk " + division.getDivisionName() + ":", 
                "Edit Dana Divisi", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (input == null) return; // User cancelled
            
            try {
                double newBudget = Double.parseDouble(input.trim());
                if (newBudget < 0) {
                    showWarning("Anggaran divisi tidak boleh negatif!");
                    return;
                }
                
                boolean success = divisionDAO.updateDanaDivisi(division.getDivisionId(), newBudget);
                if (success) {
                    updateEventSelection(eventAktif);
                    showInfo("Dana divisi berhasil diubah!");
                } else {
                    showWarning("Gagal mengubah dana divisi di database.");
                }
            } catch (NumberFormatException ex) {
                showWarning("Anggaran harus berupa angka!");
            }
        });

        panel.add(createTablePanel("Alokasi Dana Divisi", divisionTable, btnEditDana, null), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCommitteePanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Registrasi Panitia");

        txtCommitteeName = createTextField();
        txtMaxCapacity = createTextField();
        btnAddCommittee = createPrimaryButton("Registrasi Panitia");

        addFormRow(formPanel, 0, "Nama Panitia", txtCommitteeName);
        addFormRow(formPanel, 1, "Kapasitas Beban Kerja", txtMaxCapacity);
        addFormButton(formPanel, 2, btnAddCommittee);

        committeeTableModel = createTableModel(new Object[]{"No.", "Nama Panitia", "Kapasitas Beban"});
        committeeTable = createTable(committeeTableModel);
        committeeTable.setAutoCreateRowSorter(true);

        btnAddCommittee.addActionListener(e -> {
            if (eventAktif == null) return;
            try {
                String name = txtCommitteeName.getText().trim();
                int maxCapacity = Integer.parseInt(txtMaxCapacity.getText().trim());

                if (name.isEmpty()) {
                    showWarning("Nama Panitia wajib diisi!");
                    return;
                }

                Committee committee = new Committee(name, maxCapacity);
                
                // Simpan panitia ke Database
                panitiaDAO.insertPanitia(committee, eventAktif.getEventId());

                // Refresh data dari database
                updateEventSelection(eventAktif);

                clearFields(txtCommitteeName, txtMaxCapacity);
                showInfo("Panitia berhasil diregistrasi!");
            } catch (NumberFormatException ex) {
                showWarning("Kapasitas beban kerja harus berupa angka!");
            }
        });

        JButton btnEditCommittee = createPrimaryButton("Edit");
        JButton btnDeleteCommittee = createSecondaryButton("Hapus");
        btnEditCommittee.setPreferredSize(new Dimension(100, 36));
        btnDeleteCommittee.setPreferredSize(new Dimension(100, 36));

        btnDeleteCommittee.addActionListener(evt -> {
            if (eventAktif == null) return;
            int selectedViewRow = committeeTable.getSelectedRow();
            if (selectedViewRow == -1) {
                showWarning("Pilih panitia yang ingin dihapus terlebih dahulu!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus panitia ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = committeeTable.convertRowIndexToModel(selectedViewRow);
                Committee committee = eventAktif.getDaftarPanitia().get(modelRow);
                
                // Hapus panitia dari Database
                panitiaDAO.deletePanitia(committee.getCommitteeId());

                // Refresh data dari database
                updateEventSelection(eventAktif);
                showInfo("Panitia berhasil dihapus!");
            }
        });

        btnEditCommittee.addActionListener(evt -> {
            if (eventAktif == null) return;
            int selectedViewRow = committeeTable.getSelectedRow();
            if (selectedViewRow == -1) {
                showWarning("Pilih panitia yang ingin diubah terlebih dahulu!");
                return;
            }
            int modelRow = committeeTable.convertRowIndexToModel(selectedViewRow);
            Committee committee = eventAktif.getDaftarPanitia().get(modelRow);

            JTextField editNameField = new JTextField(committee.getName());
            JTextField editCapacityField = new JTextField(String.valueOf(committee.getMaxCapacity()));

            Object[] message = {
                "Nama Panitia:", editNameField,
                "Kapasitas Beban Kerja:", editCapacityField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ubah Panitia", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String newName = editNameField.getText().trim();
                    int newCapacity = Integer.parseInt(editCapacityField.getText().trim());

                    if (newName.isEmpty()) {
                        showWarning("Nama panitia tidak boleh kosong!");
                        return;
                    }

                    if (newCapacity < committee.getCurrentWorkload()) {
                        showWarning("Kapasitas baru tidak boleh kurang dari beban kerja saat ini (" + committee.getCurrentWorkload() + ")!");
                        return;
                    }

                    committee.setName(newName);
                    committee.setMaxCapacity(newCapacity);

                    // Update panitia di Database
                    panitiaDAO.updatePanitia(committee);

                    // Refresh data dari database
                    updateEventSelection(eventAktif);
                    showInfo("Panitia berhasil diubah!");
                } catch (NumberFormatException ex) {
                    showWarning("Kapasitas beban kerja harus berupa angka!");
                }
            }
        });

        JButton btnManageTasks = createPrimaryButton("Lihat Tugas");
        btnManageTasks.setPreferredSize(new Dimension(160, 36));

        btnManageTasks.addActionListener(evt -> {
            if (eventAktif == null) return;
            int selectedViewRow = committeeTable.getSelectedRow();
            if (selectedViewRow == -1) {
                showWarning("Pilih panitia terlebih dahulu!");
                return;
            }
            int modelRow = committeeTable.convertRowIndexToModel(selectedViewRow);
            Committee committee = eventAktif.getDaftarPanitia().get(modelRow);
            showManageTasksDialog(committee);
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Panitia Terdaftar", committeeTable, btnEditCommittee, btnDeleteCommittee, btnManageTasks), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createExecutionPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Konsol Eksekusi Tugas");

        cmbExecuteDivision = new JComboBox<>();
        cmbExecuteTask = new JComboBox<>();
        cmbExecuteCommittee = new JComboBox<>();
        styleComboBox(cmbExecuteDivision);
        styleComboBox(cmbExecuteTask);
        styleComboBox(cmbExecuteCommittee);

        btnExecute = createPrimaryButton("Eksekusi Tugas");
        btnReport = createSecondaryButton("Buat Laporan Event");

        addFormRow(formPanel, 0, "Pilih Tugas", cmbExecuteTask);
        addFormRow(formPanel, 1, "Pilih Panitia", cmbExecuteCommittee);
        addFormButton(formPanel, 2, btnExecute);
        addFormButton(formPanel, 3, btnReport);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        txtOutput.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtOutput.setForeground(TEXT);
        txtOutput.setBackground(new Color(248, 250, 252));
        txtOutput.setBorder(new EmptyBorder(18, 18, 18, 18));
        txtOutput.setText("Log aktivitas sistem dan laporan acara akan ditampilkan di sini.");

        btnExecute.addActionListener(e -> executeTask());
        btnReport.addActionListener(e -> showEventReport());

        // Reactive Selection Listeners for Task and Committee using ItemListener
        // No dynamic filtering on dropdown selection. Showing all unassigned tasks and active committee.

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createOutputPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(18, 0, 0, 0));
        return panel;
    }

    private JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SURFACE);
        panel.setPreferredSize(new Dimension(315, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        panel.add(titleLabel, gbc);

        return panel;
    }

    private void addFormRow(JPanel panel, int row, String labelText, Component input) {
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        int baseRow = row * 2 + 1;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = baseRow;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(label, gbc);

        gbc.gridy = baseRow + 1;
        gbc.insets = new Insets(0, 0, 14, 0);
        panel.add(input, gbc);
    }

    private void addFormButton(JPanel panel, int row, JButton button) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row * 2 + 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        panel.add(button, gbc);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setForeground(TEXT);
        textField.setPreferredSize(new Dimension(0, 38));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return textField;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = createBaseButton(text);
        button.setForeground(PRIMARY_DARK);
        button.setBackground(new Color(226, 242, 241));
        return button;
    }

    private JButton createBaseButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 40));
        return button;
    }

    private DefaultTableModel createTableModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setRowHeight(34);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(236, 240, 245));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(Color.WHITE);
        header.setBackground(PRIMARY);
        header.setPreferredSize(new Dimension(0, 38));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        table.setDefaultRenderer(Object.class, renderer);

        return table;
    }

    private JPanel createTablePanel(String title, JTable table, JButton... buttons) {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(createScrollPane(table), BorderLayout.CENTER);

        if (buttons != null && buttons.length > 0) {
            JPanel btnPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
            btnPanel.setOpaque(false);
            boolean hasButtons = false;
            for (JButton btn : buttons) {
                if (btn != null) {
                    btnPanel.add(btn);
                    hasButtons = true;
                }
            }
            if (hasButtons) {
                panel.add(btnPanel, BorderLayout.SOUTH);
            }
        }

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Konsol Eksekusi Sistem & Laporan Event");
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(createScrollPane(txtOutput), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setForeground(TEXT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(0, 38));
    }

    private Division createDivision(String type, double budget) {
        if (type.contains("Acara") || type.contains("Operations")) {
            return new AcaraDivision(budget);
        }
        if (type.contains("Konsumsi") || type.contains("Procurement")) {
            return new KonsumsiDivision(budget);
        }
        return new LogisticDivision(budget);
    }

    private void executeTask() {
        if (eventAktif == null) return;
        Task task = (Task) cmbExecuteTask.getSelectedItem();
        Committee committee = (Committee) cmbExecuteCommittee.getSelectedItem();

        if (task == null || committee == null) {
            showWarning("Tugas dan panitia pelaksana wajib dipilih!");
            return;
        }

        Division division = null;
        if (task.getIdDivisi() != null) {
            division = divisionDAO.getDivisionById(task.getIdDivisi());
        }

        if (division == null) {
            showWarning("Tugas ini tidak memiliki divisi yang terasosiasi!");
            return;
        }

        // Pindahkan Validasi Beban Kerja ke Tombol 'Eksekusi'
        if (committee.getCurrentWorkload() + task.getDifficulty() > committee.getMaxCapacity()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Gagal: Kapasitas beban kerja panitia tidak mencukupi untuk mengambil tugas ini!", 
                "Validasi Gagal", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            division.eksekusiTugas(task, committee);

            // Update database records first
            tugasDAO.assignPanitiaToTugas(task.getTaskId(), committee.getCommitteeId());
            panitiaDAO.updatePanitia(committee);

            txtOutput.setText(
                    "EKSEKUSI TUGAS OPERASIONAL BERHASIL\n\n"
                            + "Divisi      : " + division.getDivisionName() + "\n"
                            + "Tugas       : " + task.getTaskName() + "\n"
                            + "Panitia     : " + committee.getName() + "\n\n"
                            + "Log Metrik Divisi:\n"
                            + division.buatLaporan() + "\n\n"
                            + "Status Pemanfaatan Sumber Daya:\n"
                            + "Beban Kerja Saat Ini: " + committee.getCurrentWorkload()
                            + " / " + committee.getMaxCapacity()
            );

            // Reload DB state
            updateEventSelection(eventAktif);
            showInfo("Tugas berhasil dieksekusi!");
        } catch (OverloadException ex) {
            showWarning("Peringatan Sistem: Beban kerja panitia melebihi batas maksimal.");
            txtOutput.setText(
                    "KEGAGALAN EKSEKUSI TUGAS\n\n"
                            + "Penyebab: Peringatan Sistem: Beban kerja panitia melebihi batas maksimal."
            );
        } catch (OverBudgetException ex) {
            showWarning("Peringatan Sistem: Alokasi anggaran tidak mencukupi.");
            txtOutput.setText(
                    "KEGAGALAN EKSEKUSI TUGAS\n\n"
                            + "Penyebab: Peringatan Sistem: Alokasi anggaran tidak mencukupi."
            );
        } catch (RuntimeException ex) {
            showWarning(ex.getMessage());
            txtOutput.setText(
                    "KEGAGALAN EKSEKUSI TUGAS\n\n"
                            + "Penyebab: " + ex.getMessage()
            );
        }
    }

    private void showEventReport() {
        if (eventAktif != null) {
            txtOutput.setText(eventAktif.buatLaporanAcara());
        } else {
            txtOutput.setText("Sistem: Belum ada event aktif yang dipilih.");
        }
    }

    private void refreshEventTable() {
        if (eventAktif != null) {
            // hitung sisa anggaran secara dinamis dari database
            double totalBudget = eventAktif.getTotalBudget();
            double totalAllocated = 0;
            String sql = "SELECT SUM(allocated_budget) FROM tabel_divisi WHERE id_event = ?";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, eventAktif.getEventId());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        totalAllocated = rs.getDouble(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            double sisaAnggaran = totalBudget - totalAllocated;

            String nameAndBudget = "<html>" + eventAktif.getEventName() + " - Sisa Anggaran: " + formatMoney(sisaAnggaran) + "</html>";
            lblDetailNameAndBudget.setText(nameAndBudget);
            lblDetailNameAndBudget.setForeground(PRIMARY);
            
            String tglMulai = eventAktif.getTanggalMulai() != null ? eventAktif.getTanggalMulai() : "-";
            String tglSelesai = eventAktif.getTanggalSelesai() != null ? eventAktif.getTanggalSelesai() : "-";
            lblDetailDates.setText("Tanggal: " + tglMulai + " s/d " + tglSelesai);
            
            String wktMulai = eventAktif.getWaktuMulai() != null ? eventAktif.getWaktuMulai() : "-";
            String wktSelesai = eventAktif.getWaktuSelesai() != null ? eventAktif.getWaktuSelesai() : "-";
            lblDetailTimes.setText("Waktu: " + wktMulai + " - " + wktSelesai);
            
            String status = eventAktif.getStatus();
            lblDetailStatus.setText("Status: " + status);
            
            if ("On-going".equalsIgnoreCase(status)) {
                lblDetailStatus.setForeground(new Color(46, 125, 50));
            } else if ("Preparation".equalsIgnoreCase(status)) {
                lblDetailStatus.setForeground(new Color(230, 81, 0));
            } else if ("Finished".equalsIgnoreCase(status)) {
                lblDetailStatus.setForeground(MUTED);
            } else {
                lblDetailStatus.setForeground(TEXT);
            }
        } else {
            lblDetailNameAndBudget.setText("Pilih Event untuk Melihat Rincian");
            lblDetailNameAndBudget.setForeground(MUTED);
            lblDetailDates.setText("Tanggal: -");
            lblDetailTimes.setText("Waktu: -");
            lblDetailStatus.setText("Status: -");
            lblDetailStatus.setForeground(MUTED);
        }
    }

    private void refreshEventList(String statusFilter) {
        isUpdatingDropdown = true;
        daftarEventGlobal.clear();
        daftarEventGlobal.addAll(eventDAO.getAllEvents());

        eventListModel.clear();
        daftarEventTampil.clear();
        for (Event ev : daftarEventGlobal) {
            if ("All".equals(statusFilter) || statusFilter.equalsIgnoreCase(ev.getStatus())) {
                daftarEventTampil.add(ev);
                eventListModel.addElement(ev.getEventName());
            }
        }
        isUpdatingDropdown = false;

        if (!daftarEventTampil.isEmpty()) {
            lstEvents.setSelectedIndex(0);
        } else {
            lstEvents.clearSelection();
            updateEventSelection(null);
        }
        updateSummary();
    }

    private void refreshTaskTable() {
        if (taskTableModel != null) {
            taskTableModel.setRowCount(0);
        }
        isUpdatingDropdown = true;
        if (cmbExecuteTask != null) {
            cmbExecuteTask.removeAllItems();
        }
        if (eventAktif == null) {
            isUpdatingDropdown = false;
            return;
        }
        List<Task> listTugas = eventAktif.getDaftarTugas();
        for (int i = 0; i < listTugas.size(); i++) {
            Task task = listTugas.get(i);
            if (taskTableModel != null) {
                String divisionName = "Tidak Ada";
                if (task.getIdDivisi() != null) {
                    Division div = divisionDAO.getDivisionById(task.getIdDivisi());
                    if (div != null) {
                        divisionName = div.getDivisionName();
                    }
                }
                taskTableModel.addRow(new Object[]{
                        i + 1,
                        task.getTaskName(),
                        divisionName,
                        task.getDifficulty(),
                        formatMoney(task.getTaskCost())
                });
            }
        }
        // Sorting dropdown items alphabetically (A-Z) by task name
        List<Task> unassignedTasks = tugasDAO.getUnassignedTugasByEvent(eventAktif.getEventId());
        List<Task> sortedTasks = new ArrayList<>(unassignedTasks);
        sortedTasks.sort((t1, t2) -> t1.getTaskName().compareToIgnoreCase(t2.getTaskName()));
        for (Task task : sortedTasks) {
            if (cmbExecuteTask != null) {
                cmbExecuteTask.addItem(task);
            }
        }
        isUpdatingDropdown = false;
    }

    private void refreshDivisionTable() {
        if (divisionTableModel != null) {
            divisionTableModel.setRowCount(0);
        }
        if (cmbExecuteDivision != null) {
            cmbExecuteDivision.removeAllItems();
        }
        if (cmbTaskDivision != null) {
            cmbTaskDivision.removeAllItems();
        }
        if (eventAktif == null) return;
        List<Division> listDivisi = eventAktif.getDaftarDivisi();
        for (int i = 0; i < listDivisi.size(); i++) {
            Division division = listDivisi.get(i);
            double dbBudget = divisionDAO.getBudgetById(division.getDivisionId());
            if (dbBudget >= 0) {
                division.setAllocatedBudget(dbBudget);
            }
            double sisa = divisionDAO.getSisaAnggaranDivisi(division.getDivisionId());
            if (divisionTableModel != null) {
                divisionTableModel.addRow(new Object[]{
                        division.getDivisionId(),
                        i + 1,
                        division.getDivisionName(),
                        formatMoney(division.getAllocatedBudget()),
                        formatMoney(sisa)
                });
            }
            if (cmbExecuteDivision != null) {
                cmbExecuteDivision.addItem(division);
            }
            if (cmbTaskDivision != null) {
                cmbTaskDivision.addItem(division);
            }
        }
    }

    private void refreshCommitteeTable() {
        if (committeeTableModel != null) {
            committeeTableModel.setRowCount(0);
        }
        isUpdatingDropdown = true;
        if (cmbExecuteCommittee != null) {
            cmbExecuteCommittee.removeAllItems();
        }
        if (cmbTaskCommittee != null) {
            cmbTaskCommittee.removeAllItems();
        }
        if (eventAktif == null) {
            isUpdatingDropdown = false;
            return;
        }
        List<Committee> listPanitia = eventAktif.getDaftarPanitia();
        for (int i = 0; i < listPanitia.size(); i++) {
            Committee committee = listPanitia.get(i);
            if (committeeTableModel != null) {
                committeeTableModel.addRow(new Object[]{
                        i + 1,
                        committee.getName(),
                        committee.getMaxCapacity()
                });
            }
            if (cmbExecuteCommittee != null) {
                cmbExecuteCommittee.addItem(committee);
            }
            if (cmbTaskCommittee != null) {
                cmbTaskCommittee.addItem(committee);
            }
        }
        isUpdatingDropdown = false;
    }

    private void updateSummary() {
        if (lblEventCount != null) {
            lblEventCount.setText(String.valueOf(eventDAO.getEventCount()));
        }
        if (lblTaskCount != null) {
            lblTaskCount.setText(eventAktif != null ? String.valueOf(tugasDAO.getTugasByEvent(eventAktif.getEventId()).size()) : "0");
        }
        if (lblDivisionCount != null) {
            lblDivisionCount.setText(eventAktif != null ? String.valueOf(divisionDAO.getDivisionsByEvent(eventAktif.getEventId()).size()) : "0");
        }
        if (lblCommitteeCount != null) {
            lblCommitteeCount.setText(eventAktif != null ? String.valueOf(panitiaDAO.getPanitiaByEvent(eventAktif.getEventId()).size()) : "0");
        }
    }

    private String getDivisionDescription(Division division) {
        if (division instanceof AcaraDivision) {
            return "Validasi Beban Kerja Panitia";
        }
        if (division instanceof KonsumsiDivision) {
            return "Validasi Anggaran Konsumsi";
        }
        if (division instanceof LogisticDivision) {
            return "Validasi Anggaran Logistik";
        }
        return "Divisi Umum";
    }

    private String formatMoney(double value) {
        return String.format("Rp %,.0f", value);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Notifikasi Sistem", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Peringatan Sistem", JOptionPane.WARNING_MESSAGE);
    }

    private void setComboRenderer() {
        DefaultListCellRenderer divisionRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Division) {
                    Division division = (Division) value;
                    setText(division.getDivisionName());
                }

                return this;
            }
        };

        if (cmbExecuteDivision != null) {
            cmbExecuteDivision.setRenderer(divisionRenderer);
        }
        if (cmbTaskDivision != null) {
            cmbTaskDivision.setRenderer(divisionRenderer);
        }

        if (cmbExecuteTask != null) {
            cmbExecuteTask.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list,
                        Object value,
                        int index,
                        boolean isSelected,
                        boolean cellHasFocus
                ) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (value instanceof Task) {
                        Task task = (Task) value;
                        String divName = "";
                        if (task.getIdDivisi() != null) {
                            Division div = divisionDAO.getDivisionById(task.getIdDivisi());
                            if (div != null) {
                                divName = " [" + div.getDivisionName() + "]";
                            }
                        }
                        setText(task.getTaskName() + divName);
                    }

                    return this;
                }
            });
        }

        DefaultListCellRenderer committeeRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Committee) {
                    Committee committee = (Committee) value;
                    setText(committee.getName());
                }

                return this;
            }
        };

        if (cmbExecuteCommittee != null) {
            cmbExecuteCommittee.setRenderer(committeeRenderer);
        }
        if (cmbTaskCommittee != null) {
            cmbTaskCommittee.setRenderer(committeeRenderer);
        }
    }

    private void setRightPanelEnabled(boolean enabled) {
        // Event Tab
        if (txtEventName != null) txtEventName.setEnabled(enabled);
        if (txtEventBudget != null) txtEventBudget.setEnabled(enabled);
        if (btnAddEvent != null) btnAddEvent.setEnabled(enabled);

        // Task Tab
        if (txtTaskId != null) txtTaskId.setEnabled(enabled);
        if (txtTaskName != null) txtTaskName.setEnabled(enabled);
        if (txtDifficulty != null) txtDifficulty.setEnabled(enabled);
        if (txtTaskCost != null) txtTaskCost.setEnabled(enabled);
        if (cmbTaskDivision != null) cmbTaskDivision.setEnabled(enabled);
        if (cmbTaskCommittee != null) cmbTaskCommittee.setEnabled(enabled);
        if (btnAddTask != null) btnAddTask.setEnabled(enabled);

        // Division Tab
        if (cmbDivisionType != null) cmbDivisionType.setEnabled(enabled);
        if (txtDivisionBudget != null) txtDivisionBudget.setEnabled(enabled);
        if (btnAddDivision != null) btnAddDivision.setEnabled(enabled);

        // Committee Tab
        if (txtCommitteeId != null) txtCommitteeId.setEnabled(enabled);
        if (txtCommitteeName != null) txtCommitteeName.setEnabled(enabled);
        if (txtMaxCapacity != null) txtMaxCapacity.setEnabled(enabled);
        if (btnAddCommittee != null) btnAddCommittee.setEnabled(enabled);

        // Execution Tab
        if (cmbExecuteDivision != null) cmbExecuteDivision.setEnabled(enabled);
        if (cmbExecuteTask != null) cmbExecuteTask.setEnabled(enabled);
        if (cmbExecuteCommittee != null) cmbExecuteCommittee.setEnabled(enabled);
        if (btnExecute != null) btnExecute.setEnabled(enabled);
        if (btnReport != null) btnReport.setEnabled(enabled);
    }

    private void updateEventSelection(Event selectedEvent) {
        this.eventAktif = selectedEvent;
        boolean hasActiveEvent = (selectedEvent != null);

        setRightPanelEnabled(hasActiveEvent);

        if (hasActiveEvent) {
            // Load Division, Panitia, and Tugas from database for this event
            List<Division> divisions = divisionDAO.getDivisionsByEvent(selectedEvent.getEventId());
            List<Committee> committees = panitiaDAO.getPanitiaByEvent(selectedEvent.getEventId());
            List<Task> tasks = tugasDAO.getTugasByEvent(selectedEvent.getEventId());
            
            // Set these lists to the active event
            selectedEvent.getDaftarDivisi().clear();
            selectedEvent.getDaftarDivisi().addAll(divisions);
            selectedEvent.getDaftarPanitia().clear();
            selectedEvent.getDaftarPanitia().addAll(committees);
            selectedEvent.getDaftarTugas().clear();
            selectedEvent.getDaftarTugas().addAll(tasks);

            // Populate form event in Right Panel
            if (txtEventId != null) txtEventId.setText("");
            if (txtEventName != null) txtEventName.setText(selectedEvent.getEventName());
            if (txtEventBudget != null) txtEventBudget.setText(String.valueOf(selectedEvent.getTotalBudget()));

            refreshEventTable();
            refreshTaskTable();
            refreshDivisionTable();
            refreshCommitteeTable();
            updateSummary();
            setComboRenderer();
        } else {
            // Clear right panel fields
            if (txtEventId != null) txtEventId.setText("");
            if (txtEventName != null) txtEventName.setText("");
            if (txtEventBudget != null) txtEventBudget.setText("");

            if (txtTaskId != null) txtTaskId.setText("");
            if (txtTaskName != null) txtTaskName.setText("");
            if (txtDifficulty != null) txtDifficulty.setText("");
            if (txtTaskCost != null) txtTaskCost.setText("");

            if (txtDivisionBudget != null) txtDivisionBudget.setText("");

            if (txtCommitteeId != null) txtCommitteeId.setText("");
            if (txtCommitteeName != null) txtCommitteeName.setText("");
            if (txtMaxCapacity != null) txtMaxCapacity.setText("");

            refreshEventTable();
            if (taskTableModel != null) taskTableModel.setRowCount(0);
            if (divisionTableModel != null) divisionTableModel.setRowCount(0);
            if (committeeTableModel != null) committeeTableModel.setRowCount(0);

            if (cmbExecuteTask != null) cmbExecuteTask.removeAllItems();
            if (cmbExecuteDivision != null) cmbExecuteDivision.removeAllItems();
            if (cmbExecuteCommittee != null) cmbExecuteCommittee.removeAllItems();
            if (cmbTaskDivision != null) cmbTaskDivision.removeAllItems();
            if (cmbTaskCommittee != null) cmbTaskCommittee.removeAllItems();

            updateSummary();
        }
    }

    private void showManageTasksDialog(Committee committee) {
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, "Daftar Tugas: " + committee.getName(), true);
        dialog.setSize(600, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID Tugas", "Nama Tugas", "Beban Kerja", "Biaya"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = createTable(model);
        // Hide ID Tugas column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        // Load data
        List<Task> assignedTasks = tugasDAO.getTugasByPanitia(committee.getCommitteeId());
        for (Task t : assignedTasks) {
            model.addRow(new Object[]{t.getTaskId(), t.getTaskName(), t.getDifficulty(), formatMoney(t.getTaskCost())});
        }

        JButton btnCabut = createPrimaryButton("Cabut Tugas");
        btnCabut.setEnabled(false);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnCabut.setEnabled(table.getSelectedRow() != -1);
            }
        });

        btnCabut.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showWarning("Pilih tugas yang ingin dicabut!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String taskId = (String) model.getValueAt(modelRow, 0);
            int difficulty = (Integer) model.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(dialog, "Apakah Anda yakin ingin mencabut tugas ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Update database: set id_panitia = NULL
                tugasDAO.assignPanitiaToTugas(taskId, null);
                
                // Update panitia: reduce current_workload
                committee.setCurrentWorkload(Math.max(0, committee.getCurrentWorkload() - difficulty));
                panitiaDAO.updatePanitia(committee);

                // Refresh table in pop-up
                model.setRowCount(0);
                List<Task> updatedTasks = tugasDAO.getTugasByPanitia(committee.getCommitteeId());
                for (Task t : updatedTasks) {
                    model.addRow(new Object[]{t.getTaskId(), t.getTaskName(), t.getDifficulty(), formatMoney(t.getTaskCost())});
                }

                // Refresh UI
                updateEventSelection(eventAktif);
                
                // Close dialog
                dialog.dispose();
                showInfo("Tugas berhasil dicabut dari panitia!");
            }
        });

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(btnCabut);

        dialog.add(createScrollPane(table), BorderLayout.CENTER);
        dialog.add(pnlButtons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
