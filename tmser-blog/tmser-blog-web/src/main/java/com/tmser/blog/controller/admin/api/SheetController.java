package com.tmser.blog.controller.admin.api;

import com.tmser.blog.cache.AbstractStringCacheStore;
import com.tmser.blog.model.dto.IndependentSheetDTO;
import com.tmser.blog.model.dto.post.BasePostDetailDTO;
import com.tmser.blog.model.dto.post.BasePostMinimalDTO;
import com.tmser.blog.model.entity.Sheet;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.params.PostContentParam;
import com.tmser.blog.model.params.SheetParam;
import com.tmser.blog.model.vo.SheetDetailVO;
import com.tmser.blog.model.vo.SheetListVO;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.SheetService;
import com.tmser.blog.utils.HaloUtils;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.spring.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Sheet controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 19-4-24
 */
@RestController
@RequestMapping("/api/admin/sheets")
public class SheetController {

    private final SheetService sheetService;

    private final AbstractStringCacheStore cacheStore;

    private final OptionService optionService;

    public SheetController(SheetService sheetService,
                           AbstractStringCacheStore cacheStore,
                           OptionService optionService) {
        this.sheetService = sheetService;
        this.cacheStore = cacheStore;
        this.optionService = optionService;
    }

    @GetMapping("{sheetId:\\d+}")
    public SheetDetailVO getBy(@PathVariable("sheetId") Integer sheetId) {
        Sheet sheet = sheetService.getWithLatestContentById(sheetId);
        return sheetService.convertToDetailVo(sheet);
    }

    @GetMapping
    public Page<SheetListVO> pageBy(
            @PageableDefault(sort = "create_time,DESC") PageImpl pageable) {
        Page<Sheet> sheetPage = sheetService.pageBy(pageable);
        return sheetService.convertToListVo(sheetPage);
    }

    @GetMapping("independent")
    public List<IndependentSheetDTO> independentSheets() {
        return sheetService.listIndependentSheets();
    }

    @PostMapping
    public SheetDetailVO createBy(@RequestBody @Valid SheetParam sheetParam,
                                  @RequestParam(value = "autoSave", required = false, defaultValue = "false")
                                          Boolean autoSave) {
        Sheet sheet =
                sheetService.createBy(sheetParam.convertTo(), sheetParam.getSheetMetas(), autoSave);
        return sheetService.convertToDetailVo(sheet);
    }

    @PutMapping("{sheetId:\\d+}")
    public SheetDetailVO updateBy(
            @PathVariable("sheetId") Integer sheetId,
            @RequestBody @Valid SheetParam sheetParam,
            @RequestParam(value = "autoSave", required = false, defaultValue = "false")
                    Boolean autoSave) {
        Sheet sheetToUpdate = sheetService.getWithLatestContentById(sheetId);

        sheetParam.update(sheetToUpdate);

        Sheet sheet = sheetService.updateBy(sheetToUpdate, sheetParam.getSheetMetas(), autoSave);

        return sheetService.convertToDetailVo(sheet);
    }

    @PutMapping("{sheetId:\\d+}/{status}")
    public void updateStatusBy(
            @PathVariable("sheetId") Integer sheetId,
            @PathVariable("status") PostStatus status) {
        Sheet sheet = sheetService.getById(sheetId);

        // Set status
        sheet.setStatus(status);

        // Update
        sheetService.update(sheet);
    }

    @PutMapping("{sheetId:\\d+}/status/draft/content")
    public BasePostDetailDTO updateDraftBy(
            @PathVariable("sheetId") Integer sheetId,
            @RequestBody PostContentParam contentParam) {
        Sheet sheetToUse = sheetService.getById(sheetId);
        String formattedContent = contentParam.decideContentBy(sheetToUse.getEditorType());

        // Update draft content
        Sheet sheet = sheetService.updateDraftContent(formattedContent,
                contentParam.getOriginalContent(), sheetId);
        return sheetService.convertToDetail(sheet);
    }

    @DeleteMapping("{sheetId:\\d+}")
    public SheetDetailVO deleteBy(@PathVariable("sheetId") Integer sheetId) {
        Sheet sheet = sheetService.removeById(sheetId);
        return sheetService.convertToDetailVo(sheet);
    }

    @GetMapping("preview/{sheetId:\\d+}")
    public String preview(@PathVariable("sheetId") Integer sheetId)
            throws UnsupportedEncodingException {
        Sheet sheet = sheetService.getById(sheetId);

        sheet.setSlug(URLEncoder.encode(sheet.getSlug(), StandardCharsets.UTF_8.name()));

        BasePostMinimalDTO sheetMinimalDTO = sheetService.convertToMinimal(sheet);

        String token = HaloUtils.simpleUUID();

        // cache preview token
        cacheStore.putAny(token, token, 10, TimeUnit.MINUTES);

        StringBuilder previewUrl = new StringBuilder();

        if (!optionService.isEnabledAbsolutePath()) {
            previewUrl.append(optionService.getBlogBaseUrl());
        }

        previewUrl.append(sheetMinimalDTO.getFullPath())
                .append("?token=")
                .append(token);

        // build preview post url and return
        return previewUrl.toString();
    }
}
