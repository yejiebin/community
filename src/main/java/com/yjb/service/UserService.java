package com.yjb.service;

import com.yjb.mapper.UserMapper;
import com.yjb.model.User;
import com.yjb.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample example = new UserExample();
        example.createCriteria().andAccountIdEqualTo(user.getAccountId());

        List<User> users = userMapper.selectByExample(example);
        if (users.size() == 0) {
            userMapper.insert(user);
        }else {
            User user1 = users.get(0);
            user1.setName(user.getName());
            user1.setToken(user.getToken());
            user1.setGmtModified(System.currentTimeMillis());
            user1.setAvatarUrl(user.getAvatarUrl());
            user1.setBio(user.getBio());
            userMapper.updateByPrimaryKeySelective(user1);
        }
    }
}
