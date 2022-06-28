package com.tmser.blog.theme;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.zip.ZipInputStream;

import com.tmser.blog.exception.ThemePropertyMissingException;
import com.tmser.blog.utils.FileUtils;
import com.tmser.blog.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import com.tmser.blog.handler.theme.config.support.ThemeProperty;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import static com.tmser.blog.utils.FileUtils.unzip;

/**
 * Zip theme fetcher.
 *
 * @author johnniang
 */
@Slf4j
public class ZipThemeFetcher implements ThemeFetcher {

    private CloseableHttpClient httpClient;

    public ZipThemeFetcher() {
        try {
            this.httpClient = HttpClientUtils.createHttpsClient(20);
        } catch (Exception e) {
            log.error("create http client failed!",e);
        }
    }

    @Override
    public boolean support(Object source) {
        if (source instanceof String) {
            return ((String) source).endsWith(".zip");
        }
        return false;
    }

    @Override
    public ThemeProperty fetch(Object source) {
        final String themeZipLink = source.toString();

        // build http request
        HttpUriRequest request = RequestBuilder.get().setUri(URI.create(themeZipLink))
                .build();

        try {
            // request from remote
            log.info("Fetching theme from {}", themeZipLink);
            CloseableHttpResponse inputStreamResponse =
                httpClient.execute(request);
            InputStream inputStream = inputStreamResponse.getEntity().getContent();

            // unzip zip archive
            try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                Path tempDirectory = FileUtils.createTempDirectory();
                log.info("Unzipping theme {} to {}", themeZipLink, tempDirectory);
                unzip(zipInputStream, tempDirectory);

                // resolve theme property
                return ThemePropertyScanner.INSTANCE.fetchThemeProperty(tempDirectory)
                    .orElseThrow(() -> new ThemePropertyMissingException("主题配置文件缺失！请确认后重试。"));
            }
        } catch (IOException e) {
            throw new RuntimeException("主题拉取失败！（" + e.getMessage() + "）", e);
        }
    }

}
