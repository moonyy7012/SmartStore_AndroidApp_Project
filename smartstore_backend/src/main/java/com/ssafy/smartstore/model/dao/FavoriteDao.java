package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.Favorite;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface FavoriteDao {
    int insert(Favorite favorite);

    List<Map<String, Object>> select(String userId);

    int delete(String id);
}
