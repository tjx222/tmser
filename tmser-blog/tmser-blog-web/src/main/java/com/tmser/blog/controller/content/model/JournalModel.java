package com.tmser.blog.controller.content.model;

import com.tmser.blog.model.entity.Journal;
import com.tmser.blog.model.enums.JournalType;
import com.tmser.blog.model.properties.SheetProperties;
import com.tmser.blog.service.JournalService;
import com.tmser.blog.service.OptionService;
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
public class JournalModel {

    private final JournalService journalService;

    private final OptionService optionService;

    private final ThemeService themeService;

    public JournalModel(JournalService journalService,
                        OptionService optionService,
                        ThemeService themeService) {
        this.journalService = journalService;
        this.optionService = optionService;
        this.themeService = themeService;
    }

    public String list(Integer page, Model model) {

        int pageSize = optionService
                .getByPropertyOrDefault(SheetProperties.JOURNALS_PAGE_SIZE, Integer.class,
                        Integer.parseInt(SheetProperties.JOURNALS_PAGE_SIZE.defaultValue()));

        PageImpl pageable =
                PageImpl.of(page >= 1 ? page - 1 : page, pageSize, Sort.by(DESC, "createTime"));

        Page<Journal> journals = journalService.pageBy(JournalType.PUBLIC, pageable);

        model.addAttribute("is_journals", true);
        model.addAttribute("journals", journalService.convertToCmtCountDto(journals));
        model.addAttribute("meta_keywords", optionService.getSeoKeywords());
        model.addAttribute("meta_description", optionService.getSeoDescription());
        return themeService.render("journals");
    }
}
