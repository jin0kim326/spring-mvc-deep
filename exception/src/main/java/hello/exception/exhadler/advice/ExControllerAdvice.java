package hello.exception.exhadler.advice;

import hello.exception.api.ApiExceptionV3Controller;
import hello.exception.exception.UserException;
import hello.exception.exhadler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("hello.exception.api")
//@RestControllerAdvice(assignableTypes = {ApiExceptionV3Controller.class})
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD !!", e.getMessage());
    }

    //    @ExceptionHandler(UserException.class) // 생략가능
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity(errorResult,HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception이라는것은 "최상위 예외"
     * 즉, 이 컨트롤러에서 처리하지 못하는 예외를 의미한다.
     * 이 예제 컨트롤러에서는 RuntimeException을 처리하는 ExceptionHandler가 없기때문에
     * RuntimeException이 발생하면 이 핸들러가 호출 된다.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler (Exception e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }
}
