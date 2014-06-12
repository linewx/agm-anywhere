package com.hp.alm.ali.manager;

/**
 * Created by lugan on 6/12/2014.
 */

import java.util.Map;
import com.hp.alm.ali.model.*;


public class StrategyManager {
    private static Map<String, ServerStrategy> serverStrategies;

    static {
        registerStrategy(new MayaStrategy());
        registerStrategy(new Ali2Strategy());
        registerStrategy(new AliStrategy());
    }

    public static void registerStrategy(ServerStrategy serverStrategy) {
        serverStrategies.put(serverStrategy.toString(), serverStrategy);
    }



    public static ServerStrategy getServerStrategy(String Name) {
        return serverStrategies.get(Name);
    }
}
