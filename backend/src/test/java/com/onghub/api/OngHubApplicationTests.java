package com.onghub.api;

import com.onghub.api.config.MailTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(MailTestConfig.class)
class OngHubApplicationTests {

	@Test
	void contextLoads() {
	}

}
