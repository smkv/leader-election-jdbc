package ee.smkv.leader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.LinkedHashSet;
import java.util.Set;

public class LeaderElectionBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderElectionBeanPostProcessor.class);
    private final Set<LeaderElectionListener> listeners = new LinkedHashSet<>();


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LeaderElectionListener) {
            LOGGER.info("Adding bean {} listener", beanName);
            listeners.add((LeaderElectionListener) bean);
        }
        return bean;
    }

    public Set<LeaderElectionListener> getListeners() {
        return listeners;
    }
}
