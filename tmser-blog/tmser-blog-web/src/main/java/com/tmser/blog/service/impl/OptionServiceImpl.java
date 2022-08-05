package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiniu.storage.Region;
import com.tmser.blog.cache.AbstractStringCacheStore;
import com.tmser.blog.event.options.OptionUpdatedEvent;
import com.tmser.blog.model.dto.OptionSimpleDTO;
import com.tmser.blog.model.entity.Option;
import com.tmser.blog.model.enums.PostPermalinkType;
import com.tmser.blog.model.enums.SheetPermalinkType;
import com.tmser.blog.model.params.OptionParam;
import com.tmser.blog.model.params.OptionQuery;
import com.tmser.blog.model.properties.*;
import com.tmser.blog.repository.OptionRepository;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.utils.DateUtils;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.blog.utils.ValidationUtils;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * OptionService implementation class
 *
 * @author ryanwang
 * @author johnniang
 * @date 2019-03-14
 */
@Slf4j
@Service
public class OptionServiceImpl extends AbstractCrudService<Option, Integer>
        implements OptionService {

    private final OptionRepository optionRepository;
    private final ApplicationContext applicationContext;
    private final AbstractStringCacheStore cacheStore;
    private final Map<String, PropertyEnum> propertyEnumMap;
    private final ApplicationEventPublisher eventPublisher;

    public OptionServiceImpl(OptionRepository optionRepository,
                             ApplicationContext applicationContext,
                             AbstractStringCacheStore cacheStore,
                             ApplicationEventPublisher eventPublisher) {
        super(optionRepository);
        this.optionRepository = optionRepository;
        this.applicationContext = applicationContext;
        this.cacheStore = cacheStore;
        this.eventPublisher = eventPublisher;

        propertyEnumMap = Collections.unmodifiableMap(PropertyEnum.getValuePropertyEnumMap());
    }


    @Override
    @Transactional
    public void save(Map<String, Object> optionMap) {
        if (CollectionUtils.isEmpty(optionMap)) {
            return;
        }

        Map<String, Option> optionKeyMap = ServiceUtils.convertToMap(listAll(), Option::getOptionKey);

        List<Option> optionsToCreate = new LinkedList<>();
        List<Option> optionsToUpdate = new LinkedList<>();

        optionMap.forEach((key, value) -> {
            Option oldOption = optionKeyMap.get(key);
            if (oldOption == null || !StringUtils.equals(oldOption.getOptionValue(), value.toString())) {
                OptionParam optionParam = new OptionParam();
                optionParam.setOptionKey(key);
                optionParam.setOptionValue(value.toString());
                ValidationUtils.validate(optionParam);

                if (oldOption == null) {
                    // Create it
                    optionsToCreate.add(optionParam.convertTo());
                } else if (!StringUtils.equals(oldOption.getOptionValue(), value.toString())) {
                    // Update it
                    optionParam.update(oldOption);
                    optionsToUpdate.add(oldOption);
                }
            }
        });

        // Update them
        updateInBatch(optionsToUpdate);

        // Create them
        createInBatch(optionsToCreate);

        if (!CollectionUtils.isEmpty(optionsToUpdate)
                || !CollectionUtils.isEmpty(optionsToCreate)) {
            // If there is something changed
            eventPublisher.publishEvent(new OptionUpdatedEvent(this));
        }

    }

    @Override
    public void save(List<OptionParam> optionParams) {
        if (CollectionUtils.isEmpty(optionParams)) {
            return;
        }

        Map<String, Object> optionMap =
                ServiceUtils.convertToMap(optionParams, OptionParam::getOptionKey, OptionParam::getOptionValue);
        save(optionMap);
    }

    @Override
    public void save(OptionParam optionParam) {
        Option option = optionParam.convertTo();
        create(option);
        eventPublisher.publishEvent(new OptionUpdatedEvent(this));
    }

    @Override
    public void update(Integer optionId, OptionParam optionParam) {
        Option optionToUpdate = getById(optionId);
        optionParam.update(optionToUpdate);
        update(optionToUpdate);
        eventPublisher.publishEvent(new OptionUpdatedEvent(this));
    }

    @Override
    public void saveProperty(PropertyEnum property, String value) {
        Assert.notNull(property, "Property must not be null");

        save(Collections.singletonMap(property.getValue(), value));
    }

    @Override
    public void saveProperties(Map<? extends PropertyEnum, String> properties) {
        if (CollectionUtils.isEmpty(properties)) {
            return;
        }

        Map<String, Object> optionMap = new LinkedHashMap<>();

        properties.forEach((property, value) -> optionMap.put(property.getValue(), value));

        save(optionMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> listOptions() {
        // Get options from cache
        return cacheStore.getAny(OPTIONS_KEY, Map.class).orElseGet(() -> {
            List<Option> options = listAll();

            Set<String> keys = ServiceUtils.fetchProperty(options, Option::getOptionKey);

            Map<String, Object> userDefinedOptionMap =
                    ServiceUtils.convertToMap(options, Option::getOptionKey, option -> {
                        String key = option.getOptionKey();

                        PropertyEnum propertyEnum = propertyEnumMap.get(key);

                        if (propertyEnum == null) {
                            return option.getOptionValue();
                        }

                        return PropertyEnum.convertTo(option.getOptionValue(), propertyEnum);
                    });

            Map<String, Object> result = new HashMap<>(userDefinedOptionMap);

            // Add default property
            propertyEnumMap.keySet()
                    .stream()
                    .filter(key -> !keys.contains(key))
                    .forEach(key -> {
                        PropertyEnum propertyEnum = propertyEnumMap.get(key);

                        if (StringUtils.isBlank(propertyEnum.defaultValue())) {
                            return;
                        }

                        result.put(key,
                                PropertyEnum.convertTo(propertyEnum.defaultValue(), propertyEnum));
                    });

            // Cache the result
            cacheStore.putAny(OPTIONS_KEY, result);

            return result;
        });
    }

    @Override
    public Page<OptionSimpleDTO> pageDtosBy(Page pageable, OptionQuery optionQuery) {
        Assert.notNull(pageable, "Page info must not be null");

        QueryWrapper<Option> wrapper = new QueryWrapper<>();
        if (optionQuery.getType() != null) {
            wrapper.eq("type", optionQuery.getType());
        }

        if (optionQuery.getKeyword() != null) {
            String likeCondition = StringUtils.strip(optionQuery.getKeyword());
            wrapper.and(wp -> wp.like("value", likeCondition)
                    .or().like("key", likeCondition));
        }
        Page<Option> optionPage = MybatisPageHelper.fillPageData(
                optionRepository.selectPage(MybatisPageHelper.changeToMybatisPage(pageable), wrapper), pageable);
        return optionPage.convert(this::convertToDto);
    }

    @Override
    public Option removePermanently(Integer id) {
        Option deletedOption = removeById(id);
        eventPublisher.publishEvent(new OptionUpdatedEvent(this));
        return deletedOption;
    }

    @Override
    public int getPostPageSize() {
        try {
            return getByPropertyOrDefault(PostProperties.INDEX_PAGE_SIZE, Integer.class,
                    DEFAULT_POST_PAGE_SIZE);
        } catch (NumberFormatException e) {
            log.error(PostProperties.INDEX_PAGE_SIZE.getValue() + " option is not a number format",
                    e);
            return DEFAULT_POST_PAGE_SIZE;
        }
    }

    @Override
    public int getArchivesPageSize() {
        try {
            return getByPropertyOrDefault(PostProperties.ARCHIVES_PAGE_SIZE, Integer.class,
                    DEFAULT_ARCHIVES_PAGE_SIZE);
        } catch (NumberFormatException e) {
            log.error(
                    PostProperties.ARCHIVES_PAGE_SIZE.getValue() + " option is not a number format", e);
            return DEFAULT_POST_PAGE_SIZE;
        }
    }

    @Override
    public int getCommentPageSize() {
        try {
            return getByPropertyOrDefault(CommentProperties.PAGE_SIZE, Integer.class,
                    DEFAULT_COMMENT_PAGE_SIZE);
        } catch (NumberFormatException e) {
            log.error(CommentProperties.PAGE_SIZE.getValue() + " option is not a number format", e);
            return DEFAULT_COMMENT_PAGE_SIZE;
        }
    }

    @Override
    public int getRssPageSize() {
        try {
            return getByPropertyOrDefault(PostProperties.RSS_PAGE_SIZE, Integer.class,
                    DEFAULT_RSS_PAGE_SIZE);
        } catch (NumberFormatException e) {
            log.error(PostProperties.RSS_PAGE_SIZE.getValue() + " setting is not a number format",
                    e);
            return DEFAULT_RSS_PAGE_SIZE;
        }
    }

    @Override
    public Region getQiniuRegion() {
        return getByProperty(QiniuOssProperties.OSS_ZONE).map(qiniuZone -> {

            Region region;
            switch (qiniuZone.toString()) {
                case "z0":
                    region = Region.region0();
                    break;
                case "z1":
                    region = Region.region1();
                    break;
                case "z2":
                    region = Region.region2();
                    break;
                case "na0":
                    region = Region.regionNa0();
                    break;
                case "as0":
                    region = Region.regionAs0();
                    break;
                default:
                    // Default is detecting zone automatically
                    region = Region.autoRegion();
            }
            return region;

        }).orElseGet(Region::autoRegion);
    }

    @Override
    public Locale getLocale() {
        return getByProperty(BlogProperties.BLOG_LOCALE).map(localeStr -> {
            try {
                return Locale.forLanguageTag(localeStr.toString());
            } catch (Exception e) {
                return Locale.getDefault();
            }
        }).orElseGet(Locale::getDefault);
    }

    @Override
    public String getBlogBaseUrl() {
        // Get server port
        String serverPort = applicationContext.getEnvironment().getProperty("server.port", "8080");

        String blogUrl = getByProperty(BlogProperties.BLOG_URL).orElse("").toString();

        if (StringUtils.isNotBlank(blogUrl)) {
            blogUrl = StringUtils.removeEnd(blogUrl, "/");
        } else {
            blogUrl = String.format("http://%s:%s", "127.0.0.1", serverPort);
        }

        return blogUrl;
    }

    @Override
    public long getBirthday() {
        return getByProperty(PrimaryProperties.BIRTHDAY, Long.class).orElseGet(() -> {
            long currentTime = DateUtils.now().getTime();
            saveProperty(PrimaryProperties.BIRTHDAY, String.valueOf(currentTime));
            return currentTime;
        });
    }

    @Override
    public PostPermalinkType getPostPermalinkType() {
        return getEnumByPropertyOrDefault(PermalinkProperties.POST_PERMALINK_TYPE,
                PostPermalinkType.class, PostPermalinkType.DEFAULT);
    }

    @Override
    public SheetPermalinkType getSheetPermalinkType() {
        return getEnumByPropertyOrDefault(PermalinkProperties.SHEET_PERMALINK_TYPE,
                SheetPermalinkType.class, SheetPermalinkType.SECONDARY);
    }

    @Override
    public String getSheetPrefix() {
        return getByPropertyOrDefault(PermalinkProperties.SHEET_PREFIX, String.class,
                PermalinkProperties.SHEET_PREFIX.defaultValue());
    }

    @Override
    public String getLinksPrefix() {
        return getByPropertyOrDefault(PermalinkProperties.LINKS_PREFIX, String.class,
                PermalinkProperties.LINKS_PREFIX.defaultValue());
    }

    @Override
    public String getPhotosPrefix() {
        return getByPropertyOrDefault(PermalinkProperties.PHOTOS_PREFIX, String.class,
                PermalinkProperties.PHOTOS_PREFIX.defaultValue());
    }

    @Override
    public String getJournalsPrefix() {
        return getByPropertyOrDefault(PermalinkProperties.JOURNALS_PREFIX, String.class,
                PermalinkProperties.JOURNALS_PREFIX.defaultValue());
    }

    @Override
    public String getArchivesPrefix() {
        return getByPropertyOrDefault(PermalinkProperties.ARCHIVES_PREFIX, String.class,
                PermalinkProperties.ARCHIVES_PREFIX.defaultValue());
    }

    @Override
    public String getCategoriesPrefix() {
        return getByPropertyOrDefault(PermalinkProperties.CATEGORIES_PREFIX, String.class,
                PermalinkProperties.CATEGORIES_PREFIX.defaultValue());
    }

    @Override
    public String getTagsPrefix() {
        return getByPropertyOrDefault(PermalinkProperties.TAGS_PREFIX, String.class,
                PermalinkProperties.TAGS_PREFIX.defaultValue());
    }

    @Override
    public String getPathSuffix() {
        return getByPropertyOrDefault(PermalinkProperties.PATH_SUFFIX, String.class,
                PermalinkProperties.PATH_SUFFIX.defaultValue());
    }

    @Override
    public OptionSimpleDTO convertToDto(Option option) {
        Assert.notNull(option, "Option must not be null");

        return new OptionSimpleDTO().convertFrom(option);
    }

}

