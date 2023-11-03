package com.wd.woodong2.domain.provider

interface TokenProvider {
    fun getToken(): String?
}