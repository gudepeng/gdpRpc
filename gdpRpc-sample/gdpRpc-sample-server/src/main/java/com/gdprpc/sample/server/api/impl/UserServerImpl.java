package com.gdprpc.sample.server.api.impl;

import com.gdprpc.sample.api.UserServer;
import com.gdprpc.sample.bean.User;

/**
 * @author 我是金角大王 on 2017-10-30.
 */
public class UserServerImpl implements UserServer {

    @Override
    public User findUserByUsername(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("123456");
        user.setLever(1);
        return user;
    }
}
