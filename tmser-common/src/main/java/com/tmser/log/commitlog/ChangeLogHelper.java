package com.tmser.log.commitlog;

import com.tmser.util.CollectionUtils;
import com.tmser.util.DateUtils;
import com.tmser.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description 发版的更新内容解析工具类
 */
public class ChangeLogHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeLogHelper.class);

    public static final String DEFAULT_CHANGELOG_FILE_NAME = "changelog.txt";
    /**
     * 提交日志前缀，每一行的开头部分
     */
    private static final String COMMIT_LOG_KEY = "log:";

    /**
     * 提交日志字段之间的分隔符，需要在Jenkins编译脚本中对应配置
     */
    private static final String FIELD_SEPARATOR = " -&&- ";

    /**
     * 每行最长字符数
     */
    private static final int MAX_LENGTH_PER_LINE = 30;
    /**
     * 最大展示行数
     */
    private static final int MAX_LOG_LINES = 10;

    /**
     * 读取更新内容
     *
     * @return
     */
    public static String readChangeLogSummary() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n---\n\n");
        String content = "";
        try {
            ChangeLog changeLog = parseChangeLog(getResourceFile());
            if (changeLog.getCode() != 0) {
                builder.append("处理变更记录出错").append("\n\n");
                builder.append(changeLog.getError());
                return builder.toString();
            }
            RepoInfo repoInfo = changeLog.getRepoInfo();
            builder.append("分支: ").append(repoInfo.getBranch()).append("\n\n");
            List<CommitInfo> commits = changeLog.getCommits();
            if (CollectionUtils.isEmpty(commits)) {
                builder.append("代码无修改");
                return builder.toString();
            }
            int half = MAX_LOG_LINES / 2;
            for (int i = 0; i < commits.size(); i++) {
                //最多显示10行，中间的用省略号替代
                if (commits.size() > MAX_LOG_LINES && i == half) {
                    builder.append("...").append("\n\n");
                }
                if (commits.size() > MAX_LOG_LINES && i >= half && i < commits.size() - half) {
                    continue;
                }
                CommitInfo commit = commits.get(i);
                builder.append(i + 1).append(" ");
                builder.append(commit.getAuthor()).append(" ")
                        .append(DateUtils.dateTime2String(commit.getCommitTime(), "MMdd HH:mm")).append(" ");
                String comment = commit.getComment();
                if (StringUtils.isNoneBlank(comment) && comment.length() >= MAX_LENGTH_PER_LINE) {
                    builder.append(comment, 0, MAX_LENGTH_PER_LINE).append("...");
                }
                if (StringUtils.isNoneBlank(comment) && comment.length() < MAX_LENGTH_PER_LINE) {
                    builder.append(comment);
                }
                builder.append("\n\n");
            }
            content = builder.toString();
            LOGGER.info("changelogs:" + content);
        } catch (Exception e) {
            LOGGER.error("read change log faild", e);
        }
        return content;
    }

    /**
     * 读取更新内容
     *
     * @return
     */
    public static ChangeLog parseChangeLog(File file) {
        ChangeLog changeLog = new ChangeLog();
        try {
            if (file == null || !file.exists()) {
                LOGGER.error("can't find changelog file");
                changeLog.setCode(401);
                changeLog.setError("读取changelog失败，文件不存在");
                return changeLog;
            }
            List<CommitInfo> commits = new ArrayList<>();
            Map<String, String> repoInfoMap = new HashMap<>();
            List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
            for (String line : lines) {
                if (StringUtils.isBlank(line) || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith(COMMIT_LOG_KEY)) {
                    line = line.substring(COMMIT_LOG_KEY.length()).trim();
                    String[] field = line.split(FIELD_SEPARATOR);
                    if (field == null || field.length != 4) {
                        LOGGER.error("parse changelog content faild. content:" + line);
                        changeLog.setCode(402);
                        changeLog.setError("解析changelog出错。内容: " + line);
                        break;
                    }
                    CommitInfo commit = new CommitInfo();
                    commit.setAuthor(field[0]);
                    commit.setCommitId(field[1]);
                    commit.setComment(field[2].replaceAll(FIELD_SEPARATOR, " "));
                    commit.setCommitTime(DateUtils.string2DateTime(field[3]));
                    commits.add(commit);
                } else {
                    int index = line.indexOf(':');
                    if (index > 0) {
                        repoInfoMap.put(line.substring(0, index), line.substring(index).trim());
                    }
                }
            }
            RepoInfo repoInfo = new RepoInfo();
            repoInfo.setGitUrl(repoInfoMap.get("git_url"));
            repoInfo.setBuildUrl(repoInfoMap.get("build_url"));
            repoInfo.setBranch(repoInfoMap.get("branch"));
            repoInfo.setLocalBranch(repoInfoMap.get("local_branch"));
            repoInfo.setCurrentCommit(repoInfoMap.get("commit"));

            changeLog.setRepoInfo(repoInfo);
            changeLog.setCommits(commits);
        } catch (IOException e) {
            LOGGER.error("read changelog faild.", e);
            changeLog.setCode(403);
            changeLog.setError("解析changelog异常，错误信息：" + e.getMessage());
        }
        return changeLog;
    }


    public static File getResourceFile() {
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        LOGGER.info("changelog path = = {}", path);
        try {
            return new File(path, DEFAULT_CHANGELOG_FILE_NAME);
        } catch (Exception e) {
            LOGGER.warn("get resource faild. " + DEFAULT_CHANGELOG_FILE_NAME, e);
        }
        return null;
    }
}
