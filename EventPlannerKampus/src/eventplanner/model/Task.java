package eventplanner.model;

/**
 * Objek Java Biasa (POJO) yang merepresentasikan sebuah Tugas dalam perencana acara.
 */
public class Task {
    private String taskId;
    private String taskName;
    private int difficulty;
    private double taskCost;
    private String divisionId;
    private String committeeId;
    private String deadline;
    private String priority;
    private String status;
    private String completedAt;

    /**
     * Konstruktor default.
     */
    public Task() {
    }

    /**
     * Konstruktor dengan parameter untuk mendefinisikan rincian tugas.
     * 
     * @param taskId ID unik dari tugas
     * @param taskName nama deskriptif dari tugas
     * @param difficulty tingkat kesulitan tugas (digunakan untuk beban kerja)
     * @param taskCost anggaran atau biaya keuangan yang diperlukan untuk tugas
     */
    public Task(String taskId, String taskName, int difficulty, double taskCost) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.difficulty = difficulty;
        this.taskCost = taskCost;
        this.priority = "Sedang";
        this.status = "Direncanakan";
    }

    /**
     * Konstruktor berparameter untuk mendefinisikan rincian tugas dengan ID yang di-generate otomatis.
     * 
     * @param taskName nama deskriptif dari tugas
     * @param difficulty tingkat kesulitan tugas (digunakan untuk beban kerja)
     * @param taskCost anggaran atau biaya keuangan yang diperlukan untuk tugas
     */
    public Task(String taskName, int difficulty, double taskCost) {
        this.taskId = java.util.UUID.randomUUID().toString();
        this.taskName = taskName;
        this.difficulty = difficulty;
        this.taskCost = taskCost;
        this.priority = "Sedang";
        this.status = "Direncanakan";
    }

    // Pengakses (Getter) dan Pengubah (Setter)

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public double getTaskCost() {
        return taskCost;
    }

    public void setTaskCost(double taskCost) {
        this.taskCost = taskCost;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(String committeeId) {
        this.committeeId = committeeId;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getPriority() {
        return priority == null || priority.isBlank() ? "Sedang" : priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status == null || status.isBlank() ? "Direncanakan" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isCompleted() {
        return "Selesai".equalsIgnoreCase(getStatus());
    }

    public boolean isAssigned() {
        return committeeId != null && !committeeId.isBlank();
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", difficulty=" + difficulty +
                ", taskCost=" + taskCost +
                ", divisionId='" + divisionId + '\'' +
                ", committeeId='" + committeeId + '\'' +
                ", deadline='" + deadline + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
