package com.example.numblebankingserverchallenge.config

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class SessionLogin(val admin:Boolean = false)
