package engine.src.SDMEngine;

import java.util.*;

public class OrderAlertsManager {

    private final Map<StoreOwner, Set<Order>> alertsMap;

    public OrderAlertsManager() { alertsMap = new HashMap<>(); }

    public OrderAlertsManager(Map<StoreOwner, Set<Order>> alertsMap) { this.alertsMap = alertsMap; }

    public Map<StoreOwner, Set<Order>> getAlertsMap() { return alertsMap; }

    public  void addAlert(StoreOwner storeOwner, Order order) {
        synchronized (storeOwner) {
            if (!alertsMap.containsKey(storeOwner)) {
                alertsMap.put(storeOwner, new HashSet<>());
            }
        }
        alertsMap.get(storeOwner).add(order);
    }

    public Collection<Order> getAlertsForStoreOwner(StoreOwner storeOwner) {
        Collection<Order> orders = null;

        synchronized (storeOwner) {
            if (this.alertsMap.containsKey(storeOwner) && !this.alertsMap.get(storeOwner).isEmpty()) {
                orders = this.alertsMap.get(storeOwner);
                this.alertsMap.put(storeOwner, new HashSet<>());
            }
        }

        return orders;
    }
    
    @Override
    public String toString() {
        return "OrderAlertsManager{" +
                "alertsMap=" + alertsMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderAlertsManager that = (OrderAlertsManager) o;
        return Objects.equals(alertsMap, that.alertsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertsMap);
    }
}
