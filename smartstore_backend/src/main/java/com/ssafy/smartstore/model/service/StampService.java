package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dto.Stamp;

import java.util.List;

public interface StampService {
    /**
     * id 사용자의 Stamp 이력을 반환한다.
     * @param id
     * @return
     */
    List<Stamp> selectByUser(String id);
}
