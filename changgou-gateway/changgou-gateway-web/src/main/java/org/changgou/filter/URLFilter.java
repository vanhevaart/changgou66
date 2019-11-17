package org.changgou.filter;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/24  22:44
 * @author HeZheng
 */
public class URLFilter {

    /**
     * 无需拦截的url
     */
    private static final String uri = "/api/user/add,/api/user/login";


    public static boolean hasAuthorize(String url){
        String[] uris = uri.split(",");
        for (String uri : uris) {
            if (url.startsWith(uri)){
                // 访问这些资源无需登录
                return true;
            }
        }
        return false;
    }
}
