package se.sundsvall.selfserviceai;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import se.sundsvall.dept44.ServiceApplication;

@ServiceApplication
@EnableFeignClients
@EnableAsync
public class Application {
	public static void main(final String... args) {
		run(Application.class, args);
	}
}
