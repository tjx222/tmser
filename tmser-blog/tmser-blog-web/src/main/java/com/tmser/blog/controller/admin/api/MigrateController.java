package com.tmser.blog.controller.admin.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.tmser.blog.exception.BadRequestException;
import com.tmser.blog.model.enums.MigrateType;
import com.tmser.blog.model.properties.PrimaryProperties;
import com.tmser.blog.service.MigrateService;
import com.tmser.blog.service.OptionService;

/**
 * Migrate controller
 *
 * @author ryanwang
 * @date 2019-10-29
 */
@RestController
@RequestMapping("/api/admin/migrations")
public class MigrateController {

    private final MigrateService migrateService;

    private final OptionService optionService;

    public MigrateController(MigrateService migrateService,
        OptionService optionService) {
        this.migrateService = migrateService;
        this.optionService = optionService;
    }

    @PostMapping("halo")
    public void migrateHalo(@RequestPart("file") MultipartFile file) {
        if (optionService.getByPropertyOrDefault(
            PrimaryProperties.IS_INSTALLED, Boolean.class, false)) {
            throw new BadRequestException("无法在博客初始化完成之后迁移数据");
        }
        migrateService.migrate(file, MigrateType.HALO);
    }
}
