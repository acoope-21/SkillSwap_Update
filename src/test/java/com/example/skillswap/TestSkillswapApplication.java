package com.example.skillswap;

import org.springframework.boot.SpringApplication;

public class TestSkillswapApplication {

	public static void main(String[] args) {
		SpringApplication.from(SkillswapApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
