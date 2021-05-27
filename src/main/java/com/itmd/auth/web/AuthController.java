package com.itmd.auth.web;

import com.itmd.auth.Config.JwtProperties;
import com.itmd.auth.entiy.UserInfo;
import com.itmd.auth.service.AuthService;
import com.itmd.auth.utils.CookieUtils;
import com.itmd.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties prop;
    /**
     * 登录授权
     * @param username
     * @param password
     * @return
     */
    @PostMapping("authentication")
    public ResponseEntity<Void> login(@RequestParam("username")String username,
                                      @RequestParam("password")String password,
                                      HttpServletRequest request,
                                      HttpServletResponse response)
    {
        String token = authService.login(username, password);
        //写入cookie
        CookieUtils.setCookie(request, response, prop.getCookieName(), token,prop.getCookieMaxAge(),true);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 登录授权
     * @param
     * @param phone
     * @param request
     * @param response
     * @return
     */
    @PostMapping("authentication/phone")
    public ResponseEntity<Void> loginByPhone(@RequestParam("phone")String phone,
                                      @RequestParam("code")String code,
                                      HttpServletRequest request,
                                      HttpServletResponse response)
    {
        String token = authService.loginByPhone(phone,code);
        //写入cookie
        CookieUtils.setCookie(request, response, prop.getCookieName(), token,prop.getCookieMaxAge(),true);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    /**
     * 验证用户信息
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo>verifyUser(@CookieValue("RaoRao_TOKEN")String token,
                                              HttpServletRequest request,HttpServletResponse response){
        UserInfo userinfo = null;
        try {
            userinfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //刷新token
            String newToken = JwtUtils.generateToken(userinfo, prop.getPrivateKey(), prop.getExpire());
            //写入cookie
            CookieUtils.setCookie(request, response, prop.getCookieName(), newToken,prop.getCookieMaxAge(),true);
            return ResponseEntity.ok(userinfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

    }
}
