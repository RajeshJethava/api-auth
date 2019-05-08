package com.auth.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.api.model.User;
import com.auth.api.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService{
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public List<User> findAllUsers() {
		 List<User> users = userRepository.findAll();
		 users.stream().forEach(user -> user.setPassword(null));
		 return users;
	}
	
	public User findByUsername(String username) {
		User user =  userRepository.findByUsername(username);
		user.setPassword(null);
		return user;
	}
	
	public void saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	public void updateUser(User user) {
		userRepository.save(user);
	}

	public void deleteUserByUsername(String username) {
		userRepository.deleteById(username);
	}

	public boolean isUserExist(User user) {
		return findByUsername(user.getUsername())!=null;
	}
	
	public void deleteAllUsers(){
		userRepository.deleteAll();
	}

}
