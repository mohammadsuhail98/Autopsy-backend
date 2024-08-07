package uma.autopsy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AutopsyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutopsyApplication.class, args);
	}

}
