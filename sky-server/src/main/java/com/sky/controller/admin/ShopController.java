package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 功能描述
 *
 * @Description TODO 设置店铺的营业状态
 * @ClassName ShopController
 */
@RestController("adminShopController")//因为用户端和管理端的接口默认名一样，所以我们需要给他们修改默认名
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺的营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺的营业状态")
    //根据项目要求，date不是必须的，所以不用设置泛型
    public Result setStatus(@PathVariable Integer status){
        //日志也可以动态设置输出信息
        log.info("设置店铺的营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        //因为状态信息的特性推荐存入redis数据库，所以这里调用配置好的redisTemplate对象来对数据库进行操作
        //此时利用对象的方法就可以把店铺的营业状态传输进redis库中成为键值对，后期再调用即可
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }
    /**
     * 获取店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取到店铺的营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
