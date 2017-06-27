package com.okdeer.mall.ele;

import com.okdeer.mall.Application;
import com.okdeer.mall.ele.config.ElemeOpenConfig;
import com.okdeer.mall.ele.config.RequestConstant;
import com.okdeer.mall.ele.response.TokenResponse;
import com.okdeer.mall.ele.sign.OpenSignHelper;
import com.okdeer.mall.ele.util.HttpClient;
import com.okdeer.mall.ele.util.HttpClientRuntimeException;
import com.okdeer.mall.ele.util.JsonUtils;
import com.okdeer.mall.ele.util.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取token例子
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TestObtainToken {
    private static final Log logger = LogFactory.getLog(TestObtainToken.class);

    /**
     * 请求生成token
     */
    @Test
    public void testGetToken() throws Exception {
        String url = ElemeOpenConfig.API_URL + RequestConstant.obtainToken;

        List<BasicNameValuePair> params = new ArrayList<>();
        String salt = String.valueOf(RandomUtils.getInstance().generateValue(1000, 10000));
        String sig = OpenSignHelper.generateSign(ElemeOpenConfig.appId, salt, ElemeOpenConfig.secretKey);

        // 请求token
        List<BasicNameValuePair> paramsToken = new ArrayList<>();
        paramsToken.add(new BasicNameValuePair("app_id", ElemeOpenConfig.appId));
        paramsToken.add(new BasicNameValuePair("salt", salt));
        paramsToken.add(new BasicNameValuePair("signature", sig));

        String tokenRes = "";
        try {
            tokenRes = HttpClient.get(url, paramsToken);
        } catch (Exception e) {
            throw new HttpClientRuntimeException("请求token出现异常", e);
        }

        logger.info(String.format("Response is %s", tokenRes));

        /**
         * 生成token
         */
        String token = JsonUtils.getInstance().readValue(tokenRes, TokenResponse.class).getData().getAccess_token();

        logger.info(String.format("Generate token is: %s", token));
    }
}
