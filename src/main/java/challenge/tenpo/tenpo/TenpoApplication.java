package challenge.tenpo.tenpo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TenpoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenpoApplication.class, args);
	}

}
