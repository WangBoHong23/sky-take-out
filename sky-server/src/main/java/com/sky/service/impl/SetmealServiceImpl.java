package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 功能描述
 *
 * @Description TODO 针对套餐表进行修改
 * @ClassName SetmealServiceImpl
 */
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    //利用SetmealPageQueryDTO封装前端写入的数据，再返回通过PageResult中封装的total总记录数和records当前页数据集合属性返回值
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        //因为查询的套餐可能跟其他表（其中有菜品）有关系，所以需要把套餐中的菜品显现出来？
        Page<SetmealVO> page=setmealMapper.pageQuery(setmealPageQueryDTO);
        //返回PageResult对象中的total总记录数和records当前页数据
        return new PageResult(page.getTotal(),page.getResult());

    }

    /*
    * 批量删除
    * 套餐涉及的表为setmaeal 和 setmaeal_dish表
    * 当删除套餐之前会判断是否被某个套餐关联setmaeal，
    * 假设套餐能够删除，对应的套餐菜品关系也会被删除setmaeal
    * */
    @Transactional//开启事务
    public void deletebatches(List<Long> ids) {

        //思路1.判断当前菜品是否能够删除---是否存在起售中的菜品？？
        //遍历每个菜品名
        for(Long id : ids){
            Setmeal setmeal = setmealMapper.getById(id);
        //StatusConstant定义了两个常量 1& 0；
        if (setmeal.getStatus()== StatusConstant.ENABLE){
        //当前菜品处于起售中，不能删除          DISH_ON_SALE  =   起售中的菜品不能删除
         throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        }

        //4.删除套餐表中的数据（根据主键来进行删除）
        //因为传入的是集合参数，又可能删除的不止一个，所以需要遍历
        for(Long id:ids){
        //走了一个条件判断了直接就可以进行尝试删除
            setmealMapper.deleteById(id);
        //4.1.删除套餐菜品关系表中的数据（因为是针对关系表的，所以在setmealDishMapper中添加方法）
            setmealDishMapper.deleteBySetmealId(id);

        }
    }


    /**
     * 根据id查询套餐和套餐菜品关系
     *
     * @param id
     * @return
     */
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO=setmealMapper.getByIdWithDish(id);
        return setmealVO;
    }

    /*
     * 修改套餐
     * */
    @Transactional//对多个表单操作一定要添加此注解
    public void updateWithFlavor(SetmealDTO setmealDTO) {
        //创建Setmeal对象
        Setmeal setmeal = new Setmeal();
        //把前端修改的setmaeal对应的值赋值进去，后面单独调用方法添加进setmeal表单
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //获取DTO对象套餐id
        Long setmealDTOId = setmealDTO.getId();
        //删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        setmealDishMapper.deleteBySetmealId(setmealDTOId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDTOId);
        });
        //3、重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
        setmealDishMapper.insertdata(setmealDishes);

    }


    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
        if(status == StatusConstant.ENABLE){
            //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if(dishList != null && dishList.size() > 0){
                dishList.forEach(dish -> {
                    if(StatusConstant.DISABLE == dish.getStatus()){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.insert(setmeal);
    }

    /*
     * day4 新增套餐
     * */
    @Transactional//多表打开来事务
    public void Newpackage(SetmealDTO setmealDTO) {
        Setmeal setmeal=new Setmeal();
        //复制setmeal表所需要的内容
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        //获取setmealdishes对象
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //遍历添加id
        if(setmealDishes !=null && setmealDishes.size() >0){
            setmealDishes.forEach(set ->{
                set.setDishId(setmealDTO.getId());
            });
            //针对setmealdishes表添加内容
        setmealDishMapper.insertdata(setmealDishes);
        }




    }
}
