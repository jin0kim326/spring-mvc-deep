package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    /**
     * ⭐️V1
     * HTTP 쿼리 스트링으로 전달하는 data는 숫자가 아니라 문자
     * 형변환을 개발자가 해준 버전
     */
    @GetMapping("hello-v1")
    public String helloV1(HttpServletRequest request) {
        String data = request.getParameter("data");
        Integer intValue = Integer.valueOf(data);
        System.out.println("intValue = " + intValue);

        return "ok";
    }

    /**
     * ⭐️V2
     * @RequestParam을 이용해서 형변환
     * 스프링이 내부에서 형변환을 해준다.
     *
     * 스프링의 타입 변환 적용 예
     * 1. 요청 파라미터 (@RequestParam, @ModelAttribute, @PathVariable)
     * 2. @Value 등으로 YML 정보 읽기
     * 3. XML에 넣은 스프링 빈 정보 변환
     * 4. 뷰를 랜더링 할때
     *
     * 그 외 새로운 타입을 만들어서 변환하고 싶을때에는 컨버터 인터페이스를 구현해서 등록만 하면됨
     *
     */
    @GetMapping("hello-v2")
    public String helloV2(@RequestParam Integer data) {
        System.out.println("data = " + data);
        // + 포맷터가 적용되면 "10,000" -> 10000으로 잘 된다
        return "ok";
    }

    /**
     * http://localhost:8089/ip-port?ipPort=127.0.0.1:8080 => IpPort 객체 타입 변환
     */
    @GetMapping("/ip-port")
    public String ip(@RequestParam IpPort ipPort) {
        System.out.println(ipPort.getIp());
        System.out.println(ipPort.getPort());
        return "ok";
    }
}
