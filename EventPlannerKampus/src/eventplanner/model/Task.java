package eventplanner.model;

/**
 * Objek Java Biasa (POJO) yang merepresentasikan sebuah Tugas dalam perencana acara.
 */
public class Task {
    private String taskId;
    private String taskName;
    private int difficulty;
    private double taskCost;

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

    @Override
    public String toString() {
        return "Task{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", difficulty=" + difficulty +
                ", taskCost=" + taskCost +
                '}';
    }
}
