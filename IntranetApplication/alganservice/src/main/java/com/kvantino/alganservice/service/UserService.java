package com.kvantino.alganservice.service;

import com.kvantino.alganservice.dto.UserDTO;
import com.kvantino.alganservice.model.User;

public interface UserService {
    UserDTO createUser(User user);
}
