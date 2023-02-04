package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class BeanValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    /**
     * 🔥 순서중요!!  @ModelAttribute 다음에 BindingResult가 와야함!!
     *
     * 스프링부트가 spring-boot-stater-validation 라이브러리가 있으면 자동으로 Bean Validator를 인지하고 스프링에 통합함
     * -> 스프링 부트는 자동으로 글로벌 Validator로 등록
     * -> 이 Validator는 애노테이션을 보고 검증을 수행, 이렇게 글로벌 Validator가 적용되어 있기에 @Valid, @Validated 만 적용하면 됨
     *
     * + @Validated - 스프링 전용 검증 애노테이션
     * + @Valid - 자바 표준 검증 애노테이션
     * => 큰차이는 없음, 단 @Validated는 groups라는 기능이 있음
     *
     * 👉🏻 검증 순서
     * 1. @ModelAttribute 각각의 필드에 타입 변환 시도
     *   -> 성공하면 다음
     *   -> 실패하면 typeMismatch로 FieldError 추가
     * 2. Validator 적용
     * => 바인딩에 성공한 필드만 Bean Validation 적용 (타입이 안맞는데, 검증은 의미가 없음!!)
     */
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

