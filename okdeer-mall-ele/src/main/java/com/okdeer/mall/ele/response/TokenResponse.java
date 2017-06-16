package com.okdeer.mall.ele.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * token响应类
 */
public class TokenResponse extends AbstractResponse {

    private TokenData data;

    @JsonCreator
    public TokenResponse(@JsonProperty("data") TokenData data) {
        this.data = data;
    }

    @JsonIgnoreProperties({"app_id"})
    public static class TokenData {
        private String access_token;
        private long expire_time;

        @JsonCreator
        public TokenData(@JsonProperty("access_token") String access_token, @JsonProperty("expire_time") long expire_time) {
            this.access_token = access_token;
            this.expire_time = expire_time;
        }

        public String getAccess_token() {
            return access_token;
        }

        public long getExpire_time() {
            return expire_time;
        }

        @Override
        public String toString() {
            return "TokenData{" +
                    "access_token='" + access_token + '\'' +
                    ", expire_time=" + expire_time +
                    '}';
        }
    }

    public TokenData getData() {
        return data;
    }

    public void setData(TokenData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
