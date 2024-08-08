package uma.autopsy.GlobalProperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "case")
@Getter @Setter
public class GlobalProperties {
    private String baseDir;
}
