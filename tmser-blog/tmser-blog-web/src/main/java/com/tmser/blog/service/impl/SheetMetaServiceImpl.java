package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.model.entity.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.entity.SheetMeta;
import com.tmser.blog.repository.SheetMetaRepository;
import com.tmser.blog.repository.SheetRepository;
import com.tmser.blog.service.SheetMetaService;

import java.util.List;
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

    private final SheetRepository sheetRepository;

    private final SheetMetaRepository sheetMetaRepository;

    public SheetMetaServiceImpl(SheetMetaRepository sheetMetaRepository,
        SheetRepository sheetRepository) {
        super(sheetMetaRepository);
        this.sheetMetaRepository = sheetMetaRepository;
        this.sheetRepository = sheetRepository;
    }

    /**
     * List All
     *
     * @return List
     */
    @Override
    public List<SheetMeta> listAll() {
        return sheetMetaRepository.selectList(new QueryWrapper<SheetMeta>().eq(true,"type", SheetMeta.MT_SHEET));
    }


    @Override
    public void validateTarget(@NonNull Integer sheetId) {
        Optional.ofNullable(sheetRepository.selectById(sheetId))
            .orElseThrow(() -> new NotFoundException("查询不到该页面的信息").setErrorData(sheetId));
    }
}
