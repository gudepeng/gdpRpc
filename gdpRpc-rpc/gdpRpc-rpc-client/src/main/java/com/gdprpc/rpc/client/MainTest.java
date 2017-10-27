package com.gdprpc.rpc.client;

/**
 * Created by 我是金角大王 on 2017-10-25.
 */
public class MainTest {
    public static void main(String[] args){
        GProxy gproxy = new GProxy();
        User user=gproxy.creat(User.class);
        String a=user.getuser();
    }
}
