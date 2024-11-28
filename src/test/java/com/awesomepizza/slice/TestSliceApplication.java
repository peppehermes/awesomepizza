package com.awesomepizza.slice;

import org.springframework.boot.SpringApplication;

public class TestSliceApplication {

	public static void main(String[] args) {
		SpringApplication.from(SliceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
