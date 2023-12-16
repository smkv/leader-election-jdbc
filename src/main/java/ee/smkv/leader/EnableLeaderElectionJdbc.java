package ee.smkv.leader;

import java.lang.annotation.*;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LeaderElectionConfiguration.class)
public @interface EnableLeaderElectionJdbc {
}
