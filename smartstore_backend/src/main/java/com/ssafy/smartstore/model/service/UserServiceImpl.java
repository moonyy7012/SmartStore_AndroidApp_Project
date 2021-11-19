package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.UserDao;
import com.ssafy.smartstore.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private static UserServiceImpl instance = new UserServiceImpl();

    private UserServiceImpl() {

    }

    public static UserServiceImpl getInstance() {
        return instance;
    }

    @Autowired
    private UserDao userDao;

    @Override
    public void join(User user) {
        userDao.insert(user);
    }

    @Override
    public User login(String id, String pass) {
        User user = userDao.select(id);

        if (user != null && user.getPass().equals(pass)) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public void leave(String id) {
        userDao.delete(id);
    }

    @Override
    public boolean isUsedId(String id) {
        return userDao.select(id) != null;
    }

    @Override
    public User select(String id) {
        return userDao.select(id);
    }
}
