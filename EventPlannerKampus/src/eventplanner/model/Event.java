package eventplanner.model;

/**
 * Objek Java Biasa (POJO) yang merepresentasikan sebuah Acara (Event) dalam sistem.
 */
public class Event {
    private String eventId;
    private String eventName;
    private double totalBudget;

    /**
     * Konstruktor default.
     */
    public Event() {
    }

    /**
     * Konstruktor berparameter untuk mendefinisikan rincian acara.
     * 
     * @param eventId ID unik acara
     * @param eventName nama dari acara tersebut
     * @param totalBudget total anggaran yang dialokasikan untuk keseluruhan acara
     */
    public Event(String eventId, String eventName, double totalBudget) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.totalBudget = totalBudget;
    }

    // Pengakses (Getter) dan Pengubah (Setter)

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", totalBudget=" + totalBudget +
                '}';
    }
}
