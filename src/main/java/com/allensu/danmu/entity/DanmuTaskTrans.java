package com.allensu.danmu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("danmu_mango_task_trans")
public class DanmuTaskTrans {
    private int id;
    private String title;
    private LocalDateTime createTime;
    private Integer count;
    private String remark;
}
