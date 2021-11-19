package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.Stamp;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StampDao {
    int insert(Stamp stamp);

    Stamp select(Integer stampId);

    List<Stamp> selectAll();

    List<Stamp> selectByUserId(String userId);

}
