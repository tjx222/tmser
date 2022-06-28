package com.tmser.blog.controller.admin.api;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.tmser.blog.model.params.StaticContentParam;
import com.tmser.blog.model.support.StaticFile;
import com.tmser.blog.service.StaticStorageService;

/**
 * Static storage controller.
 *
 * @author ryanwang
 * @date 2019-12-06
 */
@RestController
@RequestMapping("/api/admin/statics")
public class StaticStorageController {

    private final StaticStorageService staticStorageService;

    public StaticStorageController(StaticStorageService staticStorageService) {
        this.staticStorageService = staticStorageService;
    }

    @GetMapping
    public List<StaticFile> list() {
        return staticStorageService.listStaticFolder();
    }

    @DeleteMapping
    public void deletePermanently(@RequestParam("path") String path) {
        staticStorageService.delete(path);
    }

    @PostMapping
    public void createFolder(String basePath,
        @RequestParam("folderName") String folderName) {
        staticStorageService.createFolder(basePath, folderName);
    }

    @PostMapping("upload")
    public void upload(String basePath,
        @RequestPart("file") MultipartFile file) {
        staticStorageService.upload(basePath, file);
    }

    @PostMapping("rename")
    public void rename(String basePath,
        String newName) {
        staticStorageService.rename(basePath, newName);
    }

    @PutMapping("files")
    public void save(@RequestBody StaticContentParam param) {
        staticStorageService.save(param.getPath(), param.getContent());
    }
}
