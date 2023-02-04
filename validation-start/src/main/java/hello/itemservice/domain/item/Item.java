package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @NotNull, @NotBlank...
 * javax.validation 은 특정 구현에 관계없이 표준 인터페이스 (어디서든 동작)
 *
 * @Range
 * -org.hibernate.validator는 하이버네이트 validator 구현체를 사용할때만 제공되는 검증기능,
 * 실무에서도 대부분 하이버네이트validator를 사용하니 무방함
 *
 * + SciprtAssert로 오브젝트(글로벌)오류를 처리하기에는 제약이있음, 그냥 자바코드로 처리
 */
@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총합이 10,000원 이상이 되어야합니다.")
public class Item {

    private Long id;
    @NotBlank   // 빈값 + 공백 허용X .
    private String itemName;

    @NotNull    // null 허용 X .
    @Range(min = 1000, max=1000000)
    private Integer price;

    @NotNull    //.
    @Max(9999)  //.
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
