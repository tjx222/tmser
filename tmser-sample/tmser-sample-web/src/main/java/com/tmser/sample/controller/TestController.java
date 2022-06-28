package com.tmser.sample.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.money.Money;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.response.ResponseData;
import com.tmser.sample.dao.IUserDao;
import com.tmser.sample.po.UserPo;
import com.tmser.sample.service.SampleService;
import com.tmser.spring.web.PageableDefault;
import com.tmser.util.ConfigUtils;
import com.tmser.util.Identities;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private IUserDao iUserDao;

    @Resource
    private SampleService sampleService;

    @GetMapping("/hello")
    public ResponseData test() {

        final String application = ConfigUtils.get("application", "server.port");
        return ResponseData.success("hello sample");
    }


    @GetMapping("/list")
    public ResponseData list(@PageableDefault(size = 3, page = 1) PageImpl page) {
        Page<UserPo> userPoIPage = MybatisPageHelper.fillPageData(
                iUserDao.selectPageVo(MybatisPageHelper.changeToMybatisPage(page), false), page);
        return ResponseData.success(userPoIPage);
    }

    @GetMapping("/listP")
    public ResponseData listP() {
        IPage<UserPo> userPoIPage = sampleService.userPage();
        return ResponseData.success(userPoIPage);
    }


    @GetMapping("/listM")
    public ResponseData listPage() {
        UserPo um = new UserPo();
        um.setName("newUser");
        PageImpl<UserPo> p = PageImpl.of(1, 3, false);
        final Page<UserPo> userPoIPage = MybatisPageHelper.fillPageData(iUserDao.selectPage(MybatisPageHelper.changeToMybatisPage(p), new QueryWrapper<UserPo>(um)), p);

        return ResponseData.success(userPoIPage);
    }

    @GetMapping("/id")
    public ResponseData id() {
        UserPo userPoIPage = iUserDao.selectByBizId("20220114100720327050000000320001");
        return ResponseData.success(userPoIPage);
    }

    @GetMapping("/ids")
    public ResponseData ids() {
        List<UserPo> userPoIPage = iUserDao.selectByBizIds(Lists.newArrayList("20220114100720327050000000320001"));
        return ResponseData.success(userPoIPage);
    }


    @GetMapping("/in")
    public ResponseData in() {
        List<UserPo> userPoIPage = iUserDao.findByNameIn(Lists.newArrayList("ab", "testUp", "cc"));
        return ResponseData.success(userPoIPage);
    }

    @GetMapping("/new")
    public ResponseData newUser() {
        UserPo u = new UserPo();
        u.setBizId(Identities.generateId());
        u.setName("newUser");
        u.setCreateTime(LocalDateTime.now());
        u.setSex(1);
        u.setAmount(new Money());
        u.setDeleted(false);
        Integer i = iUserDao.insert(u);
        return ResponseData.success(u);
    }

    @GetMapping("/name")
    public ResponseData name(String name) {
        List<UserPo> userPoIPage = iUserDao.findByName(name);
        return ResponseData.success(userPoIPage);
    }

    @GetMapping("/update")
    public ResponseData update() {
        UserPo userPo = new UserPo();
        userPo.setId(1);
        userPo.setName("testUp");
        userPo.setVersion(1);
        int rs = iUserDao.updateById(userPo);

        iUserDao.updateName("testSen", "123126");
        return ResponseData.success(rs);
    }


    @GetMapping("/config")
    public ResponseData config(String name) {
        Integer config = ConfigUtils.getInteger("testInt");
        return ResponseData.success(config);
    }


}