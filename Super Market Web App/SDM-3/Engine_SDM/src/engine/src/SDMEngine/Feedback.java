package engine.src.SDMEngine;

import java.time.LocalDate;
import java.util.Objects;

public class Feedback {

    private Customer customer;
    private LocalDate orderDate;
    private Store store;
    private String verbalFeedback;
    private int score;

    public Feedback(Customer customer, LocalDate orderDate, Store store, String verbalFeedback, int score) {
        this.customer = customer;
        this.orderDate = orderDate;
        this.store = store;
        this.verbalFeedback = verbalFeedback;
        if(score >= 5) {
            this.score = 5;
        }
        else if(score <= 1){
            this.score = 1;
        }
        else {
            this.score = score;
        }
    }

    public Customer getCustomer() { return customer; }

    public LocalDate getDate() { return orderDate; }

    public Store getStore() { return store; }

    public String getVerbalFeedback() { return verbalFeedback; }

    public int getScore() { return score; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return score == feedback.score &&
                Objects.equals(customer, feedback.customer) &&
                Objects.equals(orderDate, feedback.orderDate) &&
                Objects.equals(store, feedback.store) &&
                Objects.equals(verbalFeedback, feedback.verbalFeedback);
    }

    @Override
    public int hashCode() { return Objects.hash(customer, orderDate, store, verbalFeedback, score); }

    @Override
    public String toString() {
        return "Feedback{" +
                "customer=" + customer +
                ", date=" + orderDate +
                ", store=" + store +
                ", verbalFeedback='" + verbalFeedback + '\'' +
                ", score=" + score +
                '}';
    }
}
