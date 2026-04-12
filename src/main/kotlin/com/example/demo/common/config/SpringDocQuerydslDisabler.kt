package com.example.demo.common.config

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.annotation.Configuration

/**
 * springdoc-openapi 2.x + Spring Boot 4.0 (Spring Framework 7.x) 호환성 문제 해결.
 * springdoc의 QueryDSL 커스터마이저 빈이 Spring Framework 7.x에서 bean post-processing 실패를 일으키므로
 * 빈 정의 등록 단계에서 해당 빈을 제거한다.
 */
@Configuration
class SpringDocQuerydslDisabler : BeanDefinitionRegistryPostProcessor {

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val beanName = "queryDslQuerydslPredicateOperationCustomizer"
        if (registry.containsBeanDefinition(beanName)) {
            registry.removeBeanDefinition(beanName)
        }
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {}
}
