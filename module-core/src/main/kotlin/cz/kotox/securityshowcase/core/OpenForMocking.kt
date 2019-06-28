package cz.kotox.securityshowcase.core

/**
 * For testing purposes, annotate class with this, which opens the class for mocking (Mockito)
 * It's used in common.gradle as part of kotlin-allopen plugin
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class OpenForMocking
