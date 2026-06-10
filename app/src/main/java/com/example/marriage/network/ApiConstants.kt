package com.example.marriage.network

object ApiConstants {
    // FIX: Sahi URL format (Ya toh IP use karein ya localhost)
    const val BASE_URL = "http://192.168.29.73:3000/" 

    const val REGISTER        = "api/auth/register"
    const val LOGIN           = "api/auth/login"
    const val REGISTRATION_STATUS = "api/registration/status"
}
