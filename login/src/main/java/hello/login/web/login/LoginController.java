package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Vesion
 * V1 : 단순 쿠키 사용
 * V2 : 서버(세션 저장소) 사용
 * V3 : 서블릿 세션 사용
 *
 * 현재 쿠키의 문제점
 * 1. 쿠키값을 변조가능 : memberId:1 , memberId:2 => 해커가 특정멤버로 로그인하기 너무 쉬운구조
 * 2. 쿠키에 보관하는 정보는 클라이언트 해킹 가능 : 주민번호,신용카드정보 등
 * 3. 해커 쿠키 탈취 후 사용
 *
 * 해결방안 : 서버세션을 사용
 * 1. 쿠키값 변조 => 세션아이디를 랜덤값으로 줌으로써 예측불가
 * 2. 쿠키 정보 해킹 => 아무 의미없는 정보만 저장 (민감정보저장x)
 * 3. 해커 쿠키 탈취 후 사용 => 일정시간(ex.30분) 후 만료
 *
 * V2. 세션이란, 쿠키를 사용하지만 서버에서 데이터를 유지하는 방법임
 * => 프로젝트마다 이러한 세션 개념을 직접 개발하는것은 상당히 불편 => 서블릿이 세션 개념을 지원
 *
 * 브라우저 최초 세션사용시 URL에 jsessionID 가 들어가는이유
 * => 웹브라우저가 쿠키를 지원하지 않은 경우, url로 세션을 유지하기위해서..
 * => 번거로워서 옵션으로 끄고 사용하는편
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                        HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember ==null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        //쿠키에 시간 정보를 주지않으면 "세션 쿠키" => 브라우저 종료시 세션삭제
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                        HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember ==null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        sessionManager.createSession(loginMember, response);

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                          HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember ==null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        //create(파라미터) 값
        // true(default) : 세션있으면 기존세션 반환, 없으면 새로운 세션을 생성해서 반환
        // false :         세션있으면 기존세션 반환, 없으면 새로운 세션을 생성X -> null 반환
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);   //세션에 로그인 회원 정보 보관

        return "redirect:/";
    }


    //    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
