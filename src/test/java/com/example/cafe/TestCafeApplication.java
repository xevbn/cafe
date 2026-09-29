package com.example.cafe;

import org.springframework.boot.SpringApplication;

public class TestCafeApplication {

    public static void main(String[] args) {
        SpringApplication.from(CafeApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
