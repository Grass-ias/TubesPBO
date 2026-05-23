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
import eventplanner.model.TaskRepository;

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
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private static final Color BACKGROUND = new Color(244, 247, 251);
    private static final Color SURFACE = Color.WHITE;
    private static final Color PRIMARY = new Color(33, 103, 166);
    private static final Color PRIMARY_DARK = new Color(24, 68, 122);
    private static final Color ACCENT = new Color(33, 150, 136);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(219, 226, 236);

    private final ArrayList<Event> eventList = new ArrayList<>();
    private final ArrayList<Division> divisionList = new ArrayList<>();
    private final ArrayList<Committee> committeeList = new ArrayList<>();
    private final TaskRepository<Task> taskRepository = new TaskRepository<>();

    private DefaultTableModel eventTableModel;
    private DefaultTableModel taskTableModel;
    private DefaultTableModel divisionTableModel;
    private DefaultTableModel committeeTableModel;

    private JComboBox<Division> cmbExecuteDivision;
    private JComboBox<Task> cmbExecuteTask;
    private JComboBox<Committee> cmbExecuteCommittee;

    private JTextArea txtOutput;

    private JLabel lblEventCount;
    private JLabel lblTaskCount;
    private JLabel lblDivisionCount;
    private JLabel lblCommitteeCount;

    public MainFrame() {
        configureLook();
        configureWindow();

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(BACKGROUND);
        rootPanel.add(createSidebar(), BorderLayout.WEST);
        rootPanel.add(createMainContent(), BorderLayout.CENTER);

        setContentPane(rootPanel);
        updateSummary();
    }

    private void configureLook() {
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND);
        UIManager.put("TabbedPane.focus", BACKGROUND);
        UIManager.put("Table.showGrid", false);
    }

    private void configureWindow() {
        setTitle("Campus Event Planner");
        setSize(1120, 720);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(28, 24, 28, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel appName = new JLabel("Campus Event");
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 25));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        sidebar.add(appName, gbc);

        JLabel appSubtitle = new JLabel("Planner");
        appSubtitle.setForeground(new Color(186, 230, 253));
        appSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 25));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        sidebar.add(appSubtitle, gbc);

        JLabel description = new JLabel("<html>Kelola event, tugas, divisi, dan panitia dalam satu dashboard.</html>");
        description.setForeground(new Color(203, 213, 225));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 28, 0);
        sidebar.add(description, gbc);

        lblEventCount = createSummaryLabel();
        lblTaskCount = createSummaryLabel();
        lblDivisionCount = createSummaryLabel();
        lblCommitteeCount = createSummaryLabel();

        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.gridy = 3;
        sidebar.add(createSummaryPanel("Event", lblEventCount), gbc);
        gbc.gridy = 4;
        sidebar.add(createSummaryPanel("Tugas", lblTaskCount), gbc);
        gbc.gridy = 5;
        sidebar.add(createSummaryPanel("Divisi", lblDivisionCount), gbc);
        gbc.gridy = 6;
        sidebar.add(createSummaryPanel("Panitia", lblCommitteeCount), gbc);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        gbc.gridy = 7;
        gbc.weighty = 1;
        sidebar.add(spacer, gbc);

        JLabel footer = new JLabel("<html>Gunakan tab Eksekusi untuk menjalankan tugas dan melihat laporan.</html>");
        footer.setForeground(new Color(203, 213, 225));
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 8;
        gbc.weighty = 0;
        gbc.insets = new Insets(18, 0, 0, 0);
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

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(24, 26, 24, 26));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Dashboard Event Planner");
        title.setForeground(TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Input data, pantau tabel, lalu eksekusi tugas dengan validasi kapasitas dan budget.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel titleStack = new JPanel(new BorderLayout(0, 4));
        titleStack.setOpaque(false);
        titleStack.add(title, BorderLayout.NORTH);
        titleStack.add(subtitle, BorderLayout.SOUTH);
        headerPanel.add(titleStack, BorderLayout.WEST);

        panel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BACKGROUND);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.addTab("Event", createEventPanel());
        tabbedPane.addTab("Tugas", createTaskPanel());
        tabbedPane.addTab("Divisi", createDivisionPanel());
        tabbedPane.addTab("Panitia", createCommitteePanel());
        tabbedPane.addTab("Eksekusi", createExecutionPanel());

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEventPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Data Event");

        JTextField txtEventId = createTextField();
        JTextField txtEventName = createTextField();
        JTextField txtEventBudget = createTextField();
        JButton btnAddEvent = createPrimaryButton("Tambah Event");

        addFormRow(formPanel, 0, "ID Event", txtEventId);
        addFormRow(formPanel, 1, "Nama Event", txtEventName);
        addFormRow(formPanel, 2, "Budget Event", txtEventBudget);
        addFormButton(formPanel, 3, btnAddEvent);

        eventTableModel = createTableModel(new Object[]{"ID Event", "Nama Event", "Budget"});
        JTable eventTable = createTable(eventTableModel);

        btnAddEvent.addActionListener(e -> {
            try {
                String id = txtEventId.getText().trim();
                String name = txtEventName.getText().trim();
                double budget = Double.parseDouble(txtEventBudget.getText().trim());

                if (id.isEmpty() || name.isEmpty()) {
                    showWarning("ID dan nama event wajib diisi!");
                    return;
                }

                Event event = new Event(id, name, budget);
                eventList.add(event);
                eventTableModel.addRow(new Object[]{
                        event.getEventId(),
                        event.getEventName(),
                        formatMoney(event.getTotalBudget())
                });

                clearFields(txtEventId, txtEventName, txtEventBudget);
                updateSummary();
                showInfo("Event berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                showWarning("Budget harus berupa angka!");
            }
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Event", eventTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTaskPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Data Tugas");

        JTextField txtTaskId = createTextField();
        JTextField txtTaskName = createTextField();
        JTextField txtDifficulty = createTextField();
        JTextField txtTaskCost = createTextField();
        JButton btnAddTask = createPrimaryButton("Tambah Tugas");

        addFormRow(formPanel, 0, "ID Tugas", txtTaskId);
        addFormRow(formPanel, 1, "Nama Tugas", txtTaskName);
        addFormRow(formPanel, 2, "Tingkat Kesulitan", txtDifficulty);
        addFormRow(formPanel, 3, "Biaya Tugas", txtTaskCost);
        addFormButton(formPanel, 4, btnAddTask);

        taskTableModel = createTableModel(new Object[]{"ID Tugas", "Nama Tugas", "Kesulitan", "Biaya"});
        JTable taskTable = createTable(taskTableModel);

        btnAddTask.addActionListener(e -> {
            try {
                String id = txtTaskId.getText().trim();
                String name = txtTaskName.getText().trim();
                int difficulty = Integer.parseInt(txtDifficulty.getText().trim());
                double cost = Double.parseDouble(txtTaskCost.getText().trim());

                if (id.isEmpty() || name.isEmpty()) {
                    showWarning("ID dan nama tugas wajib diisi!");
                    return;
                }

                Task task = new Task(id, name, difficulty, cost);
                taskRepository.addTask(task);
                taskTableModel.addRow(new Object[]{
                        task.getTaskId(),
                        task.getTaskName(),
                        task.getDifficulty(),
                        formatMoney(task.getTaskCost())
                });

                if (cmbExecuteTask != null) {
                    cmbExecuteTask.addItem(task);
                }

                clearFields(txtTaskId, txtTaskName, txtDifficulty, txtTaskCost);
                updateSummary();
                showInfo("Tugas berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                showWarning("Kesulitan dan biaya harus berupa angka!");
            }
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Tugas", taskTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDivisionPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Data Divisi");

        JComboBox<String> cmbDivisionType = new JComboBox<>(new String[]{"Acara", "Konsumsi", "Logistik"});
        styleComboBox(cmbDivisionType);
        JTextField txtBudget = createTextField();
        JButton btnAddDivision = createPrimaryButton("Tambah Divisi");

        addFormRow(formPanel, 0, "Jenis Divisi", cmbDivisionType);
        addFormRow(formPanel, 1, "Budget Divisi", txtBudget);
        addFormButton(formPanel, 2, btnAddDivision);

        divisionTableModel = createTableModel(new Object[]{"No", "Nama Divisi", "Budget", "Keterangan"});
        JTable divisionTable = createTable(divisionTableModel);

        btnAddDivision.addActionListener(e -> {
            try {
                String type = cmbDivisionType.getSelectedItem().toString();
                double budget = Double.parseDouble(txtBudget.getText().trim());
                Division division = createDivision(type, budget);

                divisionList.add(division);
                refreshDivisionTable();

                if (cmbExecuteDivision != null) {
                    cmbExecuteDivision.addItem(division);
                }

                clearFields(txtBudget);
                updateSummary();
                showInfo("Divisi berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                showWarning("Budget harus berupa angka!");
            }
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Divisi", divisionTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCommitteePanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Data Panitia");

        JTextField txtCommitteeId = createTextField();
        JTextField txtCommitteeName = createTextField();
        JTextField txtMaxCapacity = createTextField();
        JButton btnAddCommittee = createPrimaryButton("Tambah Panitia");

        addFormRow(formPanel, 0, "ID Panitia", txtCommitteeId);
        addFormRow(formPanel, 1, "Nama Panitia", txtCommitteeName);
        addFormRow(formPanel, 2, "Kapasitas Maksimal", txtMaxCapacity);
        addFormButton(formPanel, 3, btnAddCommittee);

        committeeTableModel = createTableModel(new Object[]{"No", "ID Panitia", "Nama", "Beban Saat Ini", "Kapasitas"});
        JTable committeeTable = createTable(committeeTableModel);

        btnAddCommittee.addActionListener(e -> {
            try {
                String id = txtCommitteeId.getText().trim();
                String name = txtCommitteeName.getText().trim();
                int maxCapacity = Integer.parseInt(txtMaxCapacity.getText().trim());

                if (id.isEmpty() || name.isEmpty()) {
                    showWarning("ID dan nama panitia wajib diisi!");
                    return;
                }

                Committee committee = new Committee(id, name, maxCapacity);
                committeeList.add(committee);
                refreshCommitteeTable();

                if (cmbExecuteCommittee != null) {
                    cmbExecuteCommittee.addItem(committee);
                }

                clearFields(txtCommitteeId, txtCommitteeName, txtMaxCapacity);
                updateSummary();
                showInfo("Panitia berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                showWarning("Kapasitas maksimal harus berupa angka!");
            }
        });

        panel.add(formPanel, BorderLayout.WEST);
        panel.add(createTablePanel("Daftar Panitia", committeeTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createExecutionPanel() {
        JPanel panel = createTabPanel();
        JPanel formPanel = createFormPanel("Eksekusi Tugas");

        cmbExecuteDivision = new JComboBox<>();
        cmbExecuteTask = new JComboBox<>();
        cmbExecuteCommittee = new JComboBox<>();
        styleComboBox(cmbExecuteDivision);
        styleComboBox(cmbExecuteTask);
        styleComboBox(cmbExecuteCommittee);
        setComboRenderer();

        JButton btnExecute = createPrimaryButton("Eksekusi Tugas");
        JButton btnReport = createSecondaryButton("Buat Laporan");

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
        txtOutput.setText("Output eksekusi dan laporan divisi akan tampil di sini.");

        btnExecute.addActionListener(e -> executeTask());
        btnReport.addActionListener(e -> showDivisionReport());

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

    private JPanel createTablePanel(String title, JTable table) {
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
        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Hasil Eksekusi");
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
        if (type.equals("Acara")) {
            return new AcaraDivision(budget);
        }
        if (type.equals("Konsumsi")) {
            return new KonsumsiDivision(budget);
        }
        return new LogisticDivision(budget);
    }

    private void executeTask() {
        Division division = (Division) cmbExecuteDivision.getSelectedItem();
        Task task = (Task) cmbExecuteTask.getSelectedItem();
        Committee committee = (Committee) cmbExecuteCommittee.getSelectedItem();

        if (division == null || task == null || committee == null) {
            showWarning("Divisi, tugas, dan panitia harus dipilih!");
            return;
        }

        try {
            division.eksekusiTugas(task, committee);

            txtOutput.setText(
                    "TUGAS BERHASIL DIEKSEKUSI\n\n"
                            + "Divisi  : " + division.getDivisionName() + "\n"
                            + "Tugas   : " + task.getTaskName() + "\n"
                            + "Panitia : " + committee.getCommitteeId() + " - " + committee.getName() + "\n\n"
                            + "Status Divisi:\n"
                            + division.buatLaporan() + "\n\n"
                            + "Status Panitia:\n"
                            + "Beban Kerja: " + committee.getCurrentWorkload()
                            + " / " + committee.getMaxCapacity()
            );

            refreshDivisionTable();
            refreshCommitteeTable();
            showInfo("Tugas berhasil dieksekusi!");
        } catch (OverloadException | OverBudgetException ex) {
            showWarning("Gagal: " + ex.getMessage());
            txtOutput.setText(
                    "GAGAL MENGEKSEKUSI TUGAS\n\n"
                            + "Penyebab: " + ex.getMessage()
            );
        }
    }

    private void showDivisionReport() {
        Division division = (Division) cmbExecuteDivision.getSelectedItem();

        if (division == null) {
            showWarning("Pilih divisi terlebih dahulu!");
            return;
        }

        txtOutput.setText(division.buatLaporan());
    }

    private void refreshDivisionTable() {
        divisionTableModel.setRowCount(0);

        for (int i = 0; i < divisionList.size(); i++) {
            Division division = divisionList.get(i);
            divisionTableModel.addRow(new Object[]{
                    i + 1,
                    division.getDivisionName(),
                    formatMoney(division.getAllocatedBudget()),
                    getDivisionDescription(division)
            });
        }
    }

    private void refreshCommitteeTable() {
        committeeTableModel.setRowCount(0);

        for (int i = 0; i < committeeList.size(); i++) {
            Committee committee = committeeList.get(i);
            committeeTableModel.addRow(new Object[]{
                    i + 1,
                    committee.getCommitteeId(),
                    committee.getName(),
                    committee.getCurrentWorkload(),
                    committee.getMaxCapacity()
            });
        }
    }

    private void updateSummary() {
        lblEventCount.setText(String.valueOf(eventList.size()));
        lblTaskCount.setText(String.valueOf(taskRepository.getAllTasks().size()));
        lblDivisionCount.setText(String.valueOf(divisionList.size()));
        lblCommitteeCount.setText(String.valueOf(committeeList.size()));
    }

    private String getDivisionDescription(Division division) {
        if (division instanceof AcaraDivision) {
            return "Validasi beban kerja";
        }
        if (division instanceof KonsumsiDivision) {
            return "Validasi anggaran konsumsi";
        }
        if (division instanceof LogisticDivision) {
            return "Validasi anggaran logistik";
        }
        return "Divisi umum";
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
        JOptionPane.showMessageDialog(this, message, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    private void setComboRenderer() {
        cmbExecuteDivision.setRenderer(new DefaultListCellRenderer() {
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
        });

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
                    setText(task.getTaskId() + " - " + task.getTaskName());
                }

                return this;
            }
        });

        cmbExecuteCommittee.setRenderer(new DefaultListCellRenderer() {
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
                    setText(committee.getCommitteeId() + " - " + committee.getName());
                }

                return this;
            }
        });
    }
}
