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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.DefaultListModel;


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
    private Event eventAktif = null;
    private static int eventCounter = 1;

    // Left Panel Components (Master View)
    private JTextField txtNewEventName;
    private JTextField txtNewEventBudget;
    private JButton btnCreateEvent;
    private JList<String> lstEvents;
    private DefaultListModel<String> eventListModel;

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

    private DefaultTableModel eventTableModel;
    private DefaultTableModel taskTableModel;
    private DefaultTableModel divisionTableModel;
    private DefaultTableModel committeeTableModel;

    private JTable eventTable;
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
        // Initialize the event count label
        lblEventCount = new JLabel("0");

        // Initialize the event list model first so it is ready
        eventListModel = new DefaultListModel<>();
        lstEvents = new JList<>(eventListModel);

        // 1. Create Default Event
        Event defaultEvent = new Event("Dies Natalis Campus Festival", 15000000.0);

        // 2. Initialize default divisions, committee, and tasks
        Division acaraDiv = new AcaraDivision(1000000.0);
        Division logisticDiv = new LogisticDivision(4000000.0);
        Division konsumsiDiv = new KonsumsiDivision(5000000.0);
        try {
            defaultEvent.tambahDivisi(acaraDiv);
            defaultEvent.tambahDivisi(logisticDiv);
            defaultEvent.tambahDivisi(konsumsiDiv);
        } catch (OverBudgetException e) {
            // Data awal dijamin cukup
        }

        Committee comm1 = new Committee("Budi (Staf Acara)", 5);
        Committee comm2 = new Committee("Siti (Staf Logistik)", 3);
        Committee comm3 = new Committee("Andi (Staf Konsumsi)", 2);
        defaultEvent.tambahPanitia(comm1);
        defaultEvent.tambahPanitia(comm2);
        defaultEvent.tambahPanitia(comm3);

        defaultEvent.tambahTugas(new Task("Penyusunan Rundown Acara", 3, 0.0));
        defaultEvent.tambahTugas(new Task("Sewa Sound System & Stage", 2, 3500000.0));
        defaultEvent.tambahTugas(new Task("Pemesanan Catering Konsumsi", 4, 2000000.0));
        defaultEvent.tambahTugas(new Task("Dekorasi Panggung Utama", 1, 5000000.0));

        // Add to global state
        daftarEventGlobal.add(defaultEvent);
        eventListModel.addElement(defaultEvent.getEventName());

        configureLook();
        configureWindow();

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(BACKGROUND);
        rootPanel.add(createSidebar(), BorderLayout.WEST);
        rootPanel.add(createMainContent(), BorderLayout.CENTER);

        setContentPane(rootPanel);

        // Set the selection to the default event (which triggers updateEventSelection)
        lstEvents.setSelectedIndex(0);
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

        lstEvents.setBackground(new Color(39, 86, 145));
        lstEvents.setForeground(Color.WHITE);
        lstEvents.setSelectionBackground(PRIMARY);
        lstEvents.setSelectionForeground(Color.WHITE);
        lstEvents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Listener for JList selection
        lstEvents.addListSelectionListener(selectionEvent -> {
            if (!selectionEvent.getValueIsAdjusting()) {
                int index = lstEvents.getSelectedIndex();
                if (index >= 0 && index < daftarEventGlobal.size()) {
                    updateEventSelection(daftarEventGlobal.get(index));
                } else {
                    updateEventSelection(null);
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(lstEvents);
        listScrollPane.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246)));
        listScrollPane.getViewport().setBackground(new Color(39, 86, 145));
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 16, 0);
        sidebar.add(listScrollPane, gbc);

        JLabel footer = new JLabel("<html>Gunakan menu navigasi untuk memilih Event aktif.</html>");
        footer.setForeground(new Color(203, 213, 225));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridy = 5;
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
        tabbedPane.addTab("Divisi", createDivisionPanel());
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
        btnCreateEvent = createPrimaryButton("Registrasi Event");

        addFormRow(formPanel, 0, "Nama Event", txtNewEventName);
        addFormRow(formPanel, 1, "Alokasi Anggaran Utama (Rp)", txtNewEventBudget);
        addFormButton(formPanel, 2, btnCreateEvent);

        btnCreateEvent.addActionListener(e -> {
            String name = txtNewEventName.getText().trim();
            String budgetStr = txtNewEventBudget.getText().trim();

            if (name.isEmpty() || budgetStr.isEmpty()) {
                showWarning("Nama Event dan Alokasi Anggaran Utama wajib diisi!");
                return;
            }

            try {
                double budget = Double.parseDouble(budgetStr);
                if (budget < 0) {
                    showWarning("Alokasi Anggaran Utama tidak boleh bernilai negatif!");
                    return;
                }

                Event newEvent = new Event(name, budget);
                daftarEventGlobal.add(newEvent);
                eventListModel.addElement(newEvent.getEventName());

                // Reset fields
                txtNewEventName.setText("");
                txtNewEventBudget.setText("");

                // Select the new event in the list
                lstEvents.setSelectedIndex(daftarEventGlobal.size() - 1);

                showInfo("Event baru '" + name + "' berhasil diregistrasi!");
            } catch (NumberFormatException ex) {
                showWarning("Alokasi Anggaran Utama harus berupa angka!");
            }
        });

        eventTableModel = createTableModel(new Object[]{"No.", "Nama Event", "Alokasi Anggaran Utama"});
        eventTable = createTable(eventTableModel);
        eventTable.setAutoCreateRowSorter(true);

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Rincian Event Terpilih", eventTable, null, null), BorderLayout.CENTER);
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
        addFormRow(formPanel, 1, "Kapasitas Beban Kerja", txtDifficulty);
        addFormRow(formPanel, 2, "Biaya Operasional (Rp)", txtTaskCost);
        addFormRow(formPanel, 3, "Pilih Divisi Pelaksana", cmbTaskDivision);
        addFormRow(formPanel, 4, "Pilih Panitia Pelaksana", cmbTaskCommittee);
        addFormButton(formPanel, 5, btnAddTask);

        taskTableModel = createTableModel(new Object[]{"No.", "Nama Tugas", "Kapasitas Beban", "Biaya"});
        taskTable = createTable(taskTableModel);
        taskTable.setAutoCreateRowSorter(true);

        btnAddTask.addActionListener(e -> {
            if (eventAktif == null) return;
            try {
                String name = txtTaskName.getText().trim();
                int difficulty = Integer.parseInt(txtDifficulty.getText().trim());
                double cost = Double.parseDouble(txtTaskCost.getText().trim());
                
                Division division = (Division) cmbTaskDivision.getSelectedItem();
                Committee committee = (Committee) cmbTaskCommittee.getSelectedItem();

                if (name.isEmpty()) {
                    showWarning("Nama tugas wajib diisi!");
                    return;
                }

                if (division == null || committee == null) {
                    showWarning("Divisi dan Panitia pelaksana wajib dipilih!");
                    return;
                }

                Task task = new Task(name, difficulty, cost);
                
                // Panggil method eksekusiTugas dari class divisi terkait
                division.eksekusiTugas(task, committee);

                // Tambahkan tugas ke eventAktif (Root Aggregate)
                eventAktif.tambahTugas(task);
                
                refreshTaskTable();
                refreshDivisionTable();
                refreshCommitteeTable();

                clearFields(txtTaskName, txtDifficulty, txtTaskCost);
                updateSummary();
                showInfo("Penugasan operasional berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                showWarning("Kapasitas beban kerja dan biaya operasional harus berupa angka!");
            } catch (OverloadException ex) {
                showWarning("Peringatan Sistem: Beban kerja panitia melebihi batas maksimal.");
            } catch (OverBudgetException ex) {
                showWarning("Peringatan Sistem: Alokasi anggaran tidak mencukupi.");
            }
        });

        JButton btnEditTask = createPrimaryButton("Edit");
        JButton btnDeleteTask = createSecondaryButton("Hapus");
        btnEditTask.setPreferredSize(new Dimension(100, 36));
        btnDeleteTask.setPreferredSize(new Dimension(100, 36));

        btnDeleteTask.addActionListener(evt -> {
            if (eventAktif == null) return;
            int selectedViewRow = taskTable.getSelectedRow();
            if (selectedViewRow == -1) {
                showWarning("Pilih penugasan yang ingin dihapus terlebih dahulu!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus penugasan ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = taskTable.convertRowIndexToModel(selectedViewRow);
                eventAktif.getDaftarTugas().remove(modelRow);
                refreshTaskTable();
                updateSummary();
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
            JTextField editDifficultyField = new JTextField(String.valueOf(task.getDifficulty()));
            JTextField editCostField = new JTextField(String.valueOf(task.getTaskCost()));

            Object[] message = {
                "Nama Tugas:", editNameField,
                "Kapasitas Beban Kerja:", editDifficultyField,
                "Biaya Operasional (Rp):", editCostField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ubah Penugasan", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String newName = editNameField.getText().trim();
                    int newDifficulty = Integer.parseInt(editDifficultyField.getText().trim());
                    double newCost = Double.parseDouble(editCostField.getText().trim());

                    if (newName.isEmpty()) {
                        showWarning("Nama tugas tidak boleh kosong!");
                        return;
                    }

                    task.setTaskName(newName);
                    task.setDifficulty(newDifficulty);
                    task.setTaskCost(newCost);

                    refreshTaskTable();
                    showInfo("Penugasan berhasil diubah!");
                } catch (NumberFormatException ex) {
                    showWarning("Kapasitas beban kerja dan biaya operasional harus berupa angka!");
                }
            }
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Penugasan Operasional", taskTable, btnEditTask, btnDeleteTask), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDivisionPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Registrasi Divisi");

        cmbDivisionType = new JComboBox<>(new String[]{
            "Divisi Acara", 
            "Divisi Konsumsi", 
            "Divisi Logistik"
        });
        styleComboBox(cmbDivisionType);
        txtDivisionBudget = createTextField();
        btnAddDivision = createPrimaryButton("Tambah Divisi");

        addFormRow(formPanel, 0, "Jenis Divisi", cmbDivisionType);
        addFormRow(formPanel, 1, "Anggaran Divisi (Rp)", txtDivisionBudget);
        addFormButton(formPanel, 2, btnAddDivision);

        divisionTableModel = createTableModel(new Object[]{"No.", "Nama Divisi", "Anggaran Divisi", "Deskripsi Validasi"});
        divisionTable = createTable(divisionTableModel);
        divisionTable.setAutoCreateRowSorter(true);

        btnAddDivision.addActionListener(e -> {
            if (eventAktif == null) return;
            try {
                String type = cmbDivisionType.getSelectedItem().toString();
                double budget = Double.parseDouble(txtDivisionBudget.getText().trim());

                Division division = createDivision(type, budget);

                // Daftarkan divisi ke eventAktif (Root Aggregate)
                eventAktif.tambahDivisi(division);

                refreshDivisionTable();
                refreshEventTable();

                clearFields(txtDivisionBudget);
                updateSummary();
                showInfo("Divisi berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                showWarning("Anggaran harus berupa angka!");
            } catch (OverBudgetException ex) {
                showWarning("Peringatan Sistem: Alokasi anggaran tidak mencukupi.");
            }
        });

        JButton btnEditDivision = createPrimaryButton("Edit");
        JButton btnDeleteDivision = createSecondaryButton("Hapus");
        btnEditDivision.setPreferredSize(new Dimension(100, 36));
        btnDeleteDivision.setPreferredSize(new Dimension(100, 36));

        btnDeleteDivision.addActionListener(evt -> {
            if (eventAktif == null) return;
            int selectedViewRow = divisionTable.getSelectedRow();
            if (selectedViewRow == -1) {
                showWarning("Pilih divisi yang ingin dihapus terlebih dahulu!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus divisi ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int modelRow = divisionTable.convertRowIndexToModel(selectedViewRow);
                Division division = eventAktif.getDaftarDivisi().get(modelRow);
                
                // Refund division budget to active event total budget
                eventAktif.setTotalBudget(eventAktif.getTotalBudget() + division.getAllocatedBudget());
                
                eventAktif.getDaftarDivisi().remove(modelRow);
                refreshDivisionTable();
                refreshEventTable();
                updateSummary();
                showInfo("Divisi berhasil dihapus!");
            }
        });

        btnEditDivision.addActionListener(evt -> {
            if (eventAktif == null) return;
            int selectedViewRow = divisionTable.getSelectedRow();
            if (selectedViewRow == -1) {
                showWarning("Pilih divisi yang ingin diubah terlebih dahulu!");
                return;
            }
            int modelRow = divisionTable.convertRowIndexToModel(selectedViewRow);
            Division division = eventAktif.getDaftarDivisi().get(modelRow);

            JComboBox<String> editTypeCombo = new JComboBox<>(new String[]{
                "Divisi Acara", 
                "Divisi Konsumsi", 
                "Divisi Logistik"
            });
            if (division instanceof AcaraDivision) {
                editTypeCombo.setSelectedIndex(0);
            } else if (division instanceof KonsumsiDivision) {
                editTypeCombo.setSelectedIndex(1);
            } else if (division instanceof LogisticDivision) {
                editTypeCombo.setSelectedIndex(2);
            }

            JTextField editBudgetField = new JTextField(String.valueOf(division.getAllocatedBudget()));

            Object[] message = {
                "Jenis Divisi:", editTypeCombo,
                "Anggaran Divisi (Rp):", editBudgetField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ubah Divisi", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String selectedType = editTypeCombo.getSelectedItem().toString();
                    double newBudget = Double.parseDouble(editBudgetField.getText().trim());

                    if (newBudget < 0) {
                        showWarning("Anggaran divisi tidak boleh negatif!");
                        return;
                    }

                    double oldBudget = division.getAllocatedBudget();
                    double diff = newBudget - oldBudget;

                    if (eventAktif.getTotalBudget() < diff) {
                        showWarning("Peringatan Sistem: Alokasi anggaran tidak mencukupi.");
                        return;
                    }

                    eventAktif.setTotalBudget(eventAktif.getTotalBudget() - diff);

                    boolean typeChanged = false;
                    if (selectedType.equals("Divisi Acara") && !(division instanceof AcaraDivision)) {
                        typeChanged = true;
                    } else if (selectedType.equals("Divisi Konsumsi") && !(division instanceof KonsumsiDivision)) {
                        typeChanged = true;
                    } else if (selectedType.equals("Divisi Logistik") && !(division instanceof LogisticDivision)) {
                        typeChanged = true;
                    }

                    if (typeChanged) {
                        Division newDivision = createDivision(selectedType, newBudget);
                        newDivision.setDivisionId(division.getDivisionId());
                        eventAktif.getDaftarDivisi().set(modelRow, newDivision);
                    } else {
                        division.setAllocatedBudget(newBudget);
                    }

                    refreshDivisionTable();
                    refreshEventTable();
                    updateSummary();
                    showInfo("Divisi berhasil diubah!");
                } catch (NumberFormatException ex) {
                    showWarning("Anggaran harus berupa angka!");
                }
            }
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Divisi Terdaftar", divisionTable, btnEditDivision, btnDeleteDivision), BorderLayout.CENTER);
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
                
                // Daftarkan panitia ke eventAktif (Root Aggregate)
                eventAktif.tambahPanitia(committee);

                refreshCommitteeTable();

                clearFields(txtCommitteeName, txtMaxCapacity);
                updateSummary();
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
                eventAktif.getDaftarPanitia().remove(modelRow);
                refreshCommitteeTable();
                updateSummary();
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

                    refreshCommitteeTable();
                    showInfo("Panitia berhasil diubah!");
                } catch (NumberFormatException ex) {
                    showWarning("Kapasitas beban kerja harus berupa angka!");
                }
            }
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Panitia Terdaftar", committeeTable, btnEditCommittee, btnDeleteCommittee), BorderLayout.CENTER);
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

        addFormRow(formPanel, 0, "Pilih Divisi", cmbExecuteDivision);
        addFormRow(formPanel, 1, "Pilih Tugas", cmbExecuteTask);
        addFormRow(formPanel, 2, "Pilih Panitia", cmbExecuteCommittee);
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
        txtOutput.setText("Log aktivitas sistem dan laporan acara akan ditampilkan di sini.");

        btnExecute.addActionListener(e -> executeTask());
        btnReport.addActionListener(e -> showEventReport());

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
            JPanel btnPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
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
        Division division = (Division) cmbExecuteDivision.getSelectedItem();
        Task task = (Task) cmbExecuteTask.getSelectedItem();
        Committee committee = (Committee) cmbExecuteCommittee.getSelectedItem();

        if (division == null || task == null || committee == null) {
            showWarning("Divisi, tugas, dan panitia pelaksana wajib dipilih!");
            return;
        }

        try {
            division.eksekusiTugas(task, committee);

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

            refreshDivisionTable();
            refreshCommitteeTable();
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
        if (eventTableModel != null) {
            eventTableModel.setRowCount(0);
            if (eventAktif != null) {
                eventTableModel.addRow(new Object[]{
                        1,
                        eventAktif.getEventName(),
                        formatMoney(eventAktif.getTotalBudget())
                });
            }
        }
    }

    private void refreshTaskTable() {
        if (taskTableModel != null) {
            taskTableModel.setRowCount(0);
        }
        if (cmbExecuteTask != null) {
            cmbExecuteTask.removeAllItems();
        }
        if (eventAktif == null) return;
        List<Task> listTugas = eventAktif.getDaftarTugas();
        for (int i = 0; i < listTugas.size(); i++) {
            Task task = listTugas.get(i);
            if (taskTableModel != null) {
                taskTableModel.addRow(new Object[]{
                        i + 1,
                        task.getTaskName(),
                        task.getDifficulty(),
                        formatMoney(task.getTaskCost())
                });
            }
        }
        // Sorting dropdown items alphabetically (A-Z) by task name
        List<Task> sortedTasks = new ArrayList<>(listTugas);
        sortedTasks.sort((t1, t2) -> t1.getTaskName().compareToIgnoreCase(t2.getTaskName()));
        for (Task task : sortedTasks) {
            if (cmbExecuteTask != null) {
                cmbExecuteTask.addItem(task);
            }
        }
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
            if (divisionTableModel != null) {
                divisionTableModel.addRow(new Object[]{
                        i + 1,
                        division.getDivisionName(),
                        formatMoney(division.getAllocatedBudget()),
                        getDivisionDescription(division)
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
        if (cmbExecuteCommittee != null) {
            cmbExecuteCommittee.removeAllItems();
        }
        if (cmbTaskCommittee != null) {
            cmbTaskCommittee.removeAllItems();
        }
        if (eventAktif == null) return;
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
    }

    private void updateSummary() {
        if (lblEventCount != null) {
            lblEventCount.setText(String.valueOf(daftarEventGlobal.size()));
        }
        if (lblTaskCount != null) {
            lblTaskCount.setText(eventAktif != null ? String.valueOf(eventAktif.getDaftarTugas().size()) : "0");
        }
        if (lblDivisionCount != null) {
            lblDivisionCount.setText(eventAktif != null ? String.valueOf(eventAktif.getDaftarDivisi().size()) : "0");
        }
        if (lblCommitteeCount != null) {
            lblCommitteeCount.setText(eventAktif != null ? String.valueOf(eventAktif.getDaftarPanitia().size()) : "0");
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
                        setText(task.getTaskName());
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

            if (eventTableModel != null) eventTableModel.setRowCount(0);
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
}
