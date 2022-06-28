package com.tmser.database.mybatis;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

import java.util.List;

public class BizIdLogicSqlInjector extends JpaSqlInjector {

    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new SelectByBizIdMethod());
        methodList.add(new SelectByBizIsdMethod());
        return methodList;
    }
}
