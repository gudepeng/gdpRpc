package com.gdprpc.sample.api;

import com.gdprpc.sample.bean.User;

/**
 * @author  我是金角大王 on 2017-10-29.
 */
public interface UserServer {
    /**
     * 根据用户名查询User信息
     *
     * @param username 用户名
     * @return 返回user对象
     * */
    User findUserByUsername(String username);

}
