package com.creaturelove.springbootprovider;

import com.creaturelove.entities.User;
import com.creaturelove.rpcspringbootstarter.annotation.RpcService;
import com.creaturelove.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user){
        System.out.println("Username :" + user.getName());

        return user;
    }
}
