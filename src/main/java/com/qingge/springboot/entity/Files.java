package com.qingge.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


//存放上传和下载的文件信息
@Data
@TableName("sys_file")
public class Files {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String type;
    private Long size;
    private String url;
    private String md5;
    private Boolean isDelete;
    private Boolean enable;

}
