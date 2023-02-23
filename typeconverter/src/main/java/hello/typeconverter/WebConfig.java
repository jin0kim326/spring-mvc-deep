package hello.typeconverter;

import hello.typeconverter.converter.IntegerToStringConverter;
import hello.typeconverter.converter.IpPortToStringConverter;
import hello.typeconverter.converter.StringToIntegerConverter;
import hello.typeconverter.converter.StringToIpPortConverter;
import hello.typeconverter.formatter.MyNumberFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 우선순위 떄문에 포맷터 등록시 주석처리 (컨버터,포맷터 둘다 적용시 컨버터가 우선순위가 있음)
//        registry.addConverter(new IntegerToStringConverter());
//        registry.addConverter(new StringToIntegerConverter());
        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());

        //추가
        registry.addFormatter(new MyNumberFormatter());
        /**
         * 이 포멧터 컨버전서비스는 JSON -> Object / Object -> JSON 변환할때는 적용되지 않음!!!
         * Jackson 라이브러리가 지원하는 포맷터를 적용해주어야함
         */
    }
}
