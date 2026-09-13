package com.example.habittt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.habittt.mapper") // 确保扫描到 Mapper 接口
@EnableScheduling
public class HabitttApplication {

	public static void main(String[] args) {
		SpringApplication.run(HabitttApplication.class, args);
	}

}


