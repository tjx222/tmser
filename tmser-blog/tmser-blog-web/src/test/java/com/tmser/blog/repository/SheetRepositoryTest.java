package com.tmser.blog.repository;


import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.model.sort.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.PrePersist;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Page repository test.
 *
 * @author johnniang
 * @date 3/22/19
 */
//@SpringBootTest
@Slf4j
class SheetRepositoryTest {

    @Autowired
    SheetRepository sheetRepository;

    @Test
    void listAllTest() {
       // long count = sheetRepository.countVisit();
        Sort idDesc = Sort.by("id");
        boolean allByStatus = sheetRepository.existsByIdNotAndSlug(2,"about");
        System.out.println("----------count: "+allByStatus);
    }

    @Test
    void testMethod() throws Exception{
        List<Method> methodsListWithAnnotation = MethodUtils.getMethodsListWithAnnotation(Sheet.class, PrePersist.class, true,true);
        System.out.println(methodsListWithAnnotation);
        Sheet s = new Sheet();
        Method method = methodsListWithAnnotation.get(0);
        method.setAccessible(true);
        method.invoke(s,null);
        System.out.println(s);
    }
}
