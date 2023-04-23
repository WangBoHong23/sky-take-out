package com.sky.controller.admin;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * 功能描述
 *
 * @Description TODO 菜品管理
 * @ClassName DishController
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /*
    *  day4 根据分类id查询菜品
    * */
    //查询必须有返回值，而返回值刚好可以被dish对象接收
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryid){
        log.info("根据分类id查询菜品：{}",categoryid);
        List<Dish> dish=dishService.QueryDishesByCategory(categoryid);
        return Result.success(dish);
    }


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        //接受前端的数据 DishDTO中的值跟需求文档对应
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }




    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    //分页查询统一返回的是分装的PageResult对象(分装了总记录数和当前页数据的集合)
    //DishPageQueryDTO 封装了前端传输过来的数据 ，因为请求的参数是Query的方式，所以不需要json包装注解 @RequestBody
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        //查询类方法是带有返回值的，所以返回值需要封装到PageResult对象中
        return Result.success(pageResult);
    }




                                    /**
                                    * 菜品批量删除
                                    *
                                    * @param ids
                                    * @return
                                    */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    //List<Long>方式可以被MVC框架自动解析，加入@RequestParam注解后就可以被MVC动态解析提取出来id就可以被写入集合当中
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }


                            /**
                             * 根据id查询菜品
                             *
                             * @param id
                             * @return
                             */
                     //在修改时根据业务需求做回显功能
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    //@PathVariable:路径参数
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }


                            /**
                             *接口设计：
                             *      根据id查询菜品
                             *      根据类型查询分类（以实现）
                             *      文件上传（已实现）
                             *      修改菜品
                             *
                             * 修改菜品
                             *
                             * @param dishDTO
                             * @return
                             */
    @PutMapping
    @ApiOperation("修改菜品")
    //需求文档要求为json格式@RequestBody
    //
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        //修改菜品和关联的口味
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }
}
