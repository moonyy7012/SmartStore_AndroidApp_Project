package com.ssafy.smartstore.model.dao;

import com.ssafy.smartstore.model.dto.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserDao {
    /**
     * 사용자 정보를 추가한다.
     * @param user
     * @return
     */
    int insert(User user);

    /**
     * 사용자의 Stamp 정보를 수정한다.
     * @param user
     * @return
     */
    int updateStamp(User user);

    /**
     * 사용자 정보를 조회한다.
     * @param userId
     * @return
     */

    User select(String userId);

    /**
     * 사용자 정보를 삭제한다.
     * @param userId
     * @return
     */
    int delete(String userId);

    List<User> selectAll();

    int update(User user);
}
