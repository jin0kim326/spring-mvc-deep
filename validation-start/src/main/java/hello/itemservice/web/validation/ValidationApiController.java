package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationApiController {

    /**
     * API는 3가지 경우
     * 1. 성공
     * 2. 실패 요청 : JSON을 객체로 생성하는것 자체 실패 (바인딩 오류 등)
     * 3. 검증 오류 요청 : JSON을 객체로 생성하는것은 성공 -> 검증에서 실패
     *
     * 🔥 @ModelAttribute vs @RequestBody
     * - @ModelAttribute는 필드 단위로 정교하게 바인딩이 적용, 특정 필드가 바인딩 되지 않아도 나머지 필드는 정상 바인딩 되고,
     * Validator를 사용한 검증도 적용가능
     *
     * - @RequestBody는 HttpMessageConverter단계에서 JSON데이터를 객체로 변경하지 못하면 이후 단계 자체가 진행X
     * 예외발생 및 컨트롤러 호출X => Validator 적용할 수 없음
     *
     *
     */
    @PostMapping("/add")
    public Object addItem(
            @RequestBody @Validated ItemSaveForm form,
            BindingResult bindingResult
    ) {
        log.info("API 컨트롤러 호출");

        if( bindingResult.hasErrors() ) {
            log.info("검증 오류 발생 errors= {}", bindingResult);

            return bindingResult.getAllErrors();
            // getAllErrors() => ObjectError, FieldError를 반환
            // 실무에서는 다듬어서 반환해야함
        }

        log.info("성공 로직 실행");
        return form;
    }
}
