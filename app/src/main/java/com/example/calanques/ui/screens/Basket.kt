package com.example.calanques.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calanques.model.Basket
import com.example.calanques.viewmodel.BasketViewModel
import androidx.room.Entity
import androidx.room.PrimaryKey

private val Rouge     = Color(0xFFE51A2E)
private val Noir      = Color(0xFF000000)
private val GrisClair = Color(0xFFBBBBBB)
private val Gris      = Color(0xFF555555)
private val FondPage  = Color(0xFFF4F4F2)
private val BlancCard = Color(0xFFFFFFFF)

@Composable
fun BasketItemRow(
    item: BasketViewModel.ActiviteItem,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${item.date} à ${item.hourly}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${item.nbPlace} place(s) x ${item.price}€",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "${item.price * item.nbPlace}€",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = PhosphorIcons.Duotone.Trash,
                    contentDescription = "Supprimer l'activité",
                    tint = Rouge
                )
            }
        }
    }
}