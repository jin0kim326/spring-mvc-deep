package hello.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ApiExceptionController {

    @GetMapping("/api/member/{id}")
    public MemberDto exceptionTest(
            @PathVariable("id") String id
    ) {
        if (id.equals("ex")) {
            throw new RuntimeException("예외 발생");
        }

        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        return new MemberDto("spring", "jinyoung");
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
