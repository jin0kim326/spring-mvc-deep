package hello.exception;

import hello.exception.filter.LogFilter;
import hello.exception.interceptor.LogInterceptor;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 인터셉터는 DispatchType으로 호출을 분기처리 할 수 없음
     * -> 대신에 강력한 경로 패턴으로 분기처리 가능 (/error-page/**)
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
//                .excludePathPatterns("/css/**", "*.ico", "/error");    // 예외 터져도 호출됨
                .excludePathPatterns("/css/**", "*.ico", "/error", "/error-page/**");    //오류 페이지 경로 제외
    }

//    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter>filterFilterRegistrationBean= new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/*");

        // DispatchType지정, default는 REQUEST만!!
        // 즉, 필터는 dispatcherType의 기본값이 REQUEST이기 때문에 오류시 호출되지 않는다.
        filterFilterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return filterFilterRegistrationBean;
    }
}
