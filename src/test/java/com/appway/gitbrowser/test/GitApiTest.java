package com.appway.gitbrowser.test;

import com.appway.gitbrowser.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@TestPropertySource(locations = "classpath:application.properties")
public class GitApiTest {

	@Test
	public void canary_test() {
		// TODO LC implement
		assertTrue(true);
	}
}
