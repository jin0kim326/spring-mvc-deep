package hello.exception.servelt;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * 스프링이 아닌 순수 서블릿컨테이너는 2가지 방식으로 예외처리 지원
 * 1. Exception (예외)
 * 2. response.sendError(Http 상태 코드, 오류 메시지)
 */
@Slf4j
@Controller
public class ServletExController {

    /**
     * WAS까지 예외가 올라감 -> 예외가 발생하면 500코드로 에러응답
     *
     * /no-page 접근시 -> 404 NotFound
     */
    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생");
    }

    /**
     * SendError() 흐름
     * -예외가 아니기때문에 WAS까지 정상리턴됨
     * response 내부에 오류가 발생했다는 상태를 저장해둠
     * 서블릿 컨테이너는 고객에게 응답전에 response의 sendError()가 호출되었는지 확인 -> 호출되었다면 설정한 오류 코드에 맞추어 기본 오류페이지 보여줌
     */
    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404오류");
    }

    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500, "500입니다");
    }
}
