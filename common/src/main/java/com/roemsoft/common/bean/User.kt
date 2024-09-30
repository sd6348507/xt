package com.roemsoft.common.bean

data class User(
    val code: String
) {
    fun isRoom1() = code.startsWith('Y', true)
    fun isRoom2() = code.startsWith('Y', true)
    fun isGfp() = code.startsWith('G', true)
    fun isRoom4() = code.startsWith('F', true)
    fun isQp() = code.startsWith('Q', true)
}