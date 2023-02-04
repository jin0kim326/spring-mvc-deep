package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

//@SpringBootTest
public class BeanValidationTest {
    @Test
    public void vaild() throws Exception {
        // given

        /**
         * 검증기 생성
         * -> 스프링을 사용하면 적어줄 필요 없으니 참고만
         * */
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // when
        Item item = new Item();
        item.setItemName(" ");
        item.setPrice(0);
        item.setQuantity(10000);

        Set<ConstraintViolation<Item>> validations = validator.validate(item);
        for (ConstraintViolation<Item> validation : validations) {
            System.out.println("validation = " + validation);
            System.out.println("validation = " + validation.getMessage());
        }


        // then
    }
}
