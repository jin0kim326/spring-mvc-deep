package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.*;

/**
 * ⭐️ 인터페이스 분리 원칙 - ISP
 * => 클라이언트가 자신이 이용하지 않는 메서드에 의존하지 않아야한다.
 *
 * DefaultConversionService 인터페이스는 ConversionService, ConverterRegistry를 구현했음
 *  * ConversionService -> 컨버터 사용에 초점
 *  * ConverterRegistry -> 컨버터 등록에 초점
 *
 *  
 */
public class ConversionServiceTest {
    @Test
    public void conversionService() throws Exception {
        //등록
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new StringToIntegerConverter());
        conversionService.addConverter(new IntegerToStringConverter());
        conversionService.addConverter(new IpPortToStringConverter());
        conversionService.addConverter(new StringToIpPortConverter());
        
        //사용
        assertThat(conversionService.convert("10", Integer.class)).isEqualTo(10);
        assertThat(conversionService.convert(10, String.class)).isEqualTo("10");
        assertThat(conversionService.convert("127.0.0.1:8080", IpPort.class)).isEqualTo(new IpPort("127.0.0.1", 8080));
        assertThat(conversionService.convert(new IpPort("127.0.0.1", 8080), String.class)).isEqualTo("127.0.0.1:8080");


        
    }
}
