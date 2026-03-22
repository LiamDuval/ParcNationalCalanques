package com.example.calanques.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calanques.R
import com.example.calanques.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(150.dp, 100.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo"
            )
        }
//
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Se connecter",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black, // PLUS FONCÉ
            modifier = Modifier.align(Alignment.Start)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = Color(0xFFE0E0E0))

        Spacer(modifier = Modifier.height(16.dp))

        Text("Nom d'utilisateur :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        OutlinedTextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it },
            placeholder = { Text("johndoe") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Mot de passe :", modifier = Modifier.align(Alignment.Start), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            placeholder = { Text("********") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
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
            onClick = { viewModel.onLoginClick(onLoginSuccess) },
            modifier = Modifier
                .width(150.dp)
                .height(45.dp),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0B0B0)) // Bouton gris plus foncé
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Text("Valider", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        TextButton(onClick = { /* TODO: Mot de passe oublié */ }) {
            Text("Mot de passe oublié", color = Color.DarkGray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
        Spacer(modifier = Modifier.height(20.dp))

        Text("Pas de compte ?", fontSize = 14.sp, color = Color.Black)

        Button(
            onClick = onNavigateToRegister,
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0B0B0)),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Créez-en un !", color = Color.White, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
