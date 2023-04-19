package com.sky.service;

import com.sky.dto.DishDTO;
import org.springframework.stereotype.Service;

/**
 * 功能描述
 *
 * @Description TODO 新增菜品实现类
 * @ClassName DishService
 */
public interface DishService {
    void saveWithFlavor(DishDTO dishDTO);
}
