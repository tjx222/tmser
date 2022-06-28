package com.tmser.blog.controller.content.model;

import com.tmser.blog.model.dto.PhotoDTO;
import com.tmser.blog.model.properties.SheetProperties;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.PhotoService;
import com.tmser.blog.service.ThemeService;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.sort.Sort;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import static com.tmser.model.sort.Sort.Direction.DESC;

/**
 * @author ryanwang
 * @date 2020-02-11
 */
@Component
public class PhotoModel {

    private final PhotoService photoService;

    private final ThemeService themeService;

    private final OptionService optionService;

    public PhotoModel(PhotoService photoService,
                      ThemeService themeService,
                      OptionService optionService) {
        this.photoService = photoService;
        this.themeService = themeService;
        this.optionService = optionService;
    }

    public String list(Integer page, Model model) {

        int pageSize = optionService.getByPropertyOrDefault(SheetProperties.PHOTOS_PAGE_SIZE,
                Integer.class,
                Integer.parseInt(SheetProperties.PHOTOS_PAGE_SIZE.defaultValue()));

        Page pageable =
                PageImpl.of(page >= 1 ? page - 1 : page, pageSize, Sort.by(DESC, "createTime"));

        Page<PhotoDTO> photos = photoService.pageBy(pageable);

        model.addAttribute("is_photos", true);
        model.addAttribute("photos", photos);
        model.addAttribute("meta_keywords", optionService.getSeoKeywords());
        model.addAttribute("meta_description", optionService.getSeoDescription());
        return themeService.render("photos");
    }
}
