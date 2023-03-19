package com.allensu.danmu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("danmu_mango_details")
public class DanmuEntity {
    private int id;
    private String ids;
    private int uid;
    private String uuid;
    @TableField("int_time")
    private int time;
    private String content;
    private String title;
}
