package com.itmd.auth.Config;

import com.itmd.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "raorao.jwt")
public class JwtProperties {
    private String secret; // 密钥
    private String pubKeyPath;// 公钥
    private String priKeyPath;// 私钥
    private int expire;// token过期时间
    private PublicKey publicKey; // 公钥
    private PrivateKey privateKey; // 私钥
    private String cookieName;
    private int cookieMaxAge;
    //对象实例化，读取公钥和私钥
    @PostConstruct
    public void init() throws Exception{
        //公钥私钥不存在，则生成
        File pubFile = new File(pubKeyPath);
        File priFile = new File(priKeyPath);
        if(!priFile.exists() || !pubFile.exists()){
            RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
        }
        //读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }
}
