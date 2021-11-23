package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dto.Favorite;

import java.util.List;
import java.util.Map;

public interface FavoriteService {
    int addFavoriteMenu(Favorite favorite);

    List<Map<String, Object>> getFavoriteMenuById(String userId);

    int delete(String id);
}
