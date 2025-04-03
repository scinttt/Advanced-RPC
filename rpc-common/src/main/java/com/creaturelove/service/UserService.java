package com.creaturelove.service;

import com.creaturelove.entities.User;

public interface UserService {
    User getUser(User user);

    // mock return value in test
    default short getNumber() {
        return 1;
    }
}
