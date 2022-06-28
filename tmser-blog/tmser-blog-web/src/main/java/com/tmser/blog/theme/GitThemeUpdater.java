package com.tmser.blog.theme;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.tmser.blog.exception.NotFoundException;
import com.tmser.blog.exception.ServiceException;
import com.tmser.blog.exception.ThemeUpdateException;
import com.tmser.blog.handler.theme.config.support.ThemeProperty;
import com.tmser.blog.repository.ThemeRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import static com.tmser.blog.utils.GitUtils.*;

/**
 * Update from theme property config.
 *
 * @author johnniang
 */
public class GitThemeUpdater implements ThemeUpdater {

    private final ThemeRepository themeRepository;

    private final ThemeFetcherComposite fetcherComposite;

    public GitThemeUpdater(ThemeRepository themeRepository,
        ThemeFetcherComposite fetcherComposite) {
        this.themeRepository = themeRepository;
        this.fetcherComposite = fetcherComposite;
    }

    @Override
    public ThemeProperty update(String themeId) throws IOException {
        // get theme property
        final ThemeProperty oldThemeProperty = themeRepository.fetchThemePropertyByThemeId(themeId)
            .orElseThrow(
                () -> new NotFoundException("主题 " + themeId + " 不存在或已删除！").setErrorData(themeId));

        // get update config
        final String gitRepo = oldThemeProperty.getRepo();

        // fetch latest theme
        final ThemeProperty newThemeProperty = fetcherComposite.fetch(gitRepo);

        // merge old theme and new theme
        final ThemeProperty mergedThemeProperty = merge(oldThemeProperty, newThemeProperty);

        // backup old theme
        final Path backupPath = ThemeUpdater.backup(oldThemeProperty);

        try {
            // delete old theme
            themeRepository.deleteTheme(oldThemeProperty);

            // copy new theme to old theme folder
            return themeRepository.attemptToAdd(mergedThemeProperty);
        } catch (Throwable t) {
            log.error("Failed to add new theme, and restoring old theme from " + backupPath, t);
            // restore old theme
            ThemeUpdater.restore(backupPath, oldThemeProperty);
            log.info("Restored old theme from path: {}", backupPath);
            throw t;
        }
    }

    public ThemeProperty merge(ThemeProperty oldThemeProperty, ThemeProperty newThemeProperty)
        throws IOException {

        final Path oldThemePath = Paths.get(oldThemeProperty.getThemePath());
        // open old git repo
        try (final Git oldGit = Git.init().setDirectory(oldThemePath.toFile()).call()) {
            // 0. commit old repo
            commitAutomatically(oldGit);

            final Path newThemePath = Paths.get(newThemeProperty.getThemePath());
            // trying to open new git repo
            try (final Git ignored = Git.open(newThemePath.toFile())) {
                // remove remote
                removeRemoteIfExists(oldGit, "newTheme");
                // add this new git to remote for old repo
                final RemoteConfig addedRemoteConfig = oldGit.remoteAdd()
                    .setName("newTheme")
                    .setUri(new URIish(newThemePath.toString()))
                    .call();
                log.info("git remote add newTheme {} {}",
                    addedRemoteConfig.getName(),
                    addedRemoteConfig.getURIs());

                // fetch remote data
                final String remote = "newTheme/halo";
                log.info("git fetch newTheme/halo");
                final FetchResult fetchResult = oldGit.fetch()
                    .setRemote("newTheme")
                    .call();
                log.info("Fetch result: {}", fetchResult.getMessages());

                // rebase upstream
                log.info("git rebase newTheme");
                final RebaseResult rebaseResult = oldGit.rebase()
                    .setUpstream(remote)
                    .call();
                log.info("Rebase result: {}", rebaseResult.getStatus());
                logCommit(rebaseResult.getCurrentCommit());

                // check rebase result
                if (!rebaseResult.getStatus().isSuccessful()) {
                    if (oldGit.getRepository().getRepositoryState() != RepositoryState.SAFE) {
                        // if rebasing stopped or failed, you can get back to the original state by
                        // running it
                        // with setOperation(RebaseCommand.Operation.ABORT)
                        final RebaseResult abortRebaseResult = oldGit.rebase()
                            .setUpstream(remote)
                            .setOperation(RebaseCommand.Operation.ABORT)
                            .call();
                        log.error("Aborted rebase with state: {} : {}",
                            abortRebaseResult.getStatus(),
                            abortRebaseResult.getConflicts());
                    }
                    throw new ThemeUpdateException("无法自动合并最新文件！请尝试删除主题并重新拉取。");
                }
            }
        } catch (URISyntaxException | GitAPIException e) {
            throw new ServiceException("合并主题失败！请确认该主题支持在线更新。", e);
        }

        return newThemeProperty;
    }

}
