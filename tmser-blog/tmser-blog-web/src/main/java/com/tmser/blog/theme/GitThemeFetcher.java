package com.tmser.blog.theme;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import com.tmser.blog.exception.ThemePropertyMissingException;
import com.tmser.blog.handler.theme.config.support.ThemeProperty;
import com.tmser.blog.utils.FileUtils;
import com.tmser.blog.utils.GitUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.TagOpt;

/**
 * Git theme fetcher.
 *
 * @author johnniang
 */
@Slf4j
public class GitThemeFetcher implements ThemeFetcher {

    @Override
    public boolean support(Object source) {
        if (source instanceof String) {
            return ((String) source).endsWith(".git");
        }
        return false;
    }

    @Override
    public ThemeProperty fetch(Object source) {
        final String repoUrl = source.toString();

        try {
            // create temp folder
            final Path tempDirectory = FileUtils.createTempDirectory();

            // clone from git
            log.info("Cloning git repo {} to {}", repoUrl, tempDirectory);
            try (final Git git = Git.cloneRepository()
                .setTagOption(TagOpt.FETCH_TAGS)
                .setNoCheckout(false)
                .setDirectory(tempDirectory.toFile())
                .setCloneSubmodules(false)
                .setURI(repoUrl)
                .setRemote("upstream")
                .call()) {
                log.info("Cloned git repo {} to {} successfully", repoUrl, tempDirectory);

                // find latest tag
                final Pair<Ref, RevCommit> latestTag = GitUtils.getLatestTag(git);
                final CheckoutCommand checkoutCommand = git.checkout()
                    .setName("halo")
                    .setCreateBranch(true);
                if (latestTag != null) {
                    // checkout latest tag
                    checkoutCommand.setStartPoint(latestTag.getValue());
                }
                Ref haloBranch = checkoutCommand.call();
                log.info("Checkout branch: {}", haloBranch.getName());
            }

            // locate theme property location
            Path themePropertyPath = ThemeMetaLocator.INSTANCE.locateProperty(tempDirectory)
                    .orElseThrow(() -> new ThemePropertyMissingException("主题配置文件缺失，请确认后重试！"));

            // fetch property
            return ThemePropertyScanner.INSTANCE.fetchThemeProperty(themePropertyPath.getParent())
                .orElseThrow(()->  new NoSuchElementException("No value present"));
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException("主题拉取失败！（" + e.getMessage() + "）", e);
        }
    }

}
