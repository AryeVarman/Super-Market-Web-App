package engine.src.SDMEngine;

import java.util.HashMap;
import java.util.Map;

public class StoreOwner extends User {

    Map<String, AreaManager> areaOwnerList;

    public StoreOwner(int serialNumber, String name) {
        super(serialNumber, name, UserManager.UserType.STORE_OWNER);
        areaOwnerList = new HashMap<>();
    }



}
