package com.ssafy.smartstore.controller.rest;

import com.ssafy.smartstore.model.dto.Favorite;
import com.ssafy.smartstore.model.service.FavoriteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/favorite")
@CrossOrigin
public class FavoriteMenuController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/{userId}")
    @ApiOperation(value="사용자별로 선호하는 메뉴를 반환합니다.")
    @Transactional
    public List<Map<String, Object>> getFavoriteMenuById(@PathVariable String userId) {
        return favoriteService.getFavoriteMenuById(userId);
    }

    @PostMapping
    @ApiOperation(value = "선호 메뉴를 새롭게 등록합니다.")
    @Transactional
    public int addFavoriteMenu(@RequestBody Favorite favorite) {
        return favoriteService.addFavoriteMenu(favorite);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "선호 목록에서 메뉴를 제거합니다.")
    @Transactional
    public int deleteMenu(String id) {
        return favoriteService.delete(id);
    }

}
