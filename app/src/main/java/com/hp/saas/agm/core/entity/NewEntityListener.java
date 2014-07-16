package com.hp.saas.agm.core.entity;

/**
 * Created by lugan on 7/16/2014.
 */
public class NewEntityListener {
    public interface EntityQueryListener {
        //todo: workround clone inputstream into two
        public void query(EntityQuery query, byte[] bytes);
    }
}
