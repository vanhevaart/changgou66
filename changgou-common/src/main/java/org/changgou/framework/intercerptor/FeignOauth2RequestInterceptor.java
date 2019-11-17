package org.changgou.framework.intercerptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Author:  HZ
 * <p> 拦截feign调用,在请求头中附加请求头信息(包含了token信息),以便解决微服务之间调用的认证问题
 * Create:  2019/8/24  21:25
 */
public class  FeignOauth2RequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            HttpServletRequest request = requestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                String value = request.getHeader(headerName);
                requestTemplate.header(headerName,value);
            }
        }
    }
}
