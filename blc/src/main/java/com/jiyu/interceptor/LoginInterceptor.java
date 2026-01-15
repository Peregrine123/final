package com.jiyu.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//判断 session 中是否存在 user 属性，如果存在就放行，如果不存在就跳转到 login 页面
public class LoginInterceptor  implements HandlerInterceptor {

    @Override
    public boolean preHandle (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (HttpMethod.OPTIONS.toString().equals(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }

        Subject subject = SecurityUtils.getSubject();
        // 使用 shiro 验证
        if (!subject.isAuthenticated() && !subject.isRemembered()){
            // Return a proper 401 so frontend can reliably redirect to login.
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter()
                    .write("{\"code\":401,\"message\":\"未登录或登录已过期\",\"result\":null}");
            return false;
        }

        return true;
    }

    //遍历需要拦截的路径，只要page以requiredAuthPage开头就拦截
    private boolean beginWith(String page, String[] requiredAuthPages) {
        boolean result = false;
        for (String requiredAuthPage : requiredAuthPages) {
            if(StringUtils.startsWith(page, requiredAuthPage)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
