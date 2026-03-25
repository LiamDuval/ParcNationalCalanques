package com.example.calanques.data.repository

import ActiviteItem
import BasketDao
import kotlinx.coroutines.flow.Flow

class BasketRepository(private val basketDao: BasketDao) {

    // Récupère le flux de données depuis Room
    val allItems: Flow<List<ActiviteItem>> = basketDao.getAllItems()

    suspend fun add(item: ActiviteItem) {
        basketDao.insert(item)
    }

    suspend fun update(item: ActiviteItem) {
        basketDao.update(item)
    }

    suspend fun delete(name: String) {
        basketDao.deleteByName(name)
    }

    suspend fun clear() {
        basketDao.clearBasket()
    }

    // Ici, tu pourras ajouter la logique réseau plus tard
    // ex: suspend fun syncWithServer() { ... }
}