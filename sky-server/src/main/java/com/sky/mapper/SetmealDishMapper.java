package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 功能描述
 *
 * @Description TODO
 * @ClassName SetmealDishMapper
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询对应的套餐id
     *
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
    //根据菜品id查询套餐id，多对对的关系，有可能查出多个套餐，所以是集合返回值
    //传输的参数为菜品的id
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);


    /**
     * 批量保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertdata(List<SetmealDish> setmealDishes);


    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param
     */
    @Delete("delete * from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long id);



}
