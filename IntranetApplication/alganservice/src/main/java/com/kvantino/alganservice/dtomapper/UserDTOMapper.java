package com.kvantino.alganservice.dtomapper;

import com.kvantino.alganservice.dto.UserDTO;
import com.kvantino.alganservice.model.User;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class UserDTOMapper {
    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        try {
            BeanUtils.copyProperties(userDTO, user);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return userDTO;
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        try {
            BeanUtils.copyProperties(user, userDTO);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
