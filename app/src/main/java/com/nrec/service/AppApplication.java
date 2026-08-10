package com.nrec.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenjia
 * @date 2025/06/05
 */

@SpringBootApplication(exclude = {
        com.nrec.base.configrest.autoconfig.ConfigRestConfiguration.class
})
@Slf4j
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
}
