package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConverterController {

    /**
     * ${} : 그냥 출력
     * ${{}} : 컨버전 서비스 적용
     */
    @GetMapping("/converter-view")
    public String converterView(Model model) {
        model.addAttribute("number", 10000);
        model.addAttribute("ipPort", new IpPort("127.0.0.1", 8089));
        return "converter-view";
    }

    /**
     * 타임리프의 th:field 는 자동으로 컨버터기능을 사용하게 해줌 (컨버전 서비스 적용)
     * -> 컨버터를 사용하기 싫다면 th:value
     *
     * 제출 하기 클릭시 스트링형태("127.0.0.1:8089"로 컨트롤러에 넘어옴 -> @ModelAttribute를 사용해서 IpPort 타입으로 변환함)
     */
    @GetMapping("/converter/edit")
    public String converterForm(Model model) {
        IpPort ipPort = new IpPort("127.0.0.1", 8089);
        Form form = new Form(ipPort);
        model.addAttribute("form", form);
        return "converter-form";
    }

    @PostMapping("/converter/edit")
    public String converterEdit(@ModelAttribute Form form, Model model) {
        IpPort ipPort = form.getIpPort();
        model.addAttribute("ipPort", ipPort);
        return "converter-view";
    }

    @Data
    static class Form {
        private IpPort ipPort;

        public Form(IpPort ipPort) {
            this.ipPort = ipPort;
        }
    }
}
