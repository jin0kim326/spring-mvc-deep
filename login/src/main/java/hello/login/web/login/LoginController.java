package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
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
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    @PostMapping("/login")
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

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
