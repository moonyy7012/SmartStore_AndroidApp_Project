package com.ssafy.smartstore.model.service;

import com.ssafy.smartstore.model.dao.StampDao;
import com.ssafy.smartstore.model.dto.Stamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StampServiceImpl implements StampService{

    @Autowired
    StampDao stampDao;

    @Override
    public List<Stamp> selectByUser(String id) {
        return stampDao.selectByUserId(id);
    }
}
