package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述
 *
 * @Description TODO针对套餐相关接口
 * @ClassName SetmealDishController
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api("针对套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    /*
     * day4 新增套餐
     * */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result<PageResult> Newpackage(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐数据为：{}",setmealDTO);
        setmealService.Newpackage(setmealDTO);
        return Result.success();
    }
    /*
    * 套餐分页查询
    * */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("分页查询:{}",setmealPageQueryDTO);
        setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success();
    }

    /*
    * 删除套餐
    * 业务需求：- 可以一次删除一个套餐，也可以批量删除套餐 - 起售中的套餐不能删除
    *
    * */
    @DeleteMapping
    @ApiOperation("分页查询")
    //List<Long>方式可以被MVC框架自动解析，加入@RequestParam注解后就可以被MVC动态解析提取出来id就可以被写入集合当中
    private Result delete(@RequestParam List<Long> ids){
        log.info("批量删除：{}",ids);
        setmealService.deletebatches(ids);
        return Result.success();
    }




    /**
     * 根据id查询套餐，用于修改页面回显数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("按照id查询套餐")
    public Result<SetmealVO> getId(@RequestParam Long id){
        log.info("根据id查询的数据为：{}",id);
        SetmealVO setmealVO=setmealService.getById(id);
        return Result.success(setmealVO);
    }
    /*
    * 修改套餐
    * */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改的套餐信息为：{}",setmealDTO);
        setmealService.updateWithFlavor(setmealDTO);
        return Result.success();
    }
    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        setmealService.startOrStop(status, id);
        return Result.success();
    }
}
