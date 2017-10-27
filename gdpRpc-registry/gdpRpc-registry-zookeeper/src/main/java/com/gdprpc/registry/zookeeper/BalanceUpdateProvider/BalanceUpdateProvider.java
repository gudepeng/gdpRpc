package com.gdprpc.registry.zookeeper.BalanceUpdateProvider;

/**
 * Created by 我是金角大王 on 2017-10-27.
 */
public interface BalanceUpdateProvider {
    boolean addBalance(Integer step);

    boolean reduceBalance(Integer step);
}
