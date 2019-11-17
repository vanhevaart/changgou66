package org.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/22  0:06
 */
public class ParseJwtTest {
    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiI0   InR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.AvqOUXcmE0DrIrKpR0ajT6-iVQJEDzoCELeykP0OCKzSU2HfXQ-2TTuDSULeLEMl2IWMDMI-p_l9R_Xd6LnoX9CGHup-0zRC1Cl0EI4DVNPZk9r13pecx8YQFXh3mqmJYCyXeo_0WTEcVX-lh2ggen0V-yOTWD6PsNUfe92DsFjv0hRM7tmoTbd1qO_CEhHhzXyxd7ufFkSXZGndLlW-GGUh_ISbFVqn_PZhN0kXogyJL-UcMVpY01_q1QuM5giiFJUUF4-TuPpp1-NxHiH6riAJcQv1YkmNSDh6myT9B_arE9Rf7pWumpHaFJ-vsFq27WQn48FM_4cIvUZx9ZzO8w";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm9ttnxgV8bUimZAs6hdY3GsrpD0TkYpelcOufXZ5kVYBmbVEeNLWq4cRG8YWzjqQ/XMTN4aeEodJBGz1fpjzFFB4VRO/h1SbsXfc6bUbsHoGf4WMcnl/+l35Mtep4aXoJ00w9t5qhh9pHTdd56QxioTJIT/3js+jVnVCTKSdi0XtBhbzp6I5+MQqZ48y5Jn1esWxhvpjcSFyQBy7CmI6ARPramt1MWgmo+M1ipMn7S6oV1t4wC1Kz10mSpmEvfbhB+FOKBEEIFDT60Ksck0Kc4Vd6zyGG3zqqxngf2coUvur6EEvQSidUaF8yg7ujORKkWqtsx7yWVEy9o+4Hymh1wIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
