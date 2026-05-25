package eventplanner.gui;

import eventplanner.database.DatabaseConnection;
import eventplanner.database.DivisionDAO;
import eventplanner.database.EventDAO;
import eventplanner.database.PanitiaDAO;
import eventplanner.database.TugasDAO;
import eventplanner.division.AcaraDivision;
import eventplanner.division.Division;
import eventplanner.division.KonsumsiDivision;
import eventplanner.division.LogisticDivision;
import eventplanner.exception.OverBudgetException;
import eventplanner.exception.OverloadException;
import eventplanner.model.Committee;
import eventplanner.model.Event;
import eventplanner.model.Task;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainFrame extends JFrame {

    private static final Color BACKGROUND = new Color(244, 247, 251);
    private static final Color SURFACE = Color.WHITE;
    private static final Color PRIMARY = new Color(31, 97, 141);
    private static final Color PRIMARY_DARK = new Color(22, 48, 86);
    private static final Color ACCENT = new Color(15, 118, 110);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color WARNING = new Color(202, 138, 4);
    private static final Color DANGER = new Color(190, 18, 60);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(219, 226, 236);
    private static final String STATUS_PLANNED = "Direncanakan";
    private static final String STATUS_DONE = "Selesai";

    private final List<Event> daftarEventGlobal = new ArrayList<>();
    private Event eventAktif;
    private boolean isUpdatingDropdown = false;

    private final EventDAO eventDAO = new EventDAO();
    private final DivisionDAO divisionDAO = new DivisionDAO();
    private final PanitiaDAO panitiaDAO = new PanitiaDAO();
    private final TugasDAO tugasDAO = new TugasDAO();

    private DefaultListModel<String> eventListModel;
    private JList<String> lstEvents;
    private JTabbedPane tabbedPane;

    private JTextField txtNewEventName;
    private JTextField txtNewEventBudget;
    private JTextField txtTanggalMulai;
    private JTextField txtTanggalSelesai;
    private JTextField txtWaktuMulai;
    private JTextField txtWaktuSelesai;
    private JButton btnCreateEvent;

    private JTextField txtTaskName;
    private JTextField txtDifficulty;
    private JTextField txtTaskCost;
    private JTextField txtTaskDeadline;
    private JComboBox<Division> cmbTaskDivision;
    private JComboBox<String> cmbTaskPriority;
    private JButton btnAddTask;

    private JComboBox<String> cmbDivisionType;
    private JTextField txtDivisionBudget;
    private JButton btnAddDivision;

    private JTextField txtCommitteeName;
    private JTextField txtMaxCapacity;
    private JButton btnAddCommittee;

    private JComboBox<Division> cmbExecuteDivision;
    private JComboBox<Task> cmbExecuteTask;
    private JComboBox<Committee> cmbExecuteCommittee;
    private JButton btnExecute;
    private JButton btnReport;

    private DefaultTableModel eventTableModel;
    private DefaultTableModel taskTableModel;
    private DefaultTableModel divisionTableModel;
    private DefaultTableModel committeeTableModel;

    private JTable eventTable;
    private JTable taskTable;
    private JTable divisionTable;
    private JTable committeeTable;

    private JTextArea txtOutput;

    private JLabel lblHeaderTitle;
    private JLabel lblHeaderSubtitle;
    private JLabel lblSidebarActiveEvent;
    private JLabel lblSidebarActiveDate;
    private JLabel lblStatusBar;
    private JLabel lblEventCount;
    private JLabel lblTaskCount;
    private JLabel lblDivisionCount;
    private JLabel lblCommitteeCount;

    private JLabel lblDashboardEvent;
    private JLabel lblDashboardSchedule;
    private JLabel lblDashboardBudgetRemaining;
    private JLabel lblDashboardDivisionBudget;
    private JLabel lblDashboardSpent;
    private JLabel lblDashboardNextTask;
    private JLabel lblDashboardRisk;
    private JLabel lblTaskProgressCardText;
    private JLabel lblTaskProgressText;
    private JLabel lblWorkloadCardText;
    private JLabel lblWorkloadText;
    private JLabel lblBudgetText;
    private JLabel lblChecklistDivision;
    private JLabel lblChecklistCommittee;
    private JLabel lblChecklistTask;
    private JLabel lblChecklistBudget;
    private JLabel lblChecklistCapacity;
    private JLabel lblChecklistExecution;
    private JProgressBar prgTask;
    private JProgressBar prgWorkload;
    private JProgressBar prgBudget;

    public MainFrame() {
        eventListModel = new DefaultListModel<>();
        lstEvents = new JList<>(eventListModel);
        lblEventCount = createValueLabel("0");

        DatabaseConnection.getInstance();
        configureLook();
        configureWindow();

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(BACKGROUND);
        rootPanel.add(createSidebar(), BorderLayout.WEST);
        rootPanel.add(createMainContent(), BorderLayout.CENTER);
        rootPanel.add(createStatusBar(), BorderLayout.SOUTH);
        setContentPane(rootPanel);

        reloadEventList(null);
    }

    private void configureLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing tetap memakai look and feel default jika sistem tidak tersedia.
        }
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND);
        UIManager.put("TabbedPane.focus", BACKGROUND);
        UIManager.put("Table.showGrid", false);
    }

    private void configureWindow() {
        setTitle("Event Planner Kampus");
        setSize(1240, 760);
        setMinimumSize(new Dimension(1040, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(290, 0));
        sidebar.setBorder(new EmptyBorder(24, 20, 24, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel appName = new JLabel("Event Planner");
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        sidebar.add(appName, gbc);

        JLabel appSubtitle = new JLabel("Kampus");
        appSubtitle.setForeground(new Color(153, 246, 228));
        appSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 26, 0);
        sidebar.add(appSubtitle, gbc);

        JLabel lblEventListTitle = new JLabel("DAFTAR EVENT");
        lblEventListTitle.setForeground(new Color(203, 213, 225));
        lblEventListTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 2;
        gbc.insets = new Insets(4, 0, 8, 0);
        sidebar.add(lblEventListTitle, gbc);

        JPanel totalEventPanel = new JPanel(new BorderLayout(8, 0));
        totalEventPanel.setBackground(new Color(30, 64, 105));
        totalEventPanel.setBorder(new EmptyBorder(11, 12, 11, 12));

        JLabel lblTotalEventTitle = new JLabel("Total Event");
        lblTotalEventTitle.setForeground(new Color(219, 234, 254));
        lblTotalEventTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEventCount.setForeground(Color.WHITE);
        lblEventCount.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalEventPanel.add(lblTotalEventTitle, BorderLayout.WEST);
        totalEventPanel.add(lblEventCount, BorderLayout.EAST);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 14, 0);
        sidebar.add(totalEventPanel, gbc);

        JPanel activeEventPanel = new JPanel(new BorderLayout(0, 5));
        activeEventPanel.setBackground(new Color(17, 75, 95));
        activeEventPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel activeTitle = new JLabel("EVENT AKTIF");
        activeTitle.setForeground(new Color(153, 246, 228));
        activeTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSidebarActiveEvent = new JLabel("-");
        lblSidebarActiveEvent.setForeground(Color.WHITE);
        lblSidebarActiveEvent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSidebarActiveDate = new JLabel("-");
        lblSidebarActiveDate.setForeground(new Color(203, 213, 225));
        lblSidebarActiveDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel activeText = new JPanel(new BorderLayout(0, 3));
        activeText.setOpaque(false);
        activeText.add(lblSidebarActiveEvent, BorderLayout.NORTH);
        activeText.add(lblSidebarActiveDate, BorderLayout.SOUTH);
        activeEventPanel.add(activeTitle, BorderLayout.NORTH);
        activeEventPanel.add(activeText, BorderLayout.CENTER);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 14, 0);
        sidebar.add(activeEventPanel, gbc);

        lstEvents.setBackground(new Color(30, 64, 105));
        lstEvents.setForeground(Color.WHITE);
        lstEvents.setSelectionBackground(PRIMARY);
        lstEvents.setSelectionForeground(Color.WHITE);
        lstEvents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lstEvents.setFixedCellHeight(38);
        lstEvents.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                return label;
            }
        });
        lstEvents.addListSelectionListener(selectionEvent -> {
            if (!selectionEvent.getValueIsAdjusting()) {
                int index = lstEvents.getSelectedIndex();
                updateEventSelection(index >= 0 && index < daftarEventGlobal.size() ? daftarEventGlobal.get(index) : null);
            }
        });

        JScrollPane listScrollPane = new JScrollPane(lstEvents);
        listScrollPane.setBorder(BorderFactory.createLineBorder(new Color(56, 99, 150)));
        listScrollPane.getViewport().setBackground(new Color(30, 64, 105));
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 16, 0);
        sidebar.add(listScrollPane, gbc);

        JButton btnRefresh = createSidebarButton("Refresh Data");
        btnRefresh.addActionListener(e -> reloadEventList(eventAktif != null ? eventAktif.getEventId() : null));
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        sidebar.add(btnRefresh, gbc);

        JLabel footer = new JLabel("<html>Pilih event aktif untuk membuka dashboard, rencana tugas, dan eksekusi.</html>");
        footer.setForeground(new Color(203, 213, 225));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridy = 7;
        gbc.insets = new Insets(6, 0, 0, 0);
        sidebar.add(footer, gbc);

        return sidebar;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(24, 26, 24, 26));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        lblHeaderTitle = new JLabel("Event Planner Kampus");
        lblHeaderTitle.setForeground(TEXT);
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));

        lblHeaderSubtitle = new JLabel("Kelola timeline, budget, divisi, beban kerja panitia, dan status tugas dalam satu dashboard.");
        lblHeaderSubtitle.setForeground(MUTED);
        lblHeaderSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel titleStack = new JPanel(new BorderLayout(0, 4));
        titleStack.setOpaque(false);
        titleStack.add(lblHeaderTitle, BorderLayout.NORTH);
        titleStack.add(lblHeaderSubtitle, BorderLayout.SOUTH);
        headerPanel.add(titleStack, BorderLayout.WEST);

        JPanel topContainer = new JPanel(new BorderLayout(0, 16));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(createMetricsBar(), BorderLayout.SOUTH);
        panel.add(topContainer, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BACKGROUND);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.addTab("Dashboard", createPageScrollPane(createDashboardPanel()));
        tabbedPane.addTab("Event", createPageScrollPane(createEventPanel()));
        tabbedPane.addTab("Tugas", createPageScrollPane(createTaskPanel()));
        tabbedPane.addTab("Divisi", createPageScrollPane(createDivisionPanel()));
        tabbedPane.addTab("Panitia", createPageScrollPane(createCommitteePanel()));
        tabbedPane.addTab("Eksekusi & Laporan", createPageScrollPane(createExecutionPanel()));

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(232, 240, 248));
        statusBar.setBorder(new EmptyBorder(8, 18, 8, 18));

        lblStatusBar = new JLabel("Database: db_event_kampus | Status: siap");
        lblStatusBar.setForeground(MUTED);
        lblStatusBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBar.add(lblStatusBar, BorderLayout.WEST);
        return statusBar;
    }

    private JPanel createMetricsBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 14, 0));
        bar.setOpaque(false);

        lblTaskCount = createValueLabel("0");
        lblDivisionCount = createValueLabel("0");
        lblCommitteeCount = createValueLabel("0");

        bar.add(createMetricCard("Progres Tugas", lblTaskCount));
        bar.add(createMetricCard("Total Divisi", lblDivisionCount));
        bar.add(createMetricCard("Total Panitia", lblCommitteeCount));
        return bar;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(18, 0, 0, 0));

        JPanel insightRow = new JPanel(new GridLayout(1, 3, 14, 0));
        insightRow.setOpaque(false);

        lblDashboardBudgetRemaining = createDashboardValue("-");
        lblTaskProgressCardText = createDashboardValue("0%");
        lblWorkloadCardText = createDashboardValue("0%");

        insightRow.add(createInsightPanel("Dana Event Tersisa", lblDashboardBudgetRemaining, "Belum dialokasikan ke divisi"));
        insightRow.add(createInsightPanel("Tugas Selesai", lblTaskProgressCardText, "Progres persiapan event"));
        insightRow.add(createInsightPanel("Beban Panitia", lblWorkloadCardText, "Utilisasi kapasitas tim"));

        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 18, 0));
        centerGrid.setOpaque(false);

        JPanel overview = createSectionPanel("Ikhtisar Event Aktif");
        JPanel overviewBody = new JPanel(new GridBagLayout());
        overviewBody.setOpaque(false);
        lblDashboardEvent = createBodyValue("-");
        lblDashboardSchedule = createBodyValue("-");
        lblDashboardDivisionBudget = createBodyValue("-");
        lblDashboardSpent = createBodyValue("-");
        lblDashboardNextTask = createBodyValue("-");
        lblDashboardRisk = createBodyValue("-");
        addInfoRow(overviewBody, 0, "Event", lblDashboardEvent);
        addInfoRow(overviewBody, 1, "Jadwal", lblDashboardSchedule);
        addInfoRow(overviewBody, 2, "Sisa Dana Divisi", lblDashboardDivisionBudget);
        addInfoRow(overviewBody, 3, "Biaya Tereksekusi", lblDashboardSpent);
        addInfoRow(overviewBody, 4, "Tugas Berikutnya", lblDashboardNextTask);
        addInfoRow(overviewBody, 5, "Status Sistem", lblDashboardRisk);
        overview.add(overviewBody, BorderLayout.CENTER);

        JPanel health = createSectionPanel("Kesehatan Persiapan");
        JPanel healthBody = new JPanel(new GridBagLayout());
        healthBody.setOpaque(false);
        prgBudget = createProgressBar();
        prgTask = createProgressBar();
        prgWorkload = createProgressBar();
        lblBudgetText = createBodyValue("0%");
        lblTaskProgressText = createBodyValue("0%");
        lblWorkloadText = createBodyValue("0%");
        addProgressRow(healthBody, 0, "Pemakaian Budget Operasional", prgBudget, lblBudgetText);
        addProgressRow(healthBody, 1, "Penyelesaian Tugas", prgTask, lblTaskProgressText);
        addProgressRow(healthBody, 2, "Utilisasi Panitia", prgWorkload, lblWorkloadText);
        health.add(healthBody, BorderLayout.CENTER);

        centerGrid.add(overview);
        centerGrid.add(health);

        panel.add(insightRow, BorderLayout.NORTH);
        panel.add(centerGrid, BorderLayout.CENTER);
        panel.add(createChecklistPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createChecklistPanel() {
        JPanel checklist = createSectionPanel("Checklist Kesiapan Demo");
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 10));
        grid.setOpaque(false);

        lblChecklistDivision = createChecklistItem("Divisi");
        lblChecklistCommittee = createChecklistItem("Panitia");
        lblChecklistTask = createChecklistItem("Backlog Tugas");
        lblChecklistBudget = createChecklistItem("Budget");
        lblChecklistCapacity = createChecklistItem("Kapasitas");
        lblChecklistExecution = createChecklistItem("Eksekusi");

        grid.add(lblChecklistDivision);
        grid.add(lblChecklistCommittee);
        grid.add(lblChecklistTask);
        grid.add(lblChecklistBudget);
        grid.add(lblChecklistCapacity);
        grid.add(lblChecklistExecution);
        checklist.add(grid, BorderLayout.CENTER);
        return checklist;
    }

    private JPanel createEventPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Buat Event Baru");

        txtNewEventName = createTextField();
        txtNewEventBudget = createTextField();
        txtTanggalMulai = createTextField();
        txtTanggalSelesai = createTextField();
        txtWaktuMulai = createTextField();
        txtWaktuSelesai = createTextField();
        btnCreateEvent = createPrimaryButton("Registrasi Event");

        addFormRow(formPanel, 0, "Nama Event", txtNewEventName);
        addFormRow(formPanel, 1, "Budget Awal Event (Rp)", txtNewEventBudget);
        addFormRow(formPanel, 2, "Tanggal Mulai (YYYY-MM-DD)", txtTanggalMulai);
        addFormRow(formPanel, 3, "Tanggal Selesai (YYYY-MM-DD)", txtTanggalSelesai);
        addFormRow(formPanel, 4, "Waktu Mulai (HH:MM)", txtWaktuMulai);
        addFormRow(formPanel, 5, "Waktu Selesai (HH:MM)", txtWaktuSelesai);
        addFormButton(formPanel, 6, btnCreateEvent);
        btnCreateEvent.addActionListener(e -> createEvent());

        eventTableModel = createTableModel(new Object[]{"Nama Event", "Jadwal", "Waktu", "Sisa Dana Event", "Sisa Dana Divisi", "Status"});
        eventTable = createTable(eventTableModel);
        setColumnWidths(eventTable, 180, 190, 130, 145, 145, 190);
        eventTable.setAutoCreateRowSorter(true);

        JButton btnEditEvent = createPrimaryButton("Edit Event");
        JButton btnDeleteEvent = createDangerButton("Hapus Event");
        btnEditEvent.setPreferredSize(new Dimension(126, 36));
        btnDeleteEvent.setPreferredSize(new Dimension(126, 36));
        btnEditEvent.addActionListener(e -> editActiveEvent());
        btnDeleteEvent.addActionListener(e -> deleteActiveEvent());

        panel.add(wrapFormPanel(formPanel), BorderLayout.WEST);
        panel.add(createTablePanel("Rincian Event Aktif", eventTable, btnEditEvent, btnDeleteEvent), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTaskPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Rencana Tugas");

        txtTaskName = createTextField();
        txtDifficulty = createTextField();
        txtTaskCost = createTextField();
        txtTaskDeadline = createTextField();
        cmbTaskDivision = new JComboBox<>();
        cmbTaskPriority = new JComboBox<>(new String[]{"Tinggi", "Sedang", "Rendah"});
        styleComboBox(cmbTaskDivision);
        styleComboBox(cmbTaskPriority);
        btnAddTask = createPrimaryButton("Tambah Rencana");

        addFormRow(formPanel, 0, "Nama Tugas", txtTaskName);
        addFormRow(formPanel, 1, "Divisi Penanggung Jawab", cmbTaskDivision);
        addFormRow(formPanel, 2, "Prioritas", cmbTaskPriority);
        addFormRow(formPanel, 3, "Deadline (YYYY-MM-DD)", txtTaskDeadline);
        addFormRow(formPanel, 4, "Beban Kerja", txtDifficulty);
        addFormRow(formPanel, 5, "Estimasi Biaya (Rp)", txtTaskCost);
        addFormButton(formPanel, 6, btnAddTask);
        btnAddTask.addActionListener(e -> addTask());

        taskTableModel = createTableModel(new Object[]{"No.", "Tugas", "Divisi", "Prioritas", "Deadline", "Beban", "Biaya", "PIC", "Status"});
        taskTable = createTable(taskTableModel);
        setColumnWidths(taskTable, 50, 220, 150, 95, 110, 80, 135, 150, 120);
        taskTable.setAutoCreateRowSorter(true);

        JButton btnEditTask = createPrimaryButton("Edit");
        JButton btnDeleteTask = createSecondaryButton("Hapus");
        btnEditTask.setPreferredSize(new Dimension(100, 36));
        btnDeleteTask.setPreferredSize(new Dimension(100, 36));
        btnEditTask.addActionListener(e -> editSelectedTask());
        btnDeleteTask.addActionListener(e -> deleteSelectedTask());

        panel.add(wrapFormPanel(formPanel), BorderLayout.WEST);
        panel.add(createTablePanel("Backlog Tugas Event", taskTable, btnEditTask, btnDeleteTask), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDivisionPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Registrasi Divisi");

        cmbDivisionType = new JComboBox<>(new String[]{"Divisi Acara", "Divisi Konsumsi", "Divisi Logistik"});
        txtDivisionBudget = createTextField();
        btnAddDivision = createPrimaryButton("Tambah Divisi");
        styleComboBox(cmbDivisionType);

        addFormRow(formPanel, 0, "Jenis Divisi", cmbDivisionType);
        addFormRow(formPanel, 1, "Budget Divisi (Rp)", txtDivisionBudget);
        addFormButton(formPanel, 2, btnAddDivision);
        btnAddDivision.addActionListener(e -> addDivision());

        divisionTableModel = createTableModel(new Object[]{"No.", "Nama Divisi", "Sisa Anggaran", "Tipe Kontrol"});
        divisionTable = createTable(divisionTableModel);
        setColumnWidths(divisionTable, 50, 180, 150, 220);
        divisionTable.setAutoCreateRowSorter(true);

        JButton btnEditDivision = createPrimaryButton("Edit");
        JButton btnDeleteDivision = createSecondaryButton("Hapus");
        btnEditDivision.setPreferredSize(new Dimension(100, 36));
        btnDeleteDivision.setPreferredSize(new Dimension(100, 36));
        btnEditDivision.addActionListener(e -> editSelectedDivision());
        btnDeleteDivision.addActionListener(e -> deleteSelectedDivision());

        panel.add(wrapFormPanel(formPanel), BorderLayout.WEST);
        panel.add(createTablePanel("Struktur Divisi Event", divisionTable, btnEditDivision, btnDeleteDivision), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCommitteePanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Registrasi Panitia");

        txtCommitteeName = createTextField();
        txtMaxCapacity = createTextField();
        btnAddCommittee = createPrimaryButton("Tambah Panitia");

        addFormRow(formPanel, 0, "Nama Panitia", txtCommitteeName);
        addFormRow(formPanel, 1, "Kapasitas Beban Kerja", txtMaxCapacity);
        addFormButton(formPanel, 2, btnAddCommittee);
        btnAddCommittee.addActionListener(e -> addCommittee());

        committeeTableModel = createTableModel(new Object[]{"No.", "Nama Panitia", "Beban", "Kapasitas", "Utilisasi", "Status"});
        committeeTable = createTable(committeeTableModel);
        setColumnWidths(committeeTable, 50, 220, 80, 95, 95, 110);
        committeeTable.setAutoCreateRowSorter(true);

        JButton btnEditCommittee = createPrimaryButton("Edit");
        JButton btnDeleteCommittee = createSecondaryButton("Hapus");
        btnEditCommittee.setPreferredSize(new Dimension(100, 36));
        btnDeleteCommittee.setPreferredSize(new Dimension(100, 36));
        btnEditCommittee.addActionListener(e -> editSelectedCommittee());
        btnDeleteCommittee.addActionListener(e -> deleteSelectedCommittee());

        panel.add(wrapFormPanel(formPanel), BorderLayout.WEST);
        panel.add(createTablePanel("Kapasitas Tim Panitia", committeeTable, btnEditCommittee, btnDeleteCommittee), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createExecutionPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Eksekusi Tugas");

        cmbExecuteTask = new JComboBox<>();
        cmbExecuteDivision = new JComboBox<>();
        cmbExecuteCommittee = new JComboBox<>();
        styleComboBox(cmbExecuteTask);
        styleComboBox(cmbExecuteDivision);
        styleComboBox(cmbExecuteCommittee);

        btnExecute = createPrimaryButton("Tandai Selesai");
        btnReport = createSecondaryButton("Buat Laporan Event");

        addFormRow(formPanel, 0, "Pilih Tugas", cmbExecuteTask);
        addFormRow(formPanel, 1, "Divisi Eksekusi", cmbExecuteDivision);
        addFormRow(formPanel, 2, "PIC Panitia", cmbExecuteCommittee);
        addFormButton(formPanel, 3, btnExecute);
        addFormButton(formPanel, 4, btnReport);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        txtOutput.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtOutput.setForeground(TEXT);
        txtOutput.setBackground(new Color(248, 250, 252));
        txtOutput.setBorder(new EmptyBorder(18, 18, 18, 18));
        txtOutput.setText("Log eksekusi dan laporan event akan tampil di sini.");

        cmbExecuteTask.addActionListener(e -> updateExecutionRecommendation());
        btnExecute.addActionListener(e -> executeTask());
        btnReport.addActionListener(e -> showEventReport());

        panel.add(wrapFormPanel(formPanel), BorderLayout.WEST);
        panel.add(createOutputPanel(), BorderLayout.CENTER);
        return panel;
    }

    private void createEvent() {
        String name = txtNewEventName.getText().trim();
        String budgetStr = txtNewEventBudget.getText().trim();
        String startDate = txtTanggalMulai.getText().trim();
        String endDate = txtTanggalSelesai.getText().trim();
        String startTime = txtWaktuMulai.getText().trim();
        String endTime = txtWaktuSelesai.getText().trim();

        if (name.isEmpty() || budgetStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            showWarning("Semua field event wajib diisi.");
            return;
        }
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            showWarning("Format tanggal harus YYYY-MM-DD.");
            return;
        }
        if (LocalDate.parse(endDate).isBefore(LocalDate.parse(startDate))) {
            showWarning("Tanggal selesai tidak boleh sebelum tanggal mulai.");
            return;
        }
        if (!isValidTime(startTime) || !isValidTime(endTime)) {
            showWarning("Format waktu harus HH:MM.");
            return;
        }

        try {
            double budget = parsePositiveDouble(budgetStr, "Budget awal event");
            Event newEvent = new Event(name, budget);
            newEvent.setTanggalMulai(startDate);
            newEvent.setTanggalSelesai(endDate);
            newEvent.setWaktuMulai(startTime);
            newEvent.setWaktuSelesai(endTime);
            eventDAO.insertEvent(newEvent);

            clearFields(txtNewEventName, txtNewEventBudget, txtTanggalMulai, txtTanggalSelesai, txtWaktuMulai, txtWaktuSelesai);
            reloadEventList(newEvent.getEventId());
            showInfo("Event '" + name + "' berhasil dibuat.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void editActiveEvent() {
        if (eventAktif == null) {
            showWarning("Pilih event terlebih dahulu.");
            return;
        }

        JTextField nameField = new JTextField(eventAktif.getEventName());
        JTextField budgetField = new JTextField(String.valueOf(eventAktif.getTotalBudget()));
        JTextField startDateField = new JTextField(nullToEmpty(eventAktif.getTanggalMulai()));
        JTextField endDateField = new JTextField(nullToEmpty(eventAktif.getTanggalSelesai()));
        JTextField startTimeField = new JTextField(nullToEmpty(eventAktif.getWaktuMulai()));
        JTextField endTimeField = new JTextField(nullToEmpty(eventAktif.getWaktuSelesai()));

        Object[] message = {
                "Nama Event:", nameField,
                "Sisa Dana Event (Rp):", budgetField,
                "Tanggal Mulai (YYYY-MM-DD):", startDateField,
                "Tanggal Selesai (YYYY-MM-DD):", endDateField,
                "Waktu Mulai (HH:MM):", startTimeField,
                "Waktu Selesai (HH:MM):", endTimeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Event", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();
            double budget = parseNonNegativeDouble(budgetField.getText().trim(), "Sisa dana event");

            if (name.isEmpty()) {
                showWarning("Nama event tidak boleh kosong.");
                return;
            }
            if (!isValidDate(startDate) || !isValidDate(endDate)) {
                showWarning("Format tanggal harus YYYY-MM-DD.");
                return;
            }
            if (LocalDate.parse(endDate).isBefore(LocalDate.parse(startDate))) {
                showWarning("Tanggal selesai tidak boleh sebelum tanggal mulai.");
                return;
            }
            if (!isValidTime(startTime) || !isValidTime(endTime)) {
                showWarning("Format waktu harus HH:MM.");
                return;
            }

            eventAktif.setEventName(name);
            eventAktif.setTotalBudget(budget);
            eventAktif.setTanggalMulai(startDate);
            eventAktif.setTanggalSelesai(endDate);
            eventAktif.setWaktuMulai(startTime);
            eventAktif.setWaktuSelesai(endTime);
            eventDAO.updateEvent(eventAktif);
            reloadEventList(eventAktif.getEventId());
            showInfo("Event berhasil diperbarui.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void deleteActiveEvent() {
        if (eventAktif == null) {
            showWarning("Pilih event terlebih dahulu.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hapus event '" + eventAktif.getEventName() + "' beserta divisi, panitia, dan tugasnya?",
                "Konfirmasi Hapus Event",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            eventDAO.deleteEvent(eventAktif.getEventId());
            reloadEventList(null);
            showInfo("Event berhasil dihapus.");
        }
    }

    private void addTask() {
        if (eventAktif == null) {
            showWarning("Pilih event terlebih dahulu.");
            return;
        }

        String name = txtTaskName.getText().trim();
        String deadline = txtTaskDeadline.getText().trim();
        Division division = (Division) cmbTaskDivision.getSelectedItem();

        if (name.isEmpty()) {
            showWarning("Nama tugas wajib diisi.");
            return;
        }
        if (division == null) {
            showWarning("Tambahkan dan pilih divisi penanggung jawab dulu.");
            return;
        }
        if (!isValidDate(deadline)) {
            showWarning("Deadline tugas harus berformat YYYY-MM-DD.");
            return;
        }

        try {
            int difficulty = parsePositiveInt(txtDifficulty.getText().trim(), "Beban kerja");
            double cost = parseNonNegativeDouble(txtTaskCost.getText().trim(), "Estimasi biaya");
            Task task = new Task(name, difficulty, cost);
            task.setDivisionId(division.getDivisionId());
            task.setDeadline(deadline);
            task.setPriority((String) cmbTaskPriority.getSelectedItem());
            task.setStatus(STATUS_PLANNED);
            tugasDAO.insertTugas(task, eventAktif.getEventId());

            clearFields(txtTaskName, txtDifficulty, txtTaskCost, txtTaskDeadline);
            updateEventSelection(eventAktif);
            showInfo("Rencana tugas berhasil ditambahkan ke backlog.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void editSelectedTask() {
        Task task = getSelectedTask();
        if (task == null) {
            showWarning("Pilih tugas yang ingin diubah.");
            return;
        }
        if (task.isCompleted()) {
            showWarning("Tugas yang sudah selesai tidak bisa diedit agar laporan tetap konsisten.");
            return;
        }

        JTextField nameField = new JTextField(task.getTaskName());
        JTextField difficultyField = new JTextField(String.valueOf(task.getDifficulty()));
        JTextField costField = new JTextField(String.valueOf(task.getTaskCost()));
        JTextField deadlineField = new JTextField(nullToEmpty(task.getDeadline()));

        JComboBox<Division> divisionCombo = new JComboBox<>();
        for (Division division : eventAktif.getDaftarDivisi()) {
            divisionCombo.addItem(division);
            if (division.getDivisionId().equals(task.getDivisionId())) {
                divisionCombo.setSelectedItem(division);
            }
        }
        styleComboBox(divisionCombo);
        divisionCombo.setRenderer(createDivisionRenderer());

        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"Tinggi", "Sedang", "Rendah"});
        priorityCombo.setSelectedItem(task.getPriority());

        Object[] message = {
                "Nama Tugas:", nameField,
                "Divisi:", divisionCombo,
                "Prioritas:", priorityCombo,
                "Deadline (YYYY-MM-DD):", deadlineField,
                "Beban Kerja:", difficultyField,
                "Estimasi Biaya (Rp):", costField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Tugas", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String deadline = deadlineField.getText().trim();
            Division division = (Division) divisionCombo.getSelectedItem();
            if (name.isEmpty()) {
                showWarning("Nama tugas tidak boleh kosong.");
                return;
            }
            if (division == null) {
                showWarning("Divisi tugas wajib dipilih.");
                return;
            }
            if (!isValidDate(deadline)) {
                showWarning("Deadline tugas harus berformat YYYY-MM-DD.");
                return;
            }

            task.setTaskName(name);
            task.setDivisionId(division.getDivisionId());
            task.setPriority((String) priorityCombo.getSelectedItem());
            task.setDeadline(deadline);
            task.setDifficulty(parsePositiveInt(difficultyField.getText().trim(), "Beban kerja"));
            task.setTaskCost(parseNonNegativeDouble(costField.getText().trim(), "Estimasi biaya"));
            tugasDAO.updateTugas(task);
            updateEventSelection(eventAktif);
            showInfo("Tugas berhasil diperbarui.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void deleteSelectedTask() {
        Task task = getSelectedTask();
        if (task == null) {
            showWarning("Pilih tugas yang ingin dihapus.");
            return;
        }
        if (task.isCompleted()) {
            showWarning("Tugas yang sudah selesai tidak bisa dihapus dari planner.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus rencana tugas '" + task.getTaskName() + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tugasDAO.deleteTugas(task.getTaskId());
            updateEventSelection(eventAktif);
            showInfo("Rencana tugas berhasil dihapus.");
        }
    }

    private void addDivision() {
        if (eventAktif == null) {
            showWarning("Pilih event terlebih dahulu.");
            return;
        }

        try {
            String type = (String) cmbDivisionType.getSelectedItem();
            double budget = parsePositiveDouble(txtDivisionBudget.getText().trim(), "Budget divisi");
            if (divisionTypeExists(type, null)) {
                showWarning(type + " sudah terdaftar di event ini.");
                return;
            }

            Division division = createDivision(type, budget);
            eventAktif.tambahDivisi(division);
            divisionDAO.insertDivision(division, eventAktif.getEventId());
            eventDAO.updateEvent(eventAktif);
            clearFields(txtDivisionBudget);
            updateEventSelection(eventAktif);
            showInfo(type + " berhasil ditambahkan.");
        } catch (IllegalArgumentException | OverBudgetException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void editSelectedDivision() {
        Division division = getSelectedDivision();
        if (division == null) {
            showWarning("Pilih divisi yang ingin diubah.");
            return;
        }

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Divisi Acara", "Divisi Konsumsi", "Divisi Logistik"});
        typeCombo.setSelectedItem(division.getDivisionName());
        JTextField budgetField = new JTextField(String.valueOf(division.getAllocatedBudget()));

        Object[] message = {
                "Jenis Divisi:", typeCombo,
                "Sisa Budget Divisi (Rp):", budgetField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Divisi", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String selectedType = (String) typeCombo.getSelectedItem();
            double newBudget = parseNonNegativeDouble(budgetField.getText().trim(), "Sisa budget divisi");
            if (divisionTypeExists(selectedType, division.getDivisionId())) {
                showWarning(selectedType + " sudah terdaftar di event ini.");
                return;
            }

            double difference = newBudget - division.getAllocatedBudget();
            if (difference > eventAktif.getTotalBudget()) {
                showWarning("Sisa dana event tidak cukup untuk menaikkan budget divisi.");
                return;
            }

            eventAktif.setTotalBudget(eventAktif.getTotalBudget() - difference);
            Division updated = createDivision(selectedType, newBudget);
            updated.setDivisionId(division.getDivisionId());
            divisionDAO.updateDivision(updated);
            eventDAO.updateEvent(eventAktif);
            updateEventSelection(eventAktif);
            showInfo("Divisi berhasil diperbarui.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void deleteSelectedDivision() {
        Division division = getSelectedDivision();
        if (division == null) {
            showWarning("Pilih divisi yang ingin dihapus.");
            return;
        }
        if (hasTaskForDivision(division.getDivisionId())) {
            showWarning("Divisi masih dipakai oleh tugas. Ubah atau hapus tugas terkait terlebih dahulu.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus " + division.getDivisionName() + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            eventAktif.setTotalBudget(eventAktif.getTotalBudget() + division.getAllocatedBudget());
            divisionDAO.deleteDivision(division.getDivisionId());
            eventDAO.updateEvent(eventAktif);
            updateEventSelection(eventAktif);
            showInfo("Divisi berhasil dihapus.");
        }
    }

    private void addCommittee() {
        if (eventAktif == null) {
            showWarning("Pilih event terlebih dahulu.");
            return;
        }

        String name = txtCommitteeName.getText().trim();
        if (name.isEmpty()) {
            showWarning("Nama panitia wajib diisi.");
            return;
        }

        try {
            int maxCapacity = parsePositiveInt(txtMaxCapacity.getText().trim(), "Kapasitas beban kerja");
            Committee committee = new Committee(name, maxCapacity);
            panitiaDAO.insertPanitia(committee, eventAktif.getEventId());
            clearFields(txtCommitteeName, txtMaxCapacity);
            updateEventSelection(eventAktif);
            showInfo("Panitia berhasil ditambahkan.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void editSelectedCommittee() {
        Committee committee = getSelectedCommittee();
        if (committee == null) {
            showWarning("Pilih panitia yang ingin diubah.");
            return;
        }

        JTextField nameField = new JTextField(committee.getName());
        JTextField capacityField = new JTextField(String.valueOf(committee.getMaxCapacity()));

        Object[] message = {
                "Nama Panitia:", nameField,
                "Kapasitas Beban Kerja:", capacityField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Panitia", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            int capacity = parsePositiveInt(capacityField.getText().trim(), "Kapasitas beban kerja");
            if (name.isEmpty()) {
                showWarning("Nama panitia tidak boleh kosong.");
                return;
            }
            if (capacity < committee.getCurrentWorkload()) {
                showWarning("Kapasitas baru tidak boleh lebih kecil dari beban saat ini (" + committee.getCurrentWorkload() + ").");
                return;
            }

            committee.setName(name);
            committee.setMaxCapacity(capacity);
            panitiaDAO.updatePanitia(committee);
            updateEventSelection(eventAktif);
            showInfo("Data panitia berhasil diperbarui.");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage());
        }
    }

    private void deleteSelectedCommittee() {
        Committee committee = getSelectedCommittee();
        if (committee == null) {
            showWarning("Pilih panitia yang ingin dihapus.");
            return;
        }
        if (hasTaskForCommittee(committee.getCommitteeId())) {
            showWarning("Panitia masih tercatat sebagai PIC tugas. Data tugas perlu dipindahkan dulu.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus panitia '" + committee.getName() + "'?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            panitiaDAO.deletePanitia(committee.getCommitteeId());
            updateEventSelection(eventAktif);
            showInfo("Panitia berhasil dihapus.");
        }
    }

    private void executeTask() {
        if (eventAktif == null) {
            showWarning("Pilih event terlebih dahulu.");
            return;
        }

        Task task = (Task) cmbExecuteTask.getSelectedItem();
        Division division = (Division) cmbExecuteDivision.getSelectedItem();
        Committee committee = (Committee) cmbExecuteCommittee.getSelectedItem();

        if (task == null || division == null || committee == null) {
            showWarning("Pilih tugas, divisi, dan PIC panitia terlebih dahulu.");
            return;
        }
        if (task.isCompleted()) {
            showWarning("Tugas ini sudah selesai.");
            return;
        }
        if (task.getDivisionId() != null && !task.getDivisionId().equals(division.getDivisionId())) {
            showWarning("Divisi eksekusi harus sama dengan divisi penanggung jawab tugas.");
            return;
        }

        try {
            division.eksekusiTugas(task, committee);
            String completedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            task.setDivisionId(division.getDivisionId());
            tugasDAO.completeTugas(task, committee.getCommitteeId(), completedAt);
            panitiaDAO.updatePanitia(committee);
            divisionDAO.updateDivision(division);

            txtOutput.setText(
                    "EKSEKUSI TUGAS BERHASIL\n\n"
                            + "Event       : " + eventAktif.getEventName() + "\n"
                            + "Tugas       : " + task.getTaskName() + "\n"
                            + "Divisi      : " + division.getDivisionName() + "\n"
                            + "PIC         : " + committee.getName() + "\n"
                            + "Selesai     : " + completedAt + "\n\n"
                            + "Status Panitia:\n"
                            + "Beban kerja : " + committee.getCurrentWorkload() + " / " + committee.getMaxCapacity() + "\n\n"
                            + "Status Divisi:\n"
                            + division.buatLaporan()
            );
            updateEventSelection(eventAktif);
            showInfo("Tugas berhasil ditandai selesai.");
        } catch (OverloadException ex) {
            showWarning("Beban kerja panitia melebihi kapasitas.");
            txtOutput.setText("EKSEKUSI GAGAL\n\nPenyebab: Beban kerja panitia melebihi kapasitas.");
        } catch (OverBudgetException ex) {
            showWarning("Budget divisi tidak mencukupi untuk tugas ini.");
            txtOutput.setText("EKSEKUSI GAGAL\n\nPenyebab: Budget divisi tidak mencukupi.");
        }
    }

    private void showEventReport() {
        if (eventAktif == null) {
            txtOutput.setText("Belum ada event aktif yang dipilih.");
            return;
        }
        txtOutput.setText(eventAktif.buatLaporanAcara());
    }

    private void reloadEventList(String preferredEventId) {
        daftarEventGlobal.clear();
        daftarEventGlobal.addAll(eventDAO.getAllEvents());
        eventListModel.clear();
        for (Event event : daftarEventGlobal) {
            eventListModel.addElement(getEventListText(event));
        }

        int selectedIndex = findEventIndex(preferredEventId);
        if (selectedIndex < 0 && !daftarEventGlobal.isEmpty()) {
            selectedIndex = 0;
        }

        if (selectedIndex >= 0) {
            lstEvents.setSelectedIndex(selectedIndex);
            updateEventSelection(daftarEventGlobal.get(selectedIndex));
        } else {
            updateEventSelection(null);
        }
        updateSummary();
    }

    private int findEventIndex(String eventId) {
        if (eventId == null) {
            return -1;
        }
        for (int i = 0; i < daftarEventGlobal.size(); i++) {
            if (eventId.equals(daftarEventGlobal.get(i).getEventId())) {
                return i;
            }
        }
        return -1;
    }

    private String getEventListText(Event event) {
        String startDate = event.getTanggalMulai() == null || event.getTanggalMulai().isBlank()
                ? "-"
                : event.getTanggalMulai();
        return shortText(event.getEventName(), 24) + "  |  " + startDate;
    }

    private void updateEventSelection(Event selectedEvent) {
        eventAktif = selectedEvent;
        setWorkflowEnabled(selectedEvent != null);

        if (selectedEvent != null) {
            Event latestEvent = eventDAO.getEventById(selectedEvent.getEventId());
            if (latestEvent != null) {
                selectedEvent.setEventName(latestEvent.getEventName());
                selectedEvent.setTotalBudget(latestEvent.getTotalBudget());
                selectedEvent.setTanggalMulai(latestEvent.getTanggalMulai());
                selectedEvent.setTanggalSelesai(latestEvent.getTanggalSelesai());
                selectedEvent.setWaktuMulai(latestEvent.getWaktuMulai());
                selectedEvent.setWaktuSelesai(latestEvent.getWaktuSelesai());
            }

            selectedEvent.getDaftarDivisi().clear();
            selectedEvent.getDaftarDivisi().addAll(divisionDAO.getDivisionsByEvent(selectedEvent.getEventId()));
            selectedEvent.getDaftarPanitia().clear();
            selectedEvent.getDaftarPanitia().addAll(panitiaDAO.getPanitiaByEvent(selectedEvent.getEventId()));
            selectedEvent.getDaftarTugas().clear();
            selectedEvent.getDaftarTugas().addAll(tugasDAO.getTugasByEvent(selectedEvent.getEventId()));
        }

        refreshEventTable();
        refreshDivisionTable();
        refreshCommitteeTable();
        refreshTaskTable();
        updateSummary();
        updateDashboard();
        setComboRenderer();
    }

    private void refreshEventTable() {
        if (eventTableModel == null) {
            return;
        }
        eventTableModel.setRowCount(0);
        if (eventAktif == null) {
            return;
        }

        eventTableModel.addRow(new Object[]{
                eventAktif.getEventName(),
                nullToDash(eventAktif.getTanggalMulai()) + " s.d. " + nullToDash(eventAktif.getTanggalSelesai()),
                nullToDash(eventAktif.getWaktuMulai()) + " - " + nullToDash(eventAktif.getWaktuSelesai()),
                formatMoney(eventAktif.getTotalBudget()),
                formatMoney(calculateDivisionBudget()),
                getPreparationStatus()
        });
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

        List<Task> tasks = eventAktif.getDaftarTugas();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (taskTableModel != null) {
                taskTableModel.addRow(new Object[]{
                        i + 1,
                        task.getTaskName(),
                        findDivisionName(task.getDivisionId()),
                        task.getPriority(),
                        nullToDash(task.getDeadline()),
                        task.getDifficulty(),
                        formatMoney(task.getTaskCost()),
                        findCommitteeName(task.getCommitteeId()),
                        task.getStatus()
                });
            }
            if (!task.isCompleted() && cmbExecuteTask != null) {
                cmbExecuteTask.addItem(task);
            }
        }
        isUpdatingDropdown = false;
        updateExecutionRecommendation();
    }

    private void refreshDivisionTable() {
        if (divisionTableModel != null) {
            divisionTableModel.setRowCount(0);
        }
        if (cmbTaskDivision != null) {
            cmbTaskDivision.removeAllItems();
        }
        if (cmbExecuteDivision != null) {
            cmbExecuteDivision.removeAllItems();
        }
        if (eventAktif == null) {
            return;
        }

        List<Division> divisions = eventAktif.getDaftarDivisi();
        for (int i = 0; i < divisions.size(); i++) {
            Division division = divisions.get(i);
            if (divisionTableModel != null) {
                divisionTableModel.addRow(new Object[]{
                        i + 1,
                        division.getDivisionName(),
                        formatMoney(division.getAllocatedBudget()),
                        getDivisionDescription(division)
                });
            }
            if (cmbTaskDivision != null) {
                cmbTaskDivision.addItem(division);
            }
            if (cmbExecuteDivision != null) {
                cmbExecuteDivision.addItem(division);
            }
        }
    }

    private void refreshCommitteeTable() {
        if (committeeTableModel != null) {
            committeeTableModel.setRowCount(0);
        }
        if (cmbExecuteCommittee != null) {
            cmbExecuteCommittee.removeAllItems();
        }
        if (eventAktif == null) {
            return;
        }

        List<Committee> committees = eventAktif.getDaftarPanitia();
        for (int i = 0; i < committees.size(); i++) {
            Committee committee = committees.get(i);
            int percent = percent(committee.getCurrentWorkload(), committee.getMaxCapacity());
            if (committeeTableModel != null) {
                committeeTableModel.addRow(new Object[]{
                        i + 1,
                        committee.getName(),
                        committee.getCurrentWorkload(),
                        committee.getMaxCapacity(),
                        percent + "%",
                        percent >= 90 ? "Penuh" : percent >= 70 ? "Padat" : "Aman"
                });
            }
            if (cmbExecuteCommittee != null) {
                cmbExecuteCommittee.addItem(committee);
            }
        }
    }

    private void updateSummary() {
        if (lblEventCount != null) {
            lblEventCount.setText(String.valueOf(eventDAO.getEventCount()));
        }

        if (eventAktif == null) {
            if (lblTaskCount != null) lblTaskCount.setText("0");
            if (lblDivisionCount != null) lblDivisionCount.setText("0");
            if (lblCommitteeCount != null) lblCommitteeCount.setText("0");
            if (lblHeaderTitle != null) lblHeaderTitle.setText("Event Planner Kampus");
            if (lblHeaderSubtitle != null) lblHeaderSubtitle.setText("Buat event baru atau pilih event di sidebar untuk mulai mengelola persiapan.");
            return;
        }

        int totalTasks = eventAktif.getDaftarTugas().size();
        int completedTasks = countCompletedTasks();
        if (lblTaskCount != null) lblTaskCount.setText(completedTasks + "/" + totalTasks);
        if (lblDivisionCount != null) lblDivisionCount.setText(String.valueOf(eventAktif.getDaftarDivisi().size()));
        if (lblCommitteeCount != null) lblCommitteeCount.setText(String.valueOf(eventAktif.getDaftarPanitia().size()));
        if (lblHeaderTitle != null) lblHeaderTitle.setText(eventAktif.getEventName());
        if (lblHeaderSubtitle != null) {
            lblHeaderSubtitle.setText(nullToDash(eventAktif.getTanggalMulai()) + " s.d. " + nullToDash(eventAktif.getTanggalSelesai())
                    + " | " + getPreparationStatus());
        }
    }

    private void updateDashboard() {
        if (lblDashboardEvent == null) {
            return;
        }

        if (eventAktif == null) {
            lblDashboardEvent.setText("-");
            lblDashboardSchedule.setText("-");
            lblDashboardBudgetRemaining.setText("-");
            lblDashboardDivisionBudget.setText("-");
            lblDashboardSpent.setText("-");
            lblDashboardNextTask.setText("-");
            lblDashboardRisk.setText("Belum ada event aktif.");
            updateProgress(prgBudget, 0, false);
            updateProgress(prgTask, 0, true);
            updateProgress(prgWorkload, 0, false);
            lblBudgetText.setText("0%");
            lblTaskProgressCardText.setText("0%");
            lblTaskProgressText.setText("0%");
            lblWorkloadCardText.setText("0%");
            lblWorkloadText.setText("0%");
            updateSidebarActiveEvent();
            updateStatusBar();
            updateChecklist();
            return;
        }

        int totalTasks = eventAktif.getDaftarTugas().size();
        int completedTasks = countCompletedTasks();
        int taskPct = percent(completedTasks, totalTasks);
        int workloadPct = percent(calculateCurrentWorkload(), calculateMaxWorkload());
        double spent = calculateCompletedSpend();
        double estimatedTotal = Math.max(1, eventAktif.getTotalBudget() + calculateDivisionBudget() + spent);
        int budgetPct = percent(spent, estimatedTotal);

        lblDashboardEvent.setText(eventAktif.getEventName());
        lblDashboardSchedule.setText(nullToDash(eventAktif.getTanggalMulai()) + " s.d. " + nullToDash(eventAktif.getTanggalSelesai())
                + " | " + nullToDash(eventAktif.getWaktuMulai()) + " - " + nullToDash(eventAktif.getWaktuSelesai()));
        lblDashboardBudgetRemaining.setText(formatMoney(eventAktif.getTotalBudget()));
        lblDashboardDivisionBudget.setText(formatMoney(calculateDivisionBudget()));
        lblDashboardSpent.setText(formatMoney(spent));
        lblDashboardNextTask.setText(getNextTaskLabel());
        lblDashboardRisk.setText(getPreparationStatus());
        lblBudgetText.setText(budgetPct + "%");
        lblTaskProgressCardText.setText(taskPct + "%");
        lblTaskProgressText.setText(taskPct + "%");
        lblWorkloadCardText.setText(workloadPct + "%");
        lblWorkloadText.setText(workloadPct + "%");
        updateProgress(prgBudget, budgetPct, false);
        updateProgress(prgTask, taskPct, true);
        updateProgress(prgWorkload, workloadPct, false);
        updateSidebarActiveEvent();
        updateStatusBar();
        updateChecklist();
    }

    private void updateSidebarActiveEvent() {
        if (lblSidebarActiveEvent == null || lblSidebarActiveDate == null) {
            return;
        }

        if (eventAktif == null) {
            lblSidebarActiveEvent.setText("-");
            lblSidebarActiveDate.setText("Pilih atau buat event");
            return;
        }

        lblSidebarActiveEvent.setText(shortText(eventAktif.getEventName(), 26));
        lblSidebarActiveDate.setText(nullToDash(eventAktif.getTanggalMulai()) + " s.d. " + nullToDash(eventAktif.getTanggalSelesai()));
    }

    private void updateStatusBar() {
        if (lblStatusBar == null) {
            return;
        }

        if (eventAktif == null) {
            lblStatusBar.setText("Database: db_event_kampus | Status: siap | Belum ada event aktif");
            return;
        }

        lblStatusBar.setText("Database: db_event_kampus | Event: " + eventAktif.getEventName()
                + " | Tugas: " + countCompletedTasks() + "/" + eventAktif.getDaftarTugas().size()
                + " | Divisi: " + eventAktif.getDaftarDivisi().size()
                + " | Panitia: " + eventAktif.getDaftarPanitia().size());
    }

    private void updateChecklist() {
        if (lblChecklistDivision == null) {
            return;
        }

        if (eventAktif == null) {
            setChecklistStatus(lblChecklistDivision, "Divisi", false, "OK", "pilih event");
            setChecklistStatus(lblChecklistCommittee, "Panitia", false, "OK", "pilih event");
            setChecklistStatus(lblChecklistTask, "Backlog Tugas", false, "OK", "pilih event");
            setChecklistStatus(lblChecklistBudget, "Budget", false, "OK", "pilih event");
            setChecklistStatus(lblChecklistCapacity, "Kapasitas", false, "OK", "pilih event");
            setChecklistStatus(lblChecklistExecution, "Eksekusi", false, "OK", "pilih event");
            return;
        }

        boolean hasDivision = !eventAktif.getDaftarDivisi().isEmpty();
        boolean hasCommittee = !eventAktif.getDaftarPanitia().isEmpty();
        boolean hasTask = !eventAktif.getDaftarTugas().isEmpty();
        boolean hasBudget = eventAktif.getTotalBudget() >= 0 && calculateDivisionBudget() > 0;
        boolean hasCapacity = hasCommittee && hasTask && !hasTaskWithoutAvailableCommittee();
        boolean hasExecution = countCompletedTasks() > 0;

        setChecklistStatus(lblChecklistDivision, "Divisi", hasDivision, "siap", "belum ada");
        setChecklistStatus(lblChecklistCommittee, "Panitia", hasCommittee, "siap", "belum ada");
        setChecklistStatus(lblChecklistTask, "Backlog Tugas", hasTask, "siap", "belum ada");
        setChecklistStatus(lblChecklistBudget, "Budget", hasBudget, "teralokasi", "belum dialokasikan");
        setChecklistStatus(lblChecklistCapacity, "Kapasitas", hasCapacity, "aman", "cek beban");
        setChecklistStatus(lblChecklistExecution, "Eksekusi", hasExecution, "sudah ada", "belum ada");
    }

    private void setChecklistStatus(JLabel label, String title, boolean ok, String okText, String problemText) {
        label.setText(title + ": " + (ok ? okText : problemText));
        label.setForeground(ok ? SUCCESS : WARNING);
        label.setBackground(ok ? new Color(220, 252, 231) : new Color(254, 249, 195));
    }

    private void updateExecutionRecommendation() {
        if (isUpdatingDropdown || eventAktif == null || cmbExecuteTask == null || cmbExecuteCommittee == null) {
            return;
        }

        Task task = (Task) cmbExecuteTask.getSelectedItem();
        if (task == null) {
            cmbExecuteCommittee.removeAllItems();
            return;
        }

        isUpdatingDropdown = true;
        Division assignedDivision = findDivisionById(task.getDivisionId());
        if (assignedDivision != null && cmbExecuteDivision != null) {
            cmbExecuteDivision.setSelectedItem(assignedDivision);
        }

        cmbExecuteCommittee.removeAllItems();
        for (Committee committee : eventAktif.getDaftarPanitia()) {
            if (committee.getCurrentWorkload() + task.getDifficulty() <= committee.getMaxCapacity()) {
                cmbExecuteCommittee.addItem(committee);
            }
        }
        isUpdatingDropdown = false;
    }

    private JPanel createTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(18, 0, 0, 0));
        return panel;
    }

    private JScrollPane createPageScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        scrollPane.getViewport().setBackground(BACKGROUND);
        return scrollPane;
    }

    private JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SURFACE);
        panel.setPreferredSize(new Dimension(330, 0));
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

    private JScrollPane wrapFormPanel(JPanel formPanel) {
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(335, 0));
        return scrollPane;
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

    private JButton createDangerButton(String text) {
        JButton button = createBaseButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER);
        return button;
    }

    private JButton createSidebarButton(String text) {
        JButton button = createBaseButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(15, 118, 110));
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

    private JLabel createValueLabel(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(PRIMARY);
        label.setFont(new Font("Segoe UI", Font.BOLD, 25));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JLabel createDashboardValue(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(PRIMARY);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        return label;
    }

    private JLabel createBodyValue(String value) {
        JLabel label = new JLabel(value);
        label.setForeground(TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    private JLabel createChecklistItem(String title) {
        JLabel label = new JLabel(title + ": -");
        label.setOpaque(true);
        label.setBackground(new Color(241, 245, 249));
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(new EmptyBorder(10, 12, 10, 12));
        return label;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel) {
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

    private JPanel createInsightPanel(String title, JLabel valueLabel, String description) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(MUTED);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(descLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSectionPanel(String title) {
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
        return panel;
    }

    private void addInfoRow(JPanel panel, int row, String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 13, 16);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 13, 0);
        panel.add(valueLabel, gbc);
    }

    private void addProgressRow(JPanel panel, int row, String labelText, JProgressBar progressBar, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row * 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(label, gbc);

        gbc.gridy = row * 3 + 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 18, 10);
        panel.add(progressBar, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 18, 0);
        panel.add(valueLabel, gbc);
    }

    private JProgressBar createProgressBar() {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 16));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(226, 232, 240));
        progressBar.setForeground(ACCENT);
        return progressBar;
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
        table.setRowHeight(36);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(236, 240, 245));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(Color.WHITE);
        header.setBackground(PRIMARY_DARK);
        header.setPreferredSize(new Dimension(0, 38));
        header.setOpaque(true);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(PRIMARY_DARK);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(54, 91, 137)),
                        new EmptyBorder(0, 10, 0, 10)
                ));
                return label;
            }
        });

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));

                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    setForeground(TEXT);
                }

                String text = value == null ? "" : value.toString();
                if (!isSelected) {
                    if (STATUS_DONE.equalsIgnoreCase(text) || "Aman".equalsIgnoreCase(text)) {
                        setForeground(SUCCESS);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (STATUS_PLANNED.equalsIgnoreCase(text) || "Padat".equalsIgnoreCase(text) || "Tinggi".equalsIgnoreCase(text)) {
                        setForeground(WARNING);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Penuh".equalsIgnoreCase(text) || text.contains("tanpa kapasitas")) {
                        setForeground(DANGER);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Rendah".equalsIgnoreCase(text)) {
                        setForeground(ACCENT);
                    }
                }

                if (value instanceof Number || text.endsWith("%") || text.startsWith("Rp ")) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
        return table;
    }

    private void setColumnWidths(JTable table, int... widths) {
        for (int i = 0; i < widths.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private JPanel createTablePanel(String title, JTable table, JButton btnEdit, JButton btnDelete) {
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

        if (btnEdit != null || btnDelete != null) {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btnPanel.setOpaque(false);
            if (btnEdit != null) btnPanel.add(btnEdit);
            if (btnDelete != null) btnPanel.add(btnDelete);
            panel.add(btnPanel, BorderLayout.SOUTH);
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

        JLabel titleLabel = new JLabel("Log Eksekusi & Laporan Event");
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(createScrollPane(txtOutput), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setForeground(TEXT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(0, 38));
    }

    private void setComboRenderer() {
        DefaultListCellRenderer divisionRenderer = createDivisionRenderer();
        if (cmbTaskDivision != null) cmbTaskDivision.setRenderer(divisionRenderer);
        if (cmbExecuteDivision != null) cmbExecuteDivision.setRenderer(divisionRenderer);

        if (cmbExecuteTask != null) {
            cmbExecuteTask.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Task) {
                        Task task = (Task) value;
                        setText(task.getTaskName() + " (" + task.getPriority() + ")");
                    }
                    return this;
                }
            });
        }

        if (cmbExecuteCommittee != null) {
            cmbExecuteCommittee.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Committee) {
                        Committee committee = (Committee) value;
                        setText(committee.getName() + " (" + committee.getCurrentWorkload() + "/" + committee.getMaxCapacity() + ")");
                    }
                    return this;
                }
            });
        }
    }

    private DefaultListCellRenderer createDivisionRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Division) {
                    setText(((Division) value).getDivisionName());
                }
                return this;
            }
        };
    }

    private void setWorkflowEnabled(boolean enabled) {
        if (btnAddTask != null) btnAddTask.setEnabled(enabled);
        if (cmbTaskDivision != null) cmbTaskDivision.setEnabled(enabled);
        if (cmbTaskPriority != null) cmbTaskPriority.setEnabled(enabled);
        if (txtTaskName != null) txtTaskName.setEnabled(enabled);
        if (txtDifficulty != null) txtDifficulty.setEnabled(enabled);
        if (txtTaskCost != null) txtTaskCost.setEnabled(enabled);
        if (txtTaskDeadline != null) txtTaskDeadline.setEnabled(enabled);
        if (cmbDivisionType != null) cmbDivisionType.setEnabled(enabled);
        if (txtDivisionBudget != null) txtDivisionBudget.setEnabled(enabled);
        if (btnAddDivision != null) btnAddDivision.setEnabled(enabled);
        if (txtCommitteeName != null) txtCommitteeName.setEnabled(enabled);
        if (txtMaxCapacity != null) txtMaxCapacity.setEnabled(enabled);
        if (btnAddCommittee != null) btnAddCommittee.setEnabled(enabled);
        if (cmbExecuteTask != null) cmbExecuteTask.setEnabled(enabled);
        if (cmbExecuteDivision != null) cmbExecuteDivision.setEnabled(enabled);
        if (cmbExecuteCommittee != null) cmbExecuteCommittee.setEnabled(enabled);
        if (btnExecute != null) btnExecute.setEnabled(enabled);
        if (btnReport != null) btnReport.setEnabled(enabled);
    }

    private Division createDivision(String type, double budget) {
        if (type.contains("Acara")) {
            return new AcaraDivision(budget);
        }
        if (type.contains("Konsumsi")) {
            return new KonsumsiDivision(budget);
        }
        return new LogisticDivision(budget);
    }

    private String getDivisionDescription(Division division) {
        if (division instanceof AcaraDivision) {
            return "Kontrol kapasitas panitia";
        }
        if (division instanceof KonsumsiDivision) {
            return "Kontrol budget konsumsi";
        }
        if (division instanceof LogisticDivision) {
            return "Kontrol budget logistik";
        }
        return "Kontrol umum";
    }

    private Task getSelectedTask() {
        if (eventAktif == null || taskTable == null) {
            return null;
        }
        int selected = taskTable.getSelectedRow();
        if (selected < 0) {
            return null;
        }
        int modelRow = taskTable.convertRowIndexToModel(selected);
        return modelRow >= 0 && modelRow < eventAktif.getDaftarTugas().size() ? eventAktif.getDaftarTugas().get(modelRow) : null;
    }

    private Division getSelectedDivision() {
        if (eventAktif == null || divisionTable == null) {
            return null;
        }
        int selected = divisionTable.getSelectedRow();
        if (selected < 0) {
            return null;
        }
        int modelRow = divisionTable.convertRowIndexToModel(selected);
        return modelRow >= 0 && modelRow < eventAktif.getDaftarDivisi().size() ? eventAktif.getDaftarDivisi().get(modelRow) : null;
    }

    private Committee getSelectedCommittee() {
        if (eventAktif == null || committeeTable == null) {
            return null;
        }
        int selected = committeeTable.getSelectedRow();
        if (selected < 0) {
            return null;
        }
        int modelRow = committeeTable.convertRowIndexToModel(selected);
        return modelRow >= 0 && modelRow < eventAktif.getDaftarPanitia().size() ? eventAktif.getDaftarPanitia().get(modelRow) : null;
    }

    private Division findDivisionById(String id) {
        if (id == null || eventAktif == null) {
            return null;
        }
        for (Division division : eventAktif.getDaftarDivisi()) {
            if (id.equals(division.getDivisionId())) {
                return division;
            }
        }
        return null;
    }

    private String findDivisionName(String id) {
        Division division = findDivisionById(id);
        return division == null ? "-" : division.getDivisionName();
    }

    private String findCommitteeName(String id) {
        if (id == null || eventAktif == null) {
            return "-";
        }
        for (Committee committee : eventAktif.getDaftarPanitia()) {
            if (id.equals(committee.getCommitteeId())) {
                return committee.getName();
            }
        }
        return "-";
    }

    private boolean divisionTypeExists(String divisionName, String exceptId) {
        if (eventAktif == null) {
            return false;
        }
        for (Division division : eventAktif.getDaftarDivisi()) {
            boolean sameId = exceptId != null && exceptId.equals(division.getDivisionId());
            if (!sameId && divisionName.equals(division.getDivisionName())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTaskForDivision(String divisionId) {
        for (Task task : eventAktif.getDaftarTugas()) {
            if (divisionId.equals(task.getDivisionId())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTaskForCommittee(String committeeId) {
        for (Task task : eventAktif.getDaftarTugas()) {
            if (committeeId.equals(task.getCommitteeId())) {
                return true;
            }
        }
        return false;
    }

    private int countCompletedTasks() {
        int count = 0;
        if (eventAktif == null) {
            return 0;
        }
        for (Task task : eventAktif.getDaftarTugas()) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    private double calculateDivisionBudget() {
        double total = 0;
        if (eventAktif != null) {
            for (Division division : eventAktif.getDaftarDivisi()) {
                total += division.getAllocatedBudget();
            }
        }
        return total;
    }

    private double calculateCompletedSpend() {
        double total = 0;
        if (eventAktif == null) {
            return 0;
        }
        for (Task task : eventAktif.getDaftarTugas()) {
            if (task.isCompleted()) {
                Division division = findDivisionById(task.getDivisionId());
                if (!(division instanceof AcaraDivision)) {
                    total += task.getTaskCost();
                }
            }
        }
        return total;
    }

    private int calculateCurrentWorkload() {
        int total = 0;
        if (eventAktif != null) {
            for (Committee committee : eventAktif.getDaftarPanitia()) {
                total += committee.getCurrentWorkload();
            }
        }
        return total;
    }

    private int calculateMaxWorkload() {
        int total = 0;
        if (eventAktif != null) {
            for (Committee committee : eventAktif.getDaftarPanitia()) {
                total += committee.getMaxCapacity();
            }
        }
        return total;
    }

    private String getNextTaskLabel() {
        if (eventAktif == null || eventAktif.getDaftarTugas().isEmpty()) {
            return "-";
        }
        return eventAktif.getDaftarTugas().stream()
                .filter(task -> !task.isCompleted())
                .min(Comparator.comparing(Task::getDeadline, Comparator.nullsLast(String::compareTo)))
                .map(task -> task.getTaskName() + " (" + nullToDash(task.getDeadline()) + ")")
                .orElse("Semua tugas selesai");
    }

    private String getPreparationStatus() {
        if (eventAktif == null) {
            return "Belum ada event aktif";
        }
        if (eventAktif.getDaftarDivisi().isEmpty()) {
            return "Butuh minimal satu divisi";
        }
        if (eventAktif.getDaftarPanitia().isEmpty()) {
            return "Butuh data panitia";
        }
        if (eventAktif.getDaftarTugas().isEmpty()) {
            return "Butuh backlog tugas";
        }
        if (countCompletedTasks() == eventAktif.getDaftarTugas().size()) {
            return "Semua tugas selesai";
        }
        if (hasTaskWithoutAvailableCommittee()) {
            return "Ada tugas tanpa kapasitas PIC";
        }
        return "Persiapan berjalan baik";
    }

    private boolean hasTaskWithoutAvailableCommittee() {
        for (Task task : eventAktif.getDaftarTugas()) {
            if (task.isCompleted()) {
                continue;
            }
            boolean hasAvailableCommittee = false;
            for (Committee committee : eventAktif.getDaftarPanitia()) {
                if (committee.getCurrentWorkload() + task.getDifficulty() <= committee.getMaxCapacity()) {
                    hasAvailableCommittee = true;
                    break;
                }
            }
            if (!hasAvailableCommittee) {
                return true;
            }
        }
        return false;
    }

    private void updateProgress(JProgressBar progressBar, int value, boolean highIsGood) {
        if (progressBar != null) {
            progressBar.setValue(Math.max(0, Math.min(100, value)));
            if (highIsGood && value >= 80) {
                progressBar.setForeground(SUCCESS);
            } else if (value >= 90) {
                progressBar.setForeground(DANGER);
            } else if (value >= 70) {
                progressBar.setForeground(WARNING);
            } else {
                progressBar.setForeground(ACCENT);
            }
        }
    }

    private int percent(double value, double total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.round((value / total) * 100.0);
    }

    private String formatMoney(double value) {
        return String.format("Rp %,.0f", value);
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String shortText(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return nullToDash(value);
        }
        return value.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private double parsePositiveDouble(String value, String label) {
        double number = parseDouble(value, label);
        if (number <= 0) {
            throw new IllegalArgumentException(label + " harus lebih dari 0.");
        }
        return number;
    }

    private double parseNonNegativeDouble(String value, String label) {
        double number = parseDouble(value, label);
        if (number < 0) {
            throw new IllegalArgumentException(label + " tidak boleh negatif.");
        }
        return number;
    }

    private double parseDouble(String value, String label) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " harus berupa angka.");
        }
    }

    private int parsePositiveInt(String value, String label) {
        try {
            int number = Integer.parseInt(value);
            if (number <= 0) {
                throw new IllegalArgumentException(label + " harus lebih dari 0.");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " harus berupa angka bulat.");
        }
    }

    private boolean isValidDate(String value) {
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    private boolean isValidTime(String value) {
        return value != null && value.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            if (field != null) {
                field.setText("");
            }
        }
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Notifikasi Sistem", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Peringatan Sistem", JOptionPane.WARNING_MESSAGE);
    }
}
