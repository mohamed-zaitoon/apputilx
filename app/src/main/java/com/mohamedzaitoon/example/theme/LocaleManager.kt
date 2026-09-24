package com.mohamedzaitoon.example.theme

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {

    fun localeFor(language: Language): Locale = when (language) {
        Language.SYSTEM -> Locale.getDefault()
        Language.ENGLISH -> Locale.ENGLISH
        Language.ARABIC -> Locale.Builder().setLanguage("ar").build()
    }

    fun isRtl(language: Language, context: Context): Boolean {
        return when (language) {
            Language.SYSTEM -> {
                val sysLocale = context.resources.configuration.locales[0] ?: Locale.getDefault()
                sysLocale.language.equals("ar", ignoreCase = true)
            }
            Language.ENGLISH -> false
            Language.ARABIC -> true
        }
    }

    fun wrapContext(base: Context, language: Language): Context {
        if (language == Language.SYSTEM) return base

        val locale = localeFor(language)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)

        return base.createConfigurationContext(config)
    }
}
