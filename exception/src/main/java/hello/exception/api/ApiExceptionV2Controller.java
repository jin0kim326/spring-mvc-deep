package hello.exception.api;

import hello.exception.exception.UserException;
import hello.exception.exhadler.ErrorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api2")
public class ApiExceptionV2Controller {


    @GetMapping("/member/{id}")
    public ApiExceptionController.MemberDto exceptionTest(
            @PathVariable("id") String id
    ) {
        if (id.equals("ex")) {
            throw new RuntimeException("예외 발생");
        }


        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        if (id.equals("user-ex")) {
            throw new UserException("사용자 오류");
        }

        return new ApiExceptionController.MemberDto("spring", "jinyoung");
    }
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }
}
