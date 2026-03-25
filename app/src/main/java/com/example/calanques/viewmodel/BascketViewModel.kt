package com.example.calanques.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calanques.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.time.LocalTime

class BasketViewModel : ViewModel() {

    // class des variable de l'activité
    data class ActiviteItem(val idActivite: Int = 0,val id:Int, val name: String, val price: Double, val nbPlace: Int, val date: String, val hourly: LocalTime)

    //Liste des activités
    private val _items = MutableStateFlow<List<ActiviteItem>>(emptyList())
    // État exposé à la vue (lecture seule)
    val items: StateFlow<List<ActiviteItem>> = _items.asStateFlow()

    // Ajouter une activité
    fun addActivite(activite: ActiviteItem) {
        val currentList = _items.value.toMutableList()
        currentList.add(activite)
        _items.value = currentList
    }

    // Modifier une activité
    fun updateActivite(name: String, newNbPlace: Int, newDate: String, newHourly: LocalTime) {
        _items.value = _items.value.map { item ->
            if (item.name.equals(name, ignoreCase = true)) {
                // .copy() permet de ne changer que les champs souhaités
                item.copy(nbPlace = newNbPlace, date = newDate, hourly = newHourly)
            } else {
                item
            }
        }
    }

    // Retirer une activité
    fun removeActivite(name: String) {
        _items.value = _items.value.filter { it.name != name }
    }

    // Calculer le total
    fun calculateTotal(): Double {
        return _items.value.sumOf { it.price * it.nbPlace }
    }

    // Vider le panier
    fun clearBasket() {
        _items.value = emptyList()
    }

    //validation du panier
   /*fun checkout() {
        if (_items.value.isNotEmpty()) {
            @POST("/api/activites")
            suspend fun postPanier(@Body activites: List<BasketViewModel.ActiviteItem>): Response<Unit> {

            }
            clearBasket()
        }
    }*/
}