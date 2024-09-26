package com.kvantino.ServeYouApp;

import com.kvantino.ServeYouApp.dto.SignUpRequestDto;
import com.kvantino.ServeYouApp.dto.UserDto;
import com.kvantino.ServeYouApp.utils.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServeYouAppApplicationTests {
	UserMapper userMapper;

	@Test
	void signupClientTest() {
		SignUpRequestDto signUpRequestDto = new SignUpRequestDto();
		signUpRequestDto.setId(1L);
		signUpRequestDto.setFirstName("Vova");
		signUpRequestDto.setLastName("Koval");
		signUpRequestDto.setPhoneNumber("599 099 345");
		signUpRequestDto.setEmail("vova@gmail.com");
		signUpRequestDto.setPassword("password");

		UserDto userDto = userMapper.userAuthRequestDto(signUpRequestDto);
		System.out.println(userDto);

		assertEquals("CLIENT" ,userDto.getUserRole().toString());
	}

}
