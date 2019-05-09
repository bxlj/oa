package com.lj.oa.global;

import com.lj.oa.entity.Employee;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 登陆拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws IOException
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String url = request.getRequestURI();
        if (url.toLowerCase().indexOf("login")>=0){
            return true;
        }

        Employee employee = (Employee) request.getSession().getAttribute("employee");
        if (employee != null){
            return true;
        }
        response.sendRedirect("/to_login");
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
