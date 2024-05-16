package com.homeis.user.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homeis.user.dto.DongCode;
import com.homeis.user.dto.User;
import com.homeis.user.model.mapper.UserMapper;
import com.homeis.util.JWTUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserMapper userMapper;
	private final JWTUtil jwtUtil;
	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Override
	public int idExist(String userId) {
		return userMapper.idExist(userId);
	}

	@Override
	@Transactional
	public int register(User userInfo) {
		if(userMapper.idExist(userInfo.getId()) == 1) return 0;
		
		String encodedPassword = passwordEncoder.encode(userInfo.getPassword());
		userInfo.setPassword(encodedPassword);
		int isSucceed = userMapper.register(userInfo);
		
		if(isSucceed == 0) return 0;
		
		Map<String, String> location = new HashMap<>();
		location.put("sidoName", userInfo.getSidoName());
		location.put("gugunName", userInfo.getGugunName());
		location.put("dongName", userInfo.getDongName());
		
		DongCode dongCode = userMapper.findByName(location);
		dongCode.setUserId(userInfo.getId());
		System.out.println(dongCode);
		
		return userMapper.insertDongCode(dongCode);
	}

	@Override
	public String login(User loginInfo) {
		String id = loginInfo.getId();
		String password = loginInfo.getPassword();
		
		User user = userMapper.login(id);
//		System.out.println(user);
//		System.out.println("비번1234암호화:"+passwordEncoder.encode(password));
		if(user==null || !passwordEncoder.matches(password, user.getPassword()) ) return null;
		
		return jwtUtil.generateToken(loginInfo);
	}
	
	@Override
	public User getUserInfo(String id) {
		return userMapper.getUserInfo(id);
	}

	@Override
	public int updateUserInfo(User userInfo) {
		String encodedPassword = passwordEncoder.encode(userInfo.getPassword());
		userInfo.setPassword(encodedPassword);
		
		return userMapper.updateUserInfo(userInfo);
	}

	@Override
	public int deleteUser(String userId) {
		return userMapper.deleteUser(userId);
	}

	@Override
	public List<DongCode> getInterestArea(String userId) {
		return userMapper.getInterestArea(userId);
	}
}
