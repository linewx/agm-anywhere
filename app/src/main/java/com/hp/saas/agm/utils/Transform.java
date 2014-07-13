package com.hp.saas.agm.utils;

/**
 * Created by lugan on 6/11/2014.
 */
public interface Transform <S, T> {
    T transform(S s);
}