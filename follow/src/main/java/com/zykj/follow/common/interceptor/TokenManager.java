package com.zykj.follow.common.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zykj.follow.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 验证token的实现类
 *
 * @author tang
 */
@Slf4j
@Service("tokenManager")
public class TokenManager {

    @Autowired
    private RedisUtil redisUtil;

    private static final String secret = "R78{7(53!~3&>5}3}61^~LX,0m";
    private static final String issuer = "tangjiabin";
    private static final String key = "user";


    public TokenModel createToken(String phone) throws UnsupportedEncodingException {

        String token = JWT.create()
                .withIssuer(issuer)
                .withJWTId(UUID.randomUUID().toString().toUpperCase())
                .withClaim(key, phone)
                .sign(Algorithm.HMAC256(secret));

        TokenModel model = new TokenModel(phone, token);
        redisUtil.setStringSECONDS(phone, token, 60 * 10L);

        return model;
    }

    public TokenModel getToken(String token) throws UnsupportedEncodingException, JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
        String userId = jwt.getClaim(key).asString();
        return new TokenModel(userId, jwt.getToken());
    }


    public boolean checkToken(String token) throws UnsupportedEncodingException, JWTVerificationException {
        if (!StringUtils.hasLength(token)) {
            return false;
        }
        TokenModel tokenModel = getToken(token);

        String dbtoken = null;
        try {

            if (!StringUtils.isEmpty(tokenModel)) {
                dbtoken = redisUtil.getString(tokenModel.getPhone());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbtoken != null && token.equals(dbtoken);
    }


}
