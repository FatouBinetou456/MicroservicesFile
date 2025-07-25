package sn.uam.polytech.misid.config;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.MergedContextConfiguration;
import tech.jhipster.config.JHipsterConstants;

public class SqlTestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

    private Logger log = LoggerFactory.getLogger(SqlTestContainersSpringContextCustomizerFactory.class);

    private static SqlTestContainer prodTestContainer;

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        return new ContextCustomizer() {
            @Override
            public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
                ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
                TestPropertyValues testValues = TestPropertyValues.empty();
                EmbeddedSQL sqlAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass, EmbeddedSQL.class);
                boolean usingTestProdProfile = Arrays.asList(context.getEnvironment().getActiveProfiles()).contains(
                    "test" + JHipsterConstants.SPRING_PROFILE_PRODUCTION
                );
                if (null != sqlAnnotation && usingTestProdProfile) {
                    log.debug("detected the EmbeddedSQL annotation on class {}", testClass.getName());
                    log.info("Warming up the sql database");
                    if (null == prodTestContainer) {
                        try {
                            Class<? extends SqlTestContainer> containerClass = (Class<? extends SqlTestContainer>) Class.forName(
                                this.getClass().getPackageName() + ".MariadbTestContainer"
                            );
                            prodTestContainer = beanFactory.createBean(containerClass);
                            beanFactory.registerSingleton(containerClass.getName(), prodTestContainer);
                            /**
                             * ((DefaultListableBeanFactory)beanFactory).registerDisposableBean(containerClass.getName(), prodTestContainer);
                             */
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    testValues = testValues.and(
                        "spring.r2dbc.url=" +
                        prodTestContainer.getTestContainer().getJdbcUrl().replace("jdbc", "r2dbc").replace("mariadb", "mysql") +
                        "?useLegacyDatetimeCode=false"
                    );
                    testValues = testValues.and("spring.r2dbc.username=" + prodTestContainer.getTestContainer().getUsername());
                    testValues = testValues.and("spring.r2dbc.password=" + prodTestContainer.getTestContainer().getPassword());
                    testValues = testValues.and(
                        "spring.liquibase.url=" + prodTestContainer.getTestContainer().getJdbcUrl() + "?useLegacyDatetimeCode=false"
                    );
                }
                testValues.applyTo(context);
            }

            @Override
            public int hashCode() {
                return SqlTestContainer.class.getName().hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return this.hashCode() == obj.hashCode();
            }
        };
    }
}
