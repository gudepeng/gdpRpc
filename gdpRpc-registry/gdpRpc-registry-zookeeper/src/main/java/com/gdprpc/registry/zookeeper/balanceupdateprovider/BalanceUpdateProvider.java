package com.gdprpc.registry.zookeeper.balanceupdateprovider;

/**
 * @author 我是金角大王 on 2017-10-27.
 */
public interface BalanceUpdateProvider {
    /**
     * 注册服务加权
     *
     * @param step 权重
     * @return 加权是否成功
     * */
    boolean addBalance(Integer step);

    /**
     * 注册服务减权
     *
     * @param step 权重
     * @return 减权是否成功
     * */
    boolean reduceBalance(Integer step);
}
