package com.tmser.sample.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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
public class UserPo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String bizId;

    @SensitiveField
    private String name;

    private Integer sex;

    private Boolean deleted;

    private Money amount;

    private LocalDateTime createTime;

    @Version
    private Integer version;
}
