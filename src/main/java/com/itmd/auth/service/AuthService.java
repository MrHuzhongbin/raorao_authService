package com.itmd.auth.service;

import com.itmd.auth.Config.JwtProperties;
import com.itmd.auth.client.UserClient;
import com.itmd.auth.entiy.UserInfo;
import com.itmd.auth.utils.JwtUtils;
import com.itmd.enums.ExceptionEnum;
import com.itmd.exception.RaoraoBookShopException;
import com.itmd.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties properties;
    @Autowired
    private StringRedisTemplate template;

    static final String KEY_PREFIX = "user:code:phone:";

    public String login(String username, String password) {

        User user = userClient.queryUser(username, password);
        //判断user
        if(ObjectUtils.isEmpty(user) || user.getId() == null){
            throw new RaoraoBookShopException(ExceptionEnum.USER_NOT_FOUND_ERROR);
        }
        //生成token
        String token = null;
        try {
            token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername(),user.getImage()), properties.getPrivateKey(), properties.getExpire());
        } catch (Exception e) {
            log.info("[授权中心]token生成失败:"+username);
            throw new RaoraoBookShopException(ExceptionEnum.TOKEN_ERROR);
        }
        return token;
    }
    public String loginByPhone(String phone,String code) {

        User user = userClient.queryUserByPhone(phone);
        //判断user
        if(ObjectUtils.isEmpty(user) || user.getId() == null){
            throw new RaoraoBookShopException(ExceptionEnum.USER_NOT_FOUND_ERROR);
        }
        //判断验证码
        String key = KEY_PREFIX+user.getPhone();
        String codeRedis = template.opsForValue().get(key);
        if(!code.equals(codeRedis)){
            throw new RaoraoBookShopException(ExceptionEnum.TOKEN_ERROR);
        }
        //生成token
        String token = null;
        try {
            token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername(),user.getImage()), properties.getPrivateKey(), properties.getExpire());
        } catch (Exception e) {
            log.info("[授权中心]token生成失败:"+phone);
            throw new RaoraoBookShopException(ExceptionEnum.TOKEN_ERROR);
        }
        return token;
    }
}
