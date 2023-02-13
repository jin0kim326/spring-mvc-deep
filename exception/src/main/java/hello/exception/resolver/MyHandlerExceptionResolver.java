package hello.exception.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {
    /**
     * ExceptionResolver가 ModelAndView를 반환하는 이유는 마치 try,catch를 하듯이, Exception을 처리해서 정상 흐름처럼 변경하는 것이 목적이다.
     * 이름 그대로 Exception을 Resolver(해결)하는 것이 목적이다.
     *
     * 이 리졸버 예시는 IllegalArgumentException이 발생하면 response.sendError(400)을 호출해서 Http상태코드를 400으로 지정하고, 빈 ModelAndView를 반환한다.
     *
     * 반환값에 따른 동작 방식 (리졸버의 반환값에 따른 DispatcherServlet의 동작방식)
     * 1. 빈 ModelAndView :  빈 ModelAndView를 반환하면 뷰를 랜더링 하지 않고, 정상흐름으로 서블릿이 리턴
     * 2. ModelAndView 지정 : ModelAndView에 뷰, 모델 등의 정보를 지정해서 반환하면 뷰를 렌더링
     * 3. null : 다음 ExceptionResolver를 찾아서 실행 -> 만약 처리할 수 있는 리졸버가 없으면 예외 처리가 안되고, 기존에 발생한 예외를 서블릿 밖으로 던짐
     *
     * 🔥 ExceptionResolver 활용
     * 1. 예외 상태 코드 변환
     *   - 예외를 response.sendError(xxx) 호출로 변경해서 상태코드에 따른 오류를 처리하도록 위임
     * 2. 뷰 템플릿 처리
     *    - ModelAndView에 model,view를 담아 뷰 랜더링 처리 가능
     * 3. API 응답 처리
     *    - reponse.getWriter().println(xxx); 처럼 Http응답 바디에 직접 테이를 넣어주는것도 가능 (JSON으로 응답하면 API처리도 가능)
     *
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try{
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());    // exception->sendError로 바꿔치기
                return new ModelAndView();
            }
        } catch (IOException e) {
            log.error("resolver ex", e);
        }
        return null;
    }
}
