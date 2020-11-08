package engine.src.SDMEngine;

import java.util.*;

public class FeedbackAlertManager {

    private final Map<StoreOwner, Set<Feedback>> alertsMap;

    public FeedbackAlertManager() { alertsMap = new HashMap<>(); }

    public FeedbackAlertManager(Map<StoreOwner, Set<Feedback>> alertsMap) { this.alertsMap = alertsMap; }

    public Map<StoreOwner, Set<Feedback>> getAlertsMap() { return alertsMap; }

    public  void addAlert(StoreOwner storeOwner, Feedback feedback) {
        synchronized (storeOwner) {
            if (!alertsMap.containsKey(storeOwner)) {
                alertsMap.put(storeOwner, new HashSet<>());
            }
        }
        alertsMap.get(storeOwner).add(feedback);
    }

    public Collection<Feedback> getAlertsForStoreOwner(StoreOwner storeOwner) {
        Collection<Feedback> feedbacks = null;
        synchronized (storeOwner) {
            if (this.alertsMap.containsKey(storeOwner) && !this.alertsMap.get(storeOwner).isEmpty()) {
                feedbacks = this.alertsMap.get(storeOwner);
                this.alertsMap.put(storeOwner, new HashSet<>());
            }
        }

        return feedbacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackAlertManager that = (FeedbackAlertManager) o;
        return Objects.equals(alertsMap, that.alertsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertsMap);
    }

    @Override
    public String toString() {
        return "FeedbackAlertManager{" +
                "alertsMap=" + alertsMap +
                '}';
    }
}
