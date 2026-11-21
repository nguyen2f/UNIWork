package com.uniwork;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Integration test requires live database connection")
class UniWorkApplicationTests {

	@Test
	void contextLoads() {
	}

}
