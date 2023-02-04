package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.*;

public class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    public void messageCodesResolverObject() throws Exception {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        assertThat(messageCodes).containsExactly("required.item", "required");
    }

    @Test
    public void messageCodeResolverField() throws Exception {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );

        /**
         * 🔥  DefaultMessageCodesResolver의 기본 메시지 생성규칙
         * 1️⃣ 객체 오류 (코드:required, 객체:item 인경우)
         * 1.code.object name  (required.item)
         * 2.code             (required)
         *
         * 2️⃣ 필드 오류 (코드:required, 객체:item , field:count 인경우)
         * 1.code.object name.field (required.item.count)
         * 2.code.field             (required.count)
         * 3.code.filed type        (required.int) : 숫자로 들어와야합니다 등
         * 4.code                   (required)
         */

    }
}
