import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.project3.R


@Composable
fun LoginScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loggedIn by remember { mutableStateOf(true) }
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painterResource(R.drawable.menu_bg),
            contentDescription = "Background_img",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Welcome to Cyber Escape!\r\n\nPlease Login",
                    modifier = Modifier
                        .padding(25.dp)
                        .background(Color.White)
                        .fillMaxSize(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center

                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painterResource(R.drawable.baseline_account_box_24),
                    contentDescription = "Account Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .background(Color.White)
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    Modifier
                        .padding(16.dp)
                        .border(BorderStroke(width = 2.dp, color = Color.Black))
                        .background(Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    label = { Text("Email") },
                )


                TextField(
                    value = password,
                    onValueChange = { password = it },
                    Modifier
                        .padding(16.dp)
                        .border(BorderStroke(width = 2.dp, color = Color.Black))
                        .background(Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    label = {
                        Text("Password")
                    },
                )
                Button(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(60.dp)
                        .width(200.dp),
                    onClick = {
                        if (validateLogin(email, password)) {
                            Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show()
                            loginSuccess()
                            navController.navigate(BottomNavigationItems.GameScreen.route)

                        } else {
                            Toast.makeText(context, "Email or Password Error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                ) {
                    Text(
                        text = "Login",
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(50.dp)
                        .align(Alignment.CenterHorizontally)
                )
                {
                    Text(
                        text = "Not signed Up? Click here to join!",
                        color = Color.Cyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .background(color = Color.White)
                            .fillMaxSize()
                            .clickable {
                                navController.navigate(BottomNavigationItems.SignupScreen.route)
                            },

                        )
                }
            }
        }
    }
}

fun loginSuccess() {
    val loggedIn = true
}

fun validateLogin(email: String, password: String): Boolean {
    return true
}

@Preview(name = "landscape", widthDp = 800, heightDp = 600)

@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}