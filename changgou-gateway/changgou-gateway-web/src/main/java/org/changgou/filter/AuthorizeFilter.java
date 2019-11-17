package org.changgou.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Author:  HZ
 * <p> 鉴权全局过滤器
 * Create:  2019/8/20  22:30
 *
 * @author HeZheng
 */
@Component
public class
AuthorizeFilter implements GlobalFilter, Ordered {

    /**
     * 令牌头名字
     */
    private static final String AUTHORIZE_TOKEN = "Authorization";
    private static final String LOGIN_URL = "http://localhost:9001/oauth/login";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取Request、Response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 获取请求路径
        String path = request.getURI().getPath();
        // 如果是登录请求则放行进行登录验证
        if(URLFilter.hasAuthorize(path)) {
            return chain.filter(exchange);
        } else { // 如果是其他请求则需要验证用户是否已经登录
            // 获取请求头
            HttpHeaders headers = request.getHeaders();
            String token = headers.getFirst(AUTHORIZE_TOKEN);
            // 如果为空,则从请求头中获取
            if(StringUtils.isEmpty(token)) {
                MultiValueMap<String, String> queryParams = request.getQueryParams();
                token = queryParams.getFirst(AUTHORIZE_TOKEN);
            }
            // 如果为空,从cookie中获取
            if(StringUtils.isEmpty(token)) {
                HttpCookie first = request.getCookies().getFirst(AUTHORIZE_TOKEN);
                if(first != null) {
                    token = first.getValue();
                }
            }
            // 如果token扔为空,则跳转回登录页面
            if(StringUtils.isEmpty(token)) {
                redirect(request, response);
                return response.setComplete();
            }
            // 进行JWT解析(令牌加密后网关就不做令牌解析了,只获取判断有无,有的话添加进请求头中,转发至各服务,由转发至的服务自己解析令牌)
            try {
//                Claims claims = JwtUtil.parseJWT(token);
                // 将token信息添加进头文件中
                request.mutate().header(AUTHORIZE_TOKEN, "Bearer " + token);
            } catch (Exception e) {
                e.printStackTrace();
                // 解析错误,跳转回登录页面
                redirect(request, response);
                return response.setComplete();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private void redirect(ServerHttpRequest request, ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.SEE_OTHER);
        String url = LOGIN_URL + "?ReturnUrl=" + request.getURI().toString();
        response.getHeaders().add("Location", url);
    }
}
