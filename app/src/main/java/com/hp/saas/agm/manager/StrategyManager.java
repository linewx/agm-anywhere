package com.hp.saas.agm.manager;

/**
 * Created by lugan on 6/12/2014.
 */

import java.util.Map;
import java.util.HashMap;
import com.hp.saas.agm.core.model.*;


public class StrategyManager {
    private static Map<String, ServerStrategy> serverStrategies = new HashMap<String, ServerStrategy>();;
    private static StrategyManager strategyManager = new StrategyManager();

    private StrategyManager() {
        registerStrategy(new MayaStrategy());
        registerStrategy(new Ali2Strategy());
        registerStrategy(new AliStrategy());
        registerStrategy(new HorizonStrategy());
    }
    /*static {
        strategyManager = new StrategyManager();
        //serverStrategies.put("HorizonStrategy", new HorizonStrategy());

    }*/

    public void registerStrategy(ServerStrategy serverStrategy) {
        serverStrategies.put(serverStrategy.getClass().getSimpleName(), serverStrategy);
    }



    public ServerStrategy getServerStrategy(String Name) {
        return serverStrategies.get(Name);
    }

    public static StrategyManager getInstance() {
        return strategyManager;
    }
}
