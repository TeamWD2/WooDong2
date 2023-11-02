package com.wd.woodong2.domain.provider

interface TokenProvider {
    suspend fun getToken(): String
}