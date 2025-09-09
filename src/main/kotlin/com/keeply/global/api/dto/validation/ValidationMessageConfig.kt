package com.keeply.global.api.dto.validation

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource

@Configuration
class ValidationMessageConfig {

    @Bean
    fun validationMessageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:ValidationMessages")
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }
}