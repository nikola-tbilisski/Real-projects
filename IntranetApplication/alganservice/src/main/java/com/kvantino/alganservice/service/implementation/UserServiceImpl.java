package com.kvantino.alganservice.service.implementation;

import com.kvantino.alganservice.dto.UserDTO;
import com.kvantino.alganservice.dtomapper.UserDTOMapper;
import com.kvantino.alganservice.model.User;
import com.kvantino.alganservice.repository.UserRepository;
import com.kvantino.alganservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository<User> userRepository;
    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }
}
