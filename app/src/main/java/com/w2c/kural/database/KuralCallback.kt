package com.w2c.kural.database

interface KuralCallback {
    fun update(kural: Kural?)
    fun updateList(kural: List<Kural?>?)
}