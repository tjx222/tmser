package com.tmser.log.commitlog;

import lombok.Data;

import java.util.List;

/**
 * @author xulei
 * @version 1.0
 * @title
 * @description
 * @company 好未来-爱智康
 * @created 2/22/19 10:28 AM
 * @changeRecord
 */
@Data
public class ChangeLog {

    private int code;
    private String error;

    private RepoInfo repoInfo;
    private List<CommitInfo> commits;
}
