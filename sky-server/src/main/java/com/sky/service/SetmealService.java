package com.sky.service;

import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;

import java.util.List;


/**
 * 功能描述
 *
 * @Description TODO 针对套餐进行修改
 * @ClassName SetmealService
 */

public interface SetmealService {
    /*
     * day4 新增套餐,同时需要保存套餐和菜品的关联关系
     * */
    void Newpackage(SetmealDTO setmealDTO);

    /*
    *分页查询
    * */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);


    /*
    * 批量删除
    * */
    @Delete("delete * from setmeal where id =#{id}")
    void deletebatches(List<Long> ids);



    /**
     * 根据id查询套餐和关联的菜品数据
     * @param id
     * @return
     */
    SetmealVO getById(Long id);



    /*
     * 修改套餐
     * */
    void updateWithFlavor(SetmealDTO setmealDTO);


    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}
