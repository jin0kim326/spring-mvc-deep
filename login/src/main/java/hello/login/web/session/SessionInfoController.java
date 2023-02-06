package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 세션은 사용자가 로그아웃을 호출 -> 서버에서 session.invalidate()를 호출해야 삭제됨
 * but 대부분의 사용자는 로그아웃을 클릭안하고 브라우저를 종료함
 *
 * 문제는 http가 비연결성이므로 서버입장에서는 해당 사용자가 웹브라우저를 종료한것인지 아닌지 인식 불가
 * 이렇게 남아있는 세션을 무한정 보관하면 다음과 같은 문제 발생
 * 1. 세션과 관련된 쿠키를 탈취당했을경우 오랜시간 동안 악의적으로 사용가능
 * 2. 세션은 기본적으로 메모리에 생성, 아웃 오브 메모리
 *
 * ⭐️ 세션의 종료 시점
 * 1. 세션 생성을 기준으로 30분 ?
 *   => 30분마다 재로그인 해야하는 불편함...
 * 2. 서버로부터 온 요청을 기준으로 30분 ?
 *   => 사용자가 요청을 하면 세션주기를 갱신 -> httpSession은 이방식을 채택
 *
 */
@Slf4j
@RestController
public class SessionInfoController {
    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if ( session == null ) {
            return "세션이 없습니다.";
        }

        // session 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session name ={}, value={}", name , session.getAttribute(name)));

        log.info("sessionId={}", session.getId());
        log.info("getMaxInactiveInterval={}", session.getMaxInactiveInterval());    //비활성화 시키는시간(s) default 1800s(30분)
        log.info("creationTime={}", new Date(session.getCreationTime()));           //세션이 만들어진 시간
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));   //마지막 세션 접근 시간
        log.info("isNew={}", session.isNew());                                      //세션 생성 여부

        return "세션 출력";
    }
}
