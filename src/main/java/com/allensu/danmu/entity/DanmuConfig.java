package com.allensu.danmu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("danmu_config_mango")
public class DanmuConfig {
    private int id;
    private String title;
    private String program;
    private int duration;
    private String url;
    private LocalDateTime nextExecuteTime;
    private LocalDateTime updateTime;
    private String remark;
}
