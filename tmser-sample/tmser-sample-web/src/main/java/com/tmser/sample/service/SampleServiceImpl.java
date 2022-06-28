package com.tmser.sample.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tmser.sample.dao.IUserDao;
import com.tmser.sample.po.UserPo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class SampleServiceImpl implements SampleService {

    @Resource
    private IUserDao iUserDao;

    @Transactional
    public IPage<UserPo> userPage() {
        Page page = Page.of(1, 2, false);
        int i = 1;
        while (true) {
            final Page<UserPo> userPage = (Page<UserPo>) iUserDao.selectPage(page, null);

            page.setCurrent(2);
            i++;
            if (i > 2) {
                break;
            }
        }
        return null;
    }
}
