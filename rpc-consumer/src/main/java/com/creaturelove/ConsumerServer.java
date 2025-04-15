package com.creaturelove;

import com.creaturelove.bootstrap.ConsumerBootstrap;
import com.creaturelove.entities.User;
import com.creaturelove.proxy.ServiceProxyFactory;
import com.creaturelove.service.UserService;

/**
 * Hello world!
 *
 */
public class ConsumerServer
{
    public static void main( String[] args )
    {
//        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
//        System.out.println(rpc);

//        UserService userService = null;
//
//        if(rpc.isMock()){
//            userService = ServiceProxyFactory.getMockProxy(UserService.class);
//        }else{
//            userService = ServiceProxyFactory.getProxy(UserService.class);
//        }

        // Service Consumer initialization
        ConsumerBootstrap.init();

        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("Creaturelove");

        // call provider
        User newUser = userService.getUser(user);

        if(newUser != null){
            System.out.println(newUser.getName());
        }else{
            System.out.println("User doesn't exist");
        }

//        long number = userService.getNumber();
//        System.out.println(number);
    }
}
