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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /**
     * 🔥 순서중요!!  @ModelAttribute 다음에 BindingResult가 와야함!!
     *
     * - @ModelAttribute에 바인딩 시 타입오류가 발생하면?
     *  1. BindingResult 없으면 -> 400오류, 컨트롤러 호출X, 오류페이지
     *  2. BindingResult 있으면 -> 오류정보(FieldError)를 BindingResult에 담아서 컨트롤러 정상호출
     *
     *  BindingResult에 검증오류를 적용하는 3가지 방법
     *  1. @ModelAttribute의 객체에 타입 오류 등으로 바인딩이 실패하는 경우 스프링이 FieldError를 생성해서 BindingResult에 넣어줌
     *  2. 개발자가 직접
     *  3. Validator 사용
     *
     *  💡 BindingResult 입장에서의 오류 2가지
     *  1. 아예 바인딩 자체가 실패한 오류
     *  2. 비즈니스와 관련된 검증 오류
    */
//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();
        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000~1,000,000원 사이여야 합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9999개입니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "총 금액은 10,000원 이상이여야 합니다."));
            }
        }
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * ⭐️ V2. 사용자가 입력한 값(거절된 값)을 유지시키기
     */
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();
        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName",item.getItemName(),false,null,null, "상품 이름은 필수 입니다."));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price",item.getPrice(),false,null,null, "가격은 1,000~1,000,000원 사이여야 합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity",item.getQuantity(),false,null,null, "수량은 최대 9999개입니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",null,null, "총 금액은 10,000원 이상이여야 합니다."));
            }
        }
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * ⭐️ V3. 에러메시지 코드화
     *
     */
//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();
        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null, null));
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000,1000000}, null));
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999}, null));
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000, resultPrice}, null));
            }
        }
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * ⭐️V4.
     *
     * 컨트롤러에서 BindingResult는 검증해야할 객체 바로 뒤에 온다, 즉 검증해야할 객체가 무엇인지 알고있다.
     *
     * bindingResult.rejectValue(...)
     * =>내부동작으로 V3의 fieldError를 만들어준다.
     * 2번째 파라미터인 errorCode는 메시지에 등록된 코드가 아님!!
     *  range.item.price => range로 적었는데 어떻게 동작하나??? => MessageCodeResolver를 알아야함
     */
//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        log.info("objectName ={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());
        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();
        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000},null);
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * ⭐️ V5.
     * 메시지코드(errors.properties)에 오류코드를 만들때 자세히만들수도있고,단순하게 만들수도 있음.
     * 자세히만든것 required.item.itemName : 아이템에서만 사용가능
     * 단순버전 required : 어디서든 사용가능 (범용성👍)
     *
     * 가장 좋은 방법
     * ✅ 범용성으로 사용하다가, 세밀하게 작성해야 하는 경우에는 세밀한 내용이 적용되도록 메시지에 단계를 두는 방법
     *
     * 정리!!
     * 1. rejectValue(호출)
     * 2. MessageCodesResolver를 사용해서 검증 오류 코드로 메시지 코드들을 생성
     * 3. new FieldError()를 생성하면서 메시지 코드들을 보관
     * 4. th:errors 에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고 => 출력
     * */
//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        log.info("objectName ={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());
        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();
        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000},null);
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * ⭐️ V6. Validator적용
     */
    @PostMapping("/add")

    public String addItemV6(@ModelAttribute Item item,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        itemValidator.validate(item, bindingResult);

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

