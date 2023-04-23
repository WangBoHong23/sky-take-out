package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 功能描述
 *
 * @Description TODO 新增菜品和对应的口味
 * @ClassName DishServiceImpl
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper  setmealDishMapper;


                                /**
                                 * 新增开发
                                 * 根据id修改菜品基本信息和对应的口味信息
                                 *
                                 * @param dishDTO
                                 */
                         //
    //思路:如果口味表Dish_Flavor的数据修改的话就很麻烦，所以我们可以直接全部删除口味信息，根据前端传来的值，重新插入进去就好
    //而菜品信息Dish，就可以根据前端传来的数据库原有信息在回显状态下就可以根据业务需求进行更改
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //修改菜品表基本信息
        //因为业务需求，最好把sql语句写成动态的，当dish复制过来的对象有值才会进行修改
        //而修改的时候最好添加修改的时间和修改人@AutoFill(value = OperationType.UPDATE)写好的自定义注解就可以自动修改
        dishMapper.update(dish);

        //删除原有的口味数据
        //在删除操作的时候已经写过‘根据id删除口味信息’，所以直接调用就好
        //因为前端修改的数据已经封装到dishDTO的对象中了，所以可以直接调取
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //重新插入口味数据
        //现在只需要把口味数据遍历插入到口味对象中
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //判断前端传来的数据满足要求，在批量删除之前要修改dishDTO.id
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n条数据
            //新增数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

                                /**
                                * 根据id查询菜品和对应的口味数据
                                *
                                * @param id
                                * @return
                                */
                      //在修改业务主要做回显功能
    //思路:查询菜品数据和口味数据把他们一起封装到DishVo数据中去，'回显'dishVo对象
    public DishVO getByIdWithFlavor(Long id) {

        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味数据
        //因为查询id时可能会返回多个值所以对象类型为List集合
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //将查询到的数据封装到VO
        DishVO dishVO = new DishVO();
        //利用工具类把dish拷贝到disVo对象中
        //vo中categoryName分类名称在修改时这个属性不是必须的，所以可以为null
        BeanUtils.copyProperties(dish, dishVO);
        //再把对象dishFlavors set 方法 存入dishVO 的Flavors属性集合中去
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

                            /*
                            * 新增菜品和对应的口味数据
                            * */
    //业务规则:
          //- 菜品名称必须是唯一的
          //- 菜品必须属于某个分类下，不能单独存在
          //- 新增菜品时可以根据情况选择菜品的口味
          //- 每个菜品必须对应一张图片

   //接口设计: 1.- 根据类型查询分类（已完成）   2.文件上传    3.新增菜品

        //思路:接口2：文件上传功能在后期开发中有可能被多个功能调用，所以直接编写在通用接口CommonController中，直接调用即可，而数据库不适合存入
        //图片类型，所以使用第三方存储方式：阿里云，而数据库只需要存储文件的路径信息即可
        //接口3：新增菜品时需要同时传入菜品信息和菜品口味信息，涉及到用两个表dish和dish_flavor，所以我们可直接封装一个DishDTO对象来接收前端传来的数据
        //而在业务层我们需要分解前端传来的数据分别传输进dish和dish_flavors表中，针对两个表进封装了dish和DishFlavor对象。
    @Transactional
    //多个表参与，需要事务控制
    public void saveWithFlavor(DishDTO dishDTO) {
    // DishDTO 刚好可以对应菜品需求
        //1.向dish表填充数据
        //1.1 属性拷贝到dish 这个pojo
        Dish dish = new Dish();
        //向菜品表   Dish   插入1条数据

        //此时dish对象没有数据，可以利用BeanUtils工具类把前端传输过来的dishDTO对象中的值在对象名规范的前提下进行copy
        BeanUtils.copyProperties(dishDTO,dish);

        //1.2 调用mapper插入到数据库(支持公共宇段填充，支持主键回显)
        //只是插入菜品对象
        dishMapper.insert(dish);

        //获取insert语句生成的主键值，在DishMapper.xml中设置主键返回
        Long dishId = dish.getId();

        //2.向dish_flavor表插入数据
        // 2.1给 每个dishFLavor填充dish_id属性
        //dishDTO.getFlavors() :拿到dishDTO对象中集合的数据
        List<DishFlavor> flavors = dishDTO.getFlavors();

        //判断口味数据是否提交上来了
        if (flavors != null && flavors.size() > 0) {
            //通过遍历每个dishFLavor填充dish_id属性
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //2.2 批量入库即可
            //向口味表插入n条数据 （操作口味表）直接传入集合
            dishFlavorMapper.insertBatch(flavors);
        }
    }


                                       /**
                                        * 菜品分页查询
                                        *
                                        * @param dishPageQueryDTO
                                        * @return
                                        */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //利用PageHelper调用startPage方法指定从第几页开始，每页的数据量
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }


                                     /**
                                     * 菜品批量删除
                                     *涉及的表  dish菜品表 dish_flavor菜品口味关系表 setmeal_dish套餐菜品关系表
                                      * 当删除删除菜品之前会判断是否被某个套餐关联setmeal_dish，
                                      * 假设菜品能够删除，对应的口味数据也会被删除dish_flavor
                                     * @param ids
                                     */
    @Transactional//事务
    public void deleteBatch(List<Long> ids) {
        //思路1.判断当前菜品是否能够删除---是否存在起售中的菜品？？
        //遍历每个菜品名
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            //StatusConstant定义了两个常量 1& 0；
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //当前菜品处于起售中，不能删除          DISH_ON_SALE  =   起售中的菜品不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //2.判断当前菜品是否能够删除---是否被套餐关联了？？
        //setmealDishMapper:查询关系表单独编写的mapper表
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //3.当前菜品被套餐关联了，不能删除              DISH_BE_RELATED_BY_SETMEAL  =  当前菜品关联了套餐,不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //4.删除菜品表中的菜品数据（根据主键来进行删除）
        //因为传入的是集合参数，又可能删除的不止一个，所以需要遍历
        for (Long id : ids) {
            //走了两个条件判断了直接就可以进行尝试删除
            dishMapper.deleteById(id);
            //4.1.删除菜品关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
    }


    /*
    * 根据分类id查询菜品
    * */
    @Override
    public List<Dish> QueryDishesByCategory(Long categoryid) {
        //利用Dish中的builder注解动态设置dish中的值，修改玩以build结尾
        Dish dish = Dish.builder().categoryId(categoryid).status(StatusConstant.ENABLE).build();
        return dishMapper.list(dish);
    }
}