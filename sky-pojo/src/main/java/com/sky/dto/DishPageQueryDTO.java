package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
/*
* 分页查询 根据需求文档封装的接口
* */
@Data
public class DishPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;

    //分类id
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;

}
