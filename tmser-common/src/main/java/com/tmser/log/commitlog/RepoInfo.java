package com.tmser.log.commitlog;

import lombok.Data;

@Data
public class RepoInfo {
    /**
     * jenkins 任务地址
     */
    private String buildUrl;
    /**
     * 代码地址
     */
    private String gitUrl;
    /**
     * 分支名
     */
    private String branch;
    private String localBranch;
    /**
     * 当前发布的最后一次提交
     */
    private String currentCommit;
}
