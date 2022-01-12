package com.tmser.sample.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tmser.model.response.ResponseData;
import com.tmser.sample.dao.IUserDao;
import com.tmser.sample.po.UserPo;
import com.tmser.util.ConfigUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private IUserDao iUserDao;

    @GetMapping("/hello")
    public ResponseData test(){

        final String application = ConfigUtils.get("application", "server.port");
        return ResponseData.success("hello sample");
    }


    @GetMapping("/list")
    public ResponseData list(){
        IPage<UserPo> userPoIPage = iUserDao.selectPageVo(Page.of(0, 20), false);
        return ResponseData.success(userPoIPage);
    }

    @GetMapping("/name")
    public ResponseData name(String name){
        List<UserPo> userPoIPage = iUserDao.findByName(name);
        return ResponseData.success(userPoIPage);
    }

    @GetMapping("/update")
    public ResponseData update(){
        UserPo userPo = new UserPo();
        userPo.setId(1);
        userPo.setName("testUp");
        userPo.setVersion(0);
        int rs = iUserDao.updateById(userPo);
        return ResponseData.success(rs);
    }


}
