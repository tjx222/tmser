package com.tmser.blog.service.impl;

import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.model.dto.CategoryWithPostCountDTO;
import com.tmser.blog.model.entity.Category;
import com.tmser.blog.model.entity.Post;
import com.tmser.blog.model.entity.PostCategory;
import com.tmser.blog.model.enums.PostStatus;
import com.tmser.blog.model.vo.CategoryVO;
import com.tmser.blog.repository.PostCategoryRepository;
import com.tmser.blog.repository.PostRepository;
import com.tmser.blog.service.CategoryService;
import com.tmser.blog.service.OptionService;
import com.tmser.blog.service.PostCategoryService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.utils.HaloUtils;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.sort.Sort;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Post category service implementation.
 *
 * @author johnniang
 * @author ryanwang
 * @author guqing
 * @date 2019-03-19
 */
@Service
public class PostCategoryServiceImpl extends AbstractCrudService<PostCategory, Integer>
        implements PostCategoryService {

    private final PostCategoryRepository postCategoryRepository;

    private final PostRepository postRepository;

    private CategoryService categoryService;

    private final OptionService optionService;

    public PostCategoryServiceImpl(PostCategoryRepository postCategoryRepository,
                                   PostRepository postRepository,
                                   OptionService optionService) {
        super(postCategoryRepository);
        this.postCategoryRepository = postCategoryRepository;
        this.postRepository = postRepository;
        this.optionService = optionService;
    }

    @Lazy
    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public List<Category> listCategoriesBy(Integer postId) {
        return listCategoriesBy(postId, false);
    }

    @Override
    public List<Category> listCategoriesBy(Integer postId, boolean queryEncryptCategory) {
        Assert.notNull(postId, "Post id must not be null");

        // Find all category ids
        Set<Integer> categoryIds = postCategoryRepository.findAllCategoryIdsByPostId(postId);

        return categoryService.listAllByIds(categoryIds, queryEncryptCategory);
    }


    @Override
    public Map<Integer, List<Category>> listCategoryListMap(
            Collection<Integer> postIds, boolean queryEncryptCategory) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyMap();
        }

        // Find all post categories
        List<PostCategory> postCategories = postCategoryRepository.findAllByPostIdIn(postIds);

        // Fetch category ids
        Set<Integer> categoryIds =
                ServiceUtils.fetchProperty(postCategories, PostCategory::getCategoryId);

        // Find all categories
        List<Category> categories = categoryService.listAllByIds(categoryIds, queryEncryptCategory);

        // Convert to category map
        Map<Integer, Category> categoryMap = ServiceUtils.convertToMap(categories, Category::getId);

        // Create category list map
        Map<Integer, List<Category>> categoryListMap = new HashMap<>();

        // Foreach and collect
        postCategories.forEach(postCategory -> categoryListMap
                .computeIfAbsent(postCategory.getPostId(), postId -> new LinkedList<>())
                .add(categoryMap.get(postCategory.getCategoryId())));

        return categoryListMap;
    }

    @Override
    public List<Post> listPostBy(Integer categoryId) {
        Assert.notNull(categoryId, "Category id must not be null");

        // Find all post ids
        Set<Integer> postIds = postCategoryRepository.findAllPostIdsByCategoryId(categoryId);

        return postRepository.selectBatchIds(postIds);
    }

    @Override
    public List<Post> listPostBy(Integer categoryId, PostStatus status) {
        Assert.notNull(categoryId, "Category id must not be null");
        Assert.notNull(status, "Post status must not be null");

        // Find all post ids
        Set<Integer> postIds =
                postCategoryRepository.findAllPostIdsByCategoryId(categoryId, status);

        return postRepository.selectBatchIds(postIds);
    }

    @Override
    public List<Post> listPostBy(Integer categoryId, Set<PostStatus> status) {
        Assert.notNull(categoryId, "Category id must not be null");
        Assert.notNull(status, "Post status must not be null");

        // Find all post ids
        Set<Integer> postIds = postCategoryRepository
                .findAllPostIdsByCategoryId(categoryId, status);

        return postRepository.selectBatchIds(postIds);
    }

    @Override
    public List<Post> listPostBy(String slug, Set<PostStatus> status) {
        Assert.notNull(slug, "Category slug must not be null");
        Assert.notNull(status, "Post status must not be null");

        Category category = categoryService.getBySlug(slug);

        if (Objects.isNull(category)) {
            throw new NotFoundException("查询不到该分类的信息").setErrorData(slug);
        }

        Set<Integer> postsIds = postCategoryRepository
                .findAllPostIdsByCategoryId(category.getId(), status);

        return postRepository.selectBatchIds(postsIds);
    }

    @Override
    public List<Post> listPostBy(String slug, PostStatus status) {
        Assert.notNull(slug, "Category slug must not be null");
        Assert.notNull(status, "Post status must not be null");

        Category category = categoryService.getBySlug(slug);

        if (Objects.isNull(category)) {
            throw new NotFoundException("查询不到该分类的信息").setErrorData(slug);
        }

        Set<Integer> postsIds =
                postCategoryRepository.findAllPostIdsByCategoryId(category.getId(), status);

        return postRepository.selectBatchIds(postsIds);
    }

    @Override
    public Page<Post> pagePostBy(Integer categoryId, Page pageable) {
        Assert.notNull(categoryId, "Category id must not be null");
        Assert.notNull(pageable, "Page info must not be null");

        // Find all post ids
        Set<Integer> postIds = postCategoryRepository.findAllPostIdsByCategoryId(categoryId);

        return MybatisPageHelper.fillPageData(postRepository.findAllByIdIn(postIds, MybatisPageHelper.changeToMybatisPage(pageable)), pageable);
    }

    @Override
    public Page<Post> pagePostBy(Integer categoryId, PostStatus status, Page pageable) {
        Assert.notNull(categoryId, "Category id must not be null");
        Assert.notNull(status, "Post status must not be null");
        Assert.notNull(pageable, "Page info must not be null");

        // Find all post ids
        Set<Integer> postIds = postCategoryRepository
                .findAllPostIdsByCategoryId(categoryId, status);

        return MybatisPageHelper.fillPageData(
                postRepository.findAllByIdIn(postIds, MybatisPageHelper.changeToMybatisPage(pageable)), pageable);
    }

    @Override
    public Page<Post> pagePostBy(Integer categoryId, Set<PostStatus> status, Page pageable) {
        Assert.notNull(categoryId, "Category id must not be null");
        Assert.notNull(status, "Post status must not be null");
        Assert.notNull(pageable, "Page info must not be null");

        // Find all post ids
        Set<Integer> postIds =
                postCategoryRepository.findAllPostIdsByCategoryId(categoryId, status);

        return MybatisPageHelper.fillPageData(
                postRepository.findAllByIdIn(postIds, MybatisPageHelper.changeToMybatisPage(pageable)), pageable);
    }

    @Override
    public List<PostCategory> mergeOrCreateByIfAbsent(Integer postId, Set<Integer> categoryIds) {
        Assert.notNull(postId, "Post id must not be null");

        if (CollectionUtils.isEmpty(categoryIds)) {
            return Collections.emptyList();
        }

        // Build post categories
        List<PostCategory> postCategoriesStaging = categoryIds.stream().map(categoryId -> {
            PostCategory postCategory = new PostCategory();
            postCategory.setPostId(postId);
            postCategory.setCategoryId(categoryId);
            return postCategory;
        }).collect(Collectors.toList());

        List<PostCategory> postCategoriesToCreate = new LinkedList<>();
        List<PostCategory> postCategoriesToRemove = new LinkedList<>();

        // Find all exist post categories
        List<PostCategory> postCategories = postCategoryRepository.findAllByPostId(postId);

        postCategories.forEach(postCategory -> {
            if (!postCategoriesStaging.contains(postCategory)) {
                postCategoriesToRemove.add(postCategory);
            }
        });

        postCategoriesStaging.forEach(postCategoryStaging -> {
            if (!postCategories.contains(postCategoryStaging)) {
                postCategoriesToCreate.add(postCategoryStaging);
            }
        });

        // Remove post categories
        removeInBatch(postCategoriesToRemove.stream().map(PostCategory::getId).collect(Collectors.toList()));

        // Remove all post categories need to remove
        postCategories.removeAll(postCategoriesToRemove);

        // Add all created post categories
        postCategories.addAll(createInBatch(postCategoriesToCreate));

        // Create them
        return postCategories;
    }

    @Override
    public List<PostCategory> listByPostId(Integer postId) {
        Assert.notNull(postId, "Post id must not be null");

        return postCategoryRepository.findAllByPostId(postId);
    }

    @Override
    public List<PostCategory> listByCategoryId(Integer categoryId) {
        Assert.notNull(categoryId, "Category id must not be null");

        return postCategoryRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public Set<Integer> listCategoryIdsByPostId(Integer postId) {
        Assert.notNull(postId, "Post id must not be null");

        return postCategoryRepository.findAllCategoryIdsByPostId(postId);
    }

    @Override
    public List<PostCategory> removeByPostId(Integer postId) {
        Assert.notNull(postId, "Post id must not be null");

        return postCategoryRepository.deleteByPostId(postId);
    }

    @Override
    public List<PostCategory> removeByCategoryId(Integer categoryId) {
        Assert.notNull(categoryId, "Category id must not be null");

        return postCategoryRepository.deleteByCategoryId(categoryId);
    }

    @Override
    public List<CategoryWithPostCountDTO> listCategoryWithPostCountDto(@NonNull Sort sort,
                                                                       boolean queryEncryptCategory) {
        Assert.notNull(sort, "Sort info must not be null");
        List<Category> categories = categoryService.listAll(sort, queryEncryptCategory);
        List<CategoryVO> categoryTreeVo = categoryService.listToTree(categories);
        populatePostIds(categoryTreeVo);

        // Convert and return
        return flatTreeToList(categoryTreeVo);
    }

    private List<CategoryWithPostCountDTO> flatTreeToList(List<CategoryVO> categoryTree) {
        Assert.notNull(categoryTree, "The categoryTree must not be null.");
        List<CategoryWithPostCountDTO> result = new LinkedList<>();
        walkCategoryTree(categoryTree, category -> {
            CategoryWithPostCountDTO categoryWithPostCountDto =
                    new CategoryWithPostCountDTO();
            BeanUtils.copyProperties(category, categoryWithPostCountDto);
            String fullPath = categoryService.buildCategoryFullPath(category.getSlug());
            categoryWithPostCountDto.setFullPath(fullPath);
            // populate post count.
            int postCount = Objects.nonNull(category.getPostIds()) ? category.getPostIds().size() : 0;
            categoryWithPostCountDto.setPostCount((long) postCount);
            result.add(categoryWithPostCountDto);
        });
        return result;
    }

    private void populatePostIds(List<CategoryVO> categoryTree) {
        Assert.notNull(categoryTree, "The categoryTree must not be null.");
        Map<Integer, Set<Integer>> categoryPostIdsMap = postCategoryRepository.selectList(null)
                .stream()
                .collect(Collectors.groupingBy(PostCategory::getCategoryId,
                        Collectors.mapping(PostCategory::getPostId, Collectors.toSet())));

        walkCategoryTree(categoryTree, category -> {
            // Set post count
            Set<Integer> postIds =
                    categoryPostIdsMap.getOrDefault(category.getId(), new LinkedHashSet<>());
            category.setPostIds(postIds);
        });
        CategoryVO categoryTreeRootNode = new CategoryVO();
        categoryTreeRootNode.setChildren(categoryTree);
        mergePostIdsFromBottomToTop(categoryTreeRootNode);
    }

    private void mergePostIdsFromBottomToTop(CategoryVO root) {
        if (root == null) {
            return;
        }
        List<CategoryVO> children = root.getChildren();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (CategoryVO category : children) {
            mergePostIdsFromBottomToTop(category);
            if (root.getPostIds() == null) {
                root.setPostIds(new LinkedHashSet<>());
            }
            // merge post ids.
            root.getPostIds().addAll(category.getPostIds());
        }
    }

    private void walkCategoryTree(List<CategoryVO> categoryTree, Consumer<CategoryVO> consumer) {
        Queue<CategoryVO> queue = new ArrayDeque<>(categoryTree);
        while (!queue.isEmpty()) {
            CategoryVO category = queue.poll();
            consumer.accept(category);
            if (HaloUtils.isNotEmpty(category.getChildren())) {
                queue.addAll(category.getChildren());
            }
        }
    }

    @Override
    public List<PostCategory> listByCategoryIdList(List<Integer> categoryIdList) {
        Assert.notEmpty(categoryIdList, "category id list not empty");
        return postCategoryRepository.findAllByCategoryIdList(categoryIdList);
    }

}
