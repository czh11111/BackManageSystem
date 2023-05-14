package com.qingge.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


//存放menu的图标
@TableName("sys_dict")
@Data
public class Dict {

    private String name;
    private String value;
    private String type;

}
