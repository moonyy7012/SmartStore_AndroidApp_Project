package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.FavoriteDao;
import com.ssafy.smartstore.model.dto.Favorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteDao favoriteDao;

    @Override
    public int addFavoriteMenu(Favorite favorite) {
        return favoriteDao.insert(favorite);
    }

    @Override
    public List<Map<String, Object>> getFavoriteMenuById(String userId) {
        return favoriteDao.select(userId);
    }

    @Override
    public int delete(String id) {
        return favoriteDao.delete(id);
    }
}
