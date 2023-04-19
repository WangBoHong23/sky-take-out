package com.sky.mapper;

/**
 * 功能描述
 *
 * @Description TODO
 * @ClassName DishFlavorMapper
 */
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

}
