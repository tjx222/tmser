package com.tmser.blog.model.support;

import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * PostComment page implementation.
 *
 * @author johnniang
 * @date 3/25/19
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommentPage<T> extends PageImpl<T> implements Page<T> {

    /**
     * Total comment (Contains sub comments)
     */
    private final long commentCount;

    public CommentPage(List<T> content, Pageable pageable, long topTotal, long commentCount) {
        super(pageable.getCurrent(), pageable.getSize());
        setContent(content);
        this.commentCount = commentCount;
    }

    public CommentPage(List<T> content, long commentCount) {
        super();
        setContent(content);
        this.commentCount = commentCount;
    }
}
