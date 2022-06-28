package com.tmser.sample.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tmser.sample.po.UserPo;

public interface SampleService {

    IPage<UserPo> userPage();
}
