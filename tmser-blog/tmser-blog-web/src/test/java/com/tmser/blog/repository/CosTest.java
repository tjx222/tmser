package com.tmser.blog.repository;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.region.Region;
import com.tmser.blog.model.properties.TencentCosProperties;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *    描述信息
 * </pre>
 *
 * @version $Id: CosTest.java, v1.0 2022/8/4 10:59 tmser Exp $
 */
public class CosTest {

    public String generateUrl(String key,String sign) {
        COSClient cosClient = generateClient();
// 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        String bucketName ="testoss-1309636105";
// 设置签名过期时间(可选), 若未进行设置则默认使用 ClientConfig 中的签名过期时间(1小时)
// 这里设置签名在半个小时后过期
        Date expirationDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

// 填写本次请求的参数，需与实际请求相同，能够防止用户篡改此签名的 HTTP 请求的参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("ci-process","doc-preview");
        params.put("dstType","html");

// 填写本次请求的头部，需与实际请求相同，能够防止用户篡改此签名的 HTTP 请求的头部
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("sign", sign);

// 请求的 HTTP 方法，上传请求用 PUT，下载请求用 GET，删除请求用 DELETE
        HttpMethodName method = HttpMethodName.GET;

        URL url = cosClient.generatePresignedUrl(bucketName, key, expirationDate, method, headers, params);
        return url.toString();
    }

    private COSClient generateClient() {
        String region ="ap-beijing";
        String secretId ="AKIDjHaeT3OUM1RNxINDoAOWP3";
        String secretKey ="SfeOt9AwTIkxve8NM";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        Region regionConfig = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionConfig);

        // Init OSS client
        return new COSClient(cred, clientConfig);
    }

    public static void main(String[] args) {
        CosTest cosTest = new CosTest();
        System.out.println(cosTest.generateUrl("谋事在人.docx","fsdfsdf"));
    }
}
