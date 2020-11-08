package engine.src.SDMEngine;

import java.util.*;

public class NewStoreAlertManager {
    private final Map<StoreOwner, Set<Store>> alertsMap;

    public NewStoreAlertManager() { alertsMap = new HashMap<>(); }

    public NewStoreAlertManager(Map<StoreOwner, Set<Store>> alertsMap) { this.alertsMap = alertsMap; }

    public Map<StoreOwner, Set<Store>> getAlertsMap() { return alertsMap; }

    public  void addAlert(StoreOwner areaOwner, Store store) {
        synchronized (areaOwner) {
            if (!alertsMap.containsKey(areaOwner)) {
                alertsMap.put(areaOwner, new HashSet<>());
            }
        }
        if(!areaOwner.equals(store.getStoreOwner())) {
            alertsMap.get(areaOwner).add(store);
        }
    }

    public Collection<Store> getAlertsForStoreOwner(StoreOwner storeOwner) {
        Collection<Store> stores = null;

        synchronized (storeOwner) {
            if (this.alertsMap.containsKey(storeOwner) && !this.alertsMap.get(storeOwner).isEmpty()) {
                stores = this.alertsMap.get(storeOwner);
                this.alertsMap.put(storeOwner, new HashSet<>());
            }
        }

        return stores;
    }

    @Override
    public String toString() {
        return "NewStoreAlertManager{" +
                "alertsMap=" + alertsMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewStoreAlertManager that = (NewStoreAlertManager) o;
        return Objects.equals(alertsMap, that.alertsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertsMap);
    }
}
