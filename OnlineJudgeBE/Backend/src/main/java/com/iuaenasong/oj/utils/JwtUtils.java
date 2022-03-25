/**
 * @Author LengYun
 * @Since 2022/01/13 14:28
 * @Description
 */

package com.iuaenasong.oj.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j(topic = "oj")
@Data
@Component
@ConfigurationProperties(prefix = "oj.jwt")
public class JwtUtils {

    private String secret;
    private long expire;
    private String header;
    private long checkRefreshExpire;
    private final static String TOKEN_KEY = "token-key:";
    private final static String TOKEN_REFRESH = "token-refresh:";

    @Autowired
    private RedisUtils redisUtils;

    
    public String generateToken(String userId) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        String token = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setSubject(userId)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        redisUtils.set(TOKEN_REFRESH + userId, token, checkRefreshExpire);
        redisUtils.set(TOKEN_KEY + userId, token, expire);
        return token;
    }

    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.debug("validate is token error ", e);
            return null;
        }
    }

    
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

}
