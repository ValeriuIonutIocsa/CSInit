package com.personal.scripts.gen.cs_init;

import org.junit.jupiter.api.Test;

class AppStartCSInitTest {

	@Test
	void testMain() {

		final String csProjectPathString = "C:\\IVI\\Prog\\CS\\SamplePrj";

		final String[] args = { csProjectPathString };
		AppStartCSInit.main(args);
	}
}
