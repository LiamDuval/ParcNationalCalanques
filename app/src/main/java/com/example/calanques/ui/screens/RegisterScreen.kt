package com.example.calanques.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calanques.R
import com.example.calanques.viewmodel.RegisterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier.size(120.dp, 80.dp).padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Créer un compte", 
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold, 
            color = Color.Black, // PLUS FONCÉ
            modifier = Modifier.align(Alignment.Start)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = Color(0xFFE0E0E0))

        Spacer(modifier = Modifier.height(16.dp))

        // --- PRÉNOM ET NOM ---
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Prénom :", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                OutlinedTextField(
                    value = viewModel.prenom, 
                    onValueChange = { viewModel.prenom = it }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Nom :", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                OutlinedTextField(
                    value = viewModel.nom, 
                    onValueChange = { viewModel.nom = it }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        }
//
        Spacer(modifier = Modifier.height(12.dp))

        // --- EMAIL ---
        Text("E-mail :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        OutlinedTextField(
            value = viewModel.email, 
            onValueChange = { viewModel.email = it }, 
            placeholder = { Text("john@example.com") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- TÉLÉPHONE ---
        Text("Téléphone :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        OutlinedTextField(
            value = viewModel.telephone, 
            onValueChange = { viewModel.telephone = it }, 
            placeholder = { Text("0601020304") }, 
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- ADRESSE ET VILLE ---
        Text("Adresse :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        OutlinedTextField(
            value = viewModel.adresse, 
            onValueChange = { viewModel.adresse = it }, 
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Code Postal :", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                OutlinedTextField(
                    value = viewModel.cp, 
                    onValueChange = { viewModel.cp = it }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Ville :", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                OutlinedTextField(
                    value = viewModel.ville, 
                    onValueChange = { viewModel.ville = it }, 
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- GENRE ---
        Text("Genre :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            RadioButton(selected = viewModel.gender, onClick = { viewModel.gender = true })
            Text("Homme", fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = !viewModel.gender, onClick = { viewModel.gender = false })
            Text("Femme", fontSize = 14.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- DATE DE NAISSANCE ---
        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            viewModel.birthDate = sdf.format(Date(millis))
                        }
                        showDatePicker = false
                    }) { Text("Confirmer", color = Color(0xFFE51A2E), fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Annuler", color = Color.DarkGray) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Text("Date de naissance :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
            OutlinedTextField(
                value = viewModel.birthDate,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledPlaceholderColor = Color.Gray,
                    disabledLabelColor = Color.Gray,
                    disabledContainerColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- FAMILLE ET MOTS DE PASSE ---
        Text("Membres de la famille :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        OutlinedTextField(
            value = viewModel.familyCount, 
            onValueChange = { viewModel.familyCount = it }, 
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Mot de passe :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        OutlinedTextField(
            value = viewModel.password, 
            onValueChange = { viewModel.password = it }, 
            visualTransformation = PasswordVisualTransformation(), 
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Confirmer le mot de passe :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        OutlinedTextField(
            value = viewModel.confirmPassword, 
            onValueChange = { viewModel.confirmPassword = it }, 
            visualTransformation = PasswordVisualTransformation(), 
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        viewModel.errorMessage?.let {
            Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onRegisterClick(onRegisterSuccess) },
            modifier = Modifier.width(180.dp).height(45.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0B0B0)) // Gris plus foncé
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text("S'inscrire", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBackToLogin) {
            Text("Déjà un compte ? Connectez-vous", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
