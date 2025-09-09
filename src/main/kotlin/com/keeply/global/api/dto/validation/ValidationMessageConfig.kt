package com.keeply.global.api.dto.validation

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource

@Configuration
class ValidationMessageConfig {

    /**
     * Creates a reloadable MessageSource for validation messages.
     *
     * This provides a Spring-managed MessageSource bean backed by a ReloadableResourceBundleMessageSource
     * with basename "classpath:ValidationMessages" and UTF-8 default encoding, enabling internationalized
     * validation message resolution from ValidationMessages*.properties files on the classpath.
     *
     * @return a configured MessageSource ready for use by validation and message resolution
     */
    @Bean
    fun validationMessageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:ValidationMessages")
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }
}