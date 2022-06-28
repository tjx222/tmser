package com.tmser.blog.core.freemarker.tag;

import com.tmser.blog.model.entity.PostComment;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.model.support.HaloConst;
import com.tmser.blog.service.PostCommentService;
import com.tmser.model.page.Page;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Freemarker custom tag of comment.
 *
 * @author ryanwang
 * @date 2019-03-22
 */
@Component
public class CommentTagDirective implements TemplateDirectiveModel {

    private final PostCommentService postCommentService;

    public CommentTagDirective(Configuration configuration, PostCommentService postCommentService) {
        this.postCommentService = postCommentService;
        configuration.setSharedVariable("commentTag", this);
    }

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        final DefaultObjectWrapperBuilder builder =
                new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);

        if (params.containsKey(HaloConst.METHOD_KEY)) {
            String method = params.get(HaloConst.METHOD_KEY).toString();
            switch (method) {
                case "latest":
                    int top = Integer.parseInt(params.get("top").toString());
                    Page<PostComment> postComments =
                            postCommentService.pageLatest(top, CommentStatus.PUBLISHED);
                    env.setVariable("comments",
                            builder.build().wrap(postCommentService.convertToWithPostVo(postComments)));
                    break;
                case "count":
                    env.setVariable("count", builder.build().wrap(postCommentService.count()));
                    break;
                default:
                    break;
            }
        }
        body.render(env.getOut());
    }
}
