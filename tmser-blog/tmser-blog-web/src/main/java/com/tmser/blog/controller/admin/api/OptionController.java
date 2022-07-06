package com.tmser.blog.controller.admin.api;

import com.tmser.blog.annotation.DisableOnCondition;
import com.tmser.blog.model.dto.OptionDTO;
import com.tmser.blog.model.dto.OptionSimpleDTO;
import com.tmser.blog.model.entity.Option;
import com.tmser.blog.model.params.OptionParam;
import com.tmser.blog.model.params.OptionQuery;
import com.tmser.blog.service.OptionService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.spring.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Option Controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-20
 */
@RestController
@RequestMapping("/api/admin/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public List<OptionDTO> listAll() {
        return optionService.listDtos();
    }

    @PostMapping("saving")
    @DisableOnCondition
    public void saveOptions(@Valid @RequestBody List<OptionParam> optionParams) {
        optionService.save(optionParams);
    }

    @GetMapping("map_view")
    public Map<String, Object> listAllWithMapView() {
        return optionService.listOptions();
    }

    @PostMapping("map_view/keys")
    public Map<String, Object> listAllWithMapView(@RequestBody List<String> keys) {
        return optionService.listOptions(keys);
    }

    @GetMapping("list_view")
    public Page<OptionSimpleDTO> listAllWithListView(
            @PageableDefault(sort = "update_time, DESC") PageImpl pageable,
            OptionQuery optionQuery) {
        return optionService.pageDtosBy(pageable, optionQuery);
    }

    @GetMapping("{id:\\d+}")
    public OptionSimpleDTO getBy(@PathVariable("id") Integer id) {
        Option option = optionService.getById(id);
        return optionService.convertToDto(option);
    }

    @PostMapping
    @DisableOnCondition
    public void createBy(@RequestBody @Valid OptionParam optionParam) {
        optionService.save(optionParam);
    }

    @PutMapping("{optionId:\\d+}")
    @DisableOnCondition
    public void updateBy(@PathVariable("optionId") Integer optionId,
                         @RequestBody @Valid OptionParam optionParam) {
        optionService.update(optionId, optionParam);
    }

    @DeleteMapping("{optionId:\\d+}")
    @DisableOnCondition
    public void deletePermanently(@PathVariable("optionId") Integer optionId) {
        optionService.removePermanently(optionId);
    }

    @PostMapping("map_view/saving")
    @DisableOnCondition
    public void saveOptionsWithMapView(@RequestBody Map<String, Object> optionMap) {
        optionService.save(optionMap);
    }

}
