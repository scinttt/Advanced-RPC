package com.creaturelove.springbootconsumer;

import com.creaturelove.entities.User;
import com.creaturelove.rpcspringbootstarter.annotation.RpcReference;
import com.creaturelove.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("CreatureLove");
        User resultUser = userService.getUser(user);
        System.out.println("Result User: " + resultUser.getName());
    }
}
