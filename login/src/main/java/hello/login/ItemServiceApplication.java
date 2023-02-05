package hello.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 패키지 구조
 * domain / web
 *
 * 도메인 : 화면,ui,기술 인프라 등등의 영역은 제외한 시스템이 구현해야 하는 핵심 비즈니스 업무영역
 * web을 다른 기술로 바꾸어도 도메인은 그대로 유지 !!
 *
 * =>
 * < web은 도메인을 알고있지만, domain은 web을 모르도록 설계 >
 * */
@Slf4j
@SpringBootApplication
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
		log.info("✅ server started...");
	}

}
