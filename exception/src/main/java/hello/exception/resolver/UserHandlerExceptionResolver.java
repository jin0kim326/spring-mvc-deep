package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 예외가 WAS까지 전달되지 않고, 여기서 끝나버림 (return new ModelAndView())
     *
     * 정리
     * ExceptionResolver를 사용하면 컨트롤러에서 예외가 발생해도 리졸버에서 예외를 처리해버림
     * 예외가 발생해도 서블릿 컨테이너까지 예외가 전달되지 않고 스프링 MVC에서 예외 처리는 끝남.
     * 결과적으로 WAS는 정상처리 (예외를 리졸버에서 모두 처리할 수 있는것이 핵심!!)
     *
     * 단, 이 리졸버를 직접 구현하려고 하니 복잡함, 스프링이 제공하는 ExceptionResolver는 이문제를 해결해줌
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if ( ex instanceof UserException) {
                log.info("UserException Resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                if("application/json".equals(acceptHeader)) {
                    // JSON 통신인 경우
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());
                    // 객체(json) => String
                    String result = objectMapper.writeValueAsString(errorResult);

                    response.setContentType("applecation/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);
                    return new ModelAndView();
                } else {
                    // TEXT/HTML인 경우
                    return new ModelAndView("error/500");
                }
            }
        }catch(IOException e) {
            log.error("resolver ex", e);
        }
        return null;
    }
}
