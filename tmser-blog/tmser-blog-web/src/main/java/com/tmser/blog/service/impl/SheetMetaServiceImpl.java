package com.tmser.blog.service.impl;

import com.tmser.blog.model.entity.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.entity.SheetMeta;
import com.tmser.blog.repository.SheetMetaRepository;
import com.tmser.blog.repository.SheetRepository;
import com.tmser.blog.service.SheetMetaService;

import java.util.Optional;

/**
 * Sheet meta service implementation class.
 *
 * @author ryanwang
 * @author ikaisec
 * @date 2019-08-04
 */
@Slf4j
@Service
public class SheetMetaServiceImpl extends BaseMetaServiceImpl<SheetMeta>
    implements SheetMetaService {

    private final SheetMetaRepository sheetMetaRepository;

    private final SheetRepository sheetRepository;

    public SheetMetaServiceImpl(SheetMetaRepository sheetMetaRepository,
        SheetRepository sheetRepository) {
        super(sheetMetaRepository);
        this.sheetMetaRepository = sheetMetaRepository;
        this.sheetRepository = sheetRepository;
    }

    @Override
    public void validateTarget(@NonNull Integer sheetId) {
        Optional.ofNullable(sheetRepository.selectById(sheetId))
            .orElseThrow(() -> new NotFoundException("查询不到该页面的信息").setErrorData(sheetId));
    }
}
