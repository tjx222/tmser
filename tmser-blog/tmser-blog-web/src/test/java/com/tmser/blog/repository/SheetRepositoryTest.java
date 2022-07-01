package com.tmser.blog.repository;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Page repository test.
 *
 * @author johnniang
 * @date 3/22/19
 */
@SpringBootTest
@Slf4j
class SheetRepositoryTest {

    @Autowired
    SheetRepository sheetRepository;

    @Test
    void listAllTest() {
        long count = sheetRepository.countVisit();
        System.out.println("----------count: "+count);
    }
}
