package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
//                             序列化接口，规范要求，不实现容易出现问题
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
