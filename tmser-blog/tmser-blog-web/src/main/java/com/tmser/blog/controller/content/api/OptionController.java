package com.tmser.blog.controller.content.api;

import com.tmser.blog.model.dto.OptionDTO;
import com.tmser.blog.model.properties.CommentProperties;
import com.tmser.blog.model.support.BaseResponse;
import com.tmser.blog.service.ClientOptionService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Content option controller.
 *
 * @author johnniang
 * @date 2019-04-03
 */
@RestController("ApiContentOptionController")
@RequestMapping("/api/content/options")
public class OptionController {

    private final ClientOptionService optionService;

    public OptionController(ClientOptionService clientOptionService) {
        this.optionService = clientOptionService;
    }

    @GetMapping("list_view")
    public List<OptionDTO> listAll() {
        return optionService.listDtos();
    }

    @GetMapping("map_view")
    public Map<String, Object> listAllWithMapView(
            @Deprecated()
            @RequestParam(value = "key", required = false) List<String> keyList,
            @RequestParam(value = "keys", required = false) String keys) {
        // handle for key list
        if (!CollectionUtils.isEmpty(keyList)) {
            return optionService.listOptions(keyList);
        }
        // handle for keys
        if (StringUtils.hasText(keys)) {
            Set<String> nameSet = Collections.unmodifiableSet(Arrays.stream(keys.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet()));
            return optionService.listOptions(nameSet);
        }
        // list all
        return optionService.listOptions();
    }

    @GetMapping("keys/{key}")
    public BaseResponse<Object> getBy(@PathVariable("key") String key) {
        Object optionValue = optionService.getByKey(key).orElse(null);
        return BaseResponse.ok(optionValue);
    }

    @GetMapping("comment")
    @Deprecated
    public Map<String, Object> comment() {
        List<String> keys = new ArrayList<>();
        keys.add(CommentProperties.GRAVATAR_DEFAULT.getValue());
        keys.add(CommentProperties.CONTENT_PLACEHOLDER.getValue());
        keys.add(CommentProperties.GRAVATAR_SOURCE.getValue());
        return optionService.listOptions(keys);
    }

}
