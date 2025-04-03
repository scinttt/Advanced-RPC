package com.creaturelove;

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
    }
}
