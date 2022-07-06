package com.tmser.blog.model.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.tmser.blog.model.enums.CommentStatus;
import com.tmser.blog.utils.ServiceUtils;

/**
 * Base comment entity.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-20
 */
@Data
@Entity(name = "BaseComment")

@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER,
    columnDefinition = "int default 0")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseComment extends BaseEntity {

    public static final int CT_POST = 0;
    public static final int CT_SHEET = 1;
    public static final int CT_JOUR = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    private Long id;

    /**
     * Commentator's name.
     */
    @Column(name = "author", length = 50, nullable = false)
    private String author;

    /**
     * Commentator's email.
     */
    @Column(name = "email")
    private String email;

    /**
     * Commentator's ip address.
     */
    @Column(name = "ip_address", length = 127)
    private String ipAddress;

    /**
     * Commentator's website.
     */
    @Column(name = "author_url", length = 511)
    private String authorUrl;

    /**
     * Gravatar md5
     */
    @Column(name = "gravatar_md5", length = 127)
    private String gravatarMd5;

    /**
     * Comment content.
     */
    @Column(name = "content", length = 1023, nullable = false)
    private String content;

    /**
     * Comment status.
     */
    @Column(name = "status")
    private CommentStatus status;

    /**
     * Commentator's userAgent.
     */
    @Column(name = "user_agent", length = 511)
    private String userAgent;

    /**
     * Is admin's comment.
     */
    @Column(name = "is_admin")
    private Boolean isAdmin;

    /**
     * Allow notification.
     */
    @Column(name = "allow_notification")
    private Boolean allowNotification;

    /**
     * Post id.
     */
    @Column(name = "post_id", nullable = false)
    private Integer postId;

    /**
     * Post id.
     */
    @Column(name = "type", nullable = false)
    private Integer type;

    /**
     * Whether to top the comment.
     */
    @Column(name = "top_priority")
    private Integer topPriority;

    /**
     * Parent comment.
     */
    @Column(name = "parent_id")
    private Long parentId;

    @Override
    public void prePersist() {
        super.prePersist();

        if (ServiceUtils.isEmptyId(parentId)) {
            parentId = 0L;
        }

        if (ipAddress == null) {
            ipAddress = "";
        }

        if (authorUrl == null) {
            authorUrl = "";
        }

        if (gravatarMd5 == null) {
            gravatarMd5 = "";
        }

        if (status == null) {
            status = CommentStatus.AUDITING;
        }

        if (userAgent == null) {
            userAgent = "";
        }

        if (isAdmin == null) {
            isAdmin = false;
        }

        if (allowNotification == null) {
            allowNotification = true;
        }
    }
}
