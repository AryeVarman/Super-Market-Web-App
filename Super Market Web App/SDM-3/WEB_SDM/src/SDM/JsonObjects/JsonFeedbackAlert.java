package SDM.JsonObjects;

public class JsonFeedbackAlert {
    private final String storeName;
    private final String customerName;
    private final int score;
    private final String verbalFeedback;
    private final String date;

    public JsonFeedbackAlert(String storeName, String customerName, int score, String verbalFeedback, String date) {
        this.storeName = storeName;
        this.customerName = customerName;
        this.score = score;
        this.verbalFeedback = verbalFeedback;
        this.date = date;
    }
}
