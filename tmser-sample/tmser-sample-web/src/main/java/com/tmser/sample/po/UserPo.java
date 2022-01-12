package com.tmser.sample.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tmser.database.IOptimisticLock;
import com.tmser.model.money.Money;
import com.tmser.sensitive.SensitiveClass;
import com.tmser.sensitive.SensitiveField;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ToString
@TableName("user")
@SensitiveClass
public class UserPo implements IOptimisticLock, Serializable {

    private Integer id;

    private String bizId;

    @SensitiveField
    private String name;

    private Integer sex;

    private Boolean deleted;

    private Money amount;

    private LocalDateTime createTime;

    private Integer version;
}
