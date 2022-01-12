package com.tmser.log.commitlog;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommitInfo {
    /**
     * 提交人
     */
    private String author;
    /**
     * 提交id
     */
    private String commitId;
    /**
     * 提交日期
     */
    private LocalDateTime commitTime;
    /**
     * 提交注释
     */
    private String comment;
}