package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdSimulationDialog
import com.example.ui.components.AuthDialog
import com.example.ui.components.EditUidDialog
import com.example.ui.components.PaymentCheckoutDialog
import com.example.ui.screens.AccountHistoryScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.EarnCoinsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RechargesScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isShowingPaymentModal by viewModel.isShowingPaymentModal.collectAsState()
    val selectedPackForCheckout by viewModel.selectedPackForCheckout.collectAsState()
    val isShowingAdModal by viewModel.isShowingAdModal.collectAsState()
    val adSecondsLeft by viewModel.adSecondsLeft.collectAsState()
    val isAdFinished by viewModel.isAdFinished.collectAsState()
    val isShowingAuthDialog by viewModel.isShowingAuthDialog.collectAsState()
    val authMode by viewModel.authMode.collectAsState()
    val isShowingEditUidDialog by viewModel.isShowingEditUidDialog.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val exchangeRate by viewModel.exchangeRate.collectAsState()
    val paymentDetails by viewModel.paymentDetails.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val checkoutRefNumber by viewModel.checkoutRefNumber.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Hardware back button navigation handling
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        if (currentScreen == AppScreen.ADMIN) {
            viewModel.navigateTo(AppScreen.ACCOUNT)
        } else {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentScreen != AppScreen.ADMIN) {
                NavigationBar(
                    containerColor = DarkSurfaceElevated,
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        Triple(AppScreen.HOME, "Inicio", Icons.Filled.Home to Icons.Outlined.Home),
                        Triple(AppScreen.RECHARGES, "Recargas", Icons.Filled.Diamond to Icons.Outlined.Diamond),
                        Triple(AppScreen.EARN_COINS, "Monedas", Icons.Filled.MonetizationOn to Icons.Outlined.MonetizationOn),
                        Triple(AppScreen.TOOLS, "Herramientas", Icons.Filled.SportsEsports to Icons.Outlined.SportsEsports),
                        Triple(AppScreen.ACCOUNT, "Mi Cuenta", Icons.Filled.Person to Icons.Outlined.Person)
                    )

                    navItems.forEach { (screen, label, iconPair) ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(screen) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) iconPair.first else iconPair.second,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FlameOrange,
                                selectedTextColor = FlameOrange,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = DarkSurfaceHighlight
                            ),
                            modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                    AppScreen.RECHARGES -> RechargesScreen(viewModel = viewModel)
                    AppScreen.EARN_COINS -> EarnCoinsScreen(viewModel = viewModel)
                    AppScreen.TOOLS -> ToolsScreen(viewModel = viewModel)
                    AppScreen.ACCOUNT -> AccountHistoryScreen(viewModel = viewModel)
                    AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Hosted Modals & Dialogs
    if (isShowingPaymentModal && selectedPackForCheckout != null) {
        PaymentCheckoutDialog(
            pack = selectedPackForCheckout!!,
            exchangeRate = exchangeRate,
            userCoinBalance = currentUser?.coinBalance ?: 0,
            initialUid = currentUser?.freeFireUid ?: "",
            paymentDetails = paymentDetails,
            selectedMethod = selectedPaymentMethod,
            onMethodSelect = { viewModel.selectedPaymentMethod.value = it },
            onUidChange = { viewModel.checkoutUid.value = it },
            refNumber = checkoutRefNumber,
            onRefNumberChange = { viewModel.checkoutRefNumber.value = it },
            onConfirmPay = { ctx -> viewModel.executeOrderAndOpenWhatsApp(ctx) },
            onDismiss = { viewModel.closePaymentModal() }
        )
    }

    if (isShowingAdModal) {
        AdSimulationDialog(
            secondsRemaining = adSecondsLeft,
            isCompleted = isAdFinished,
            onClaimReward = { viewModel.completeAdAndClaimReward() },
            onCancelAd = { viewModel.cancelAd() }
        )
    }

    if (isShowingAuthDialog) {
        AuthDialog(
            initialMode = authMode,
            onLogin = { u, p -> viewModel.login(u, p) },
            onRegister = { u, c, p, id -> viewModel.register(u, c, p, id) },
            onRecover = { id, p -> viewModel.recoverPassword(id, p) },
            onGoogleSignIn = { viewModel.signInWithGoogle() },
            onDismiss = { viewModel.isShowingAuthDialog.value = false }
        )
    }

    if (isShowingEditUidDialog) {
        EditUidDialog(
            currentUid = currentUser?.freeFireUid ?: "",
            onSave = { newUid -> viewModel.updateSavedUid(newUid) },
            onDismiss = { viewModel.isShowingEditUidDialog.value = false }
        )
    }
}
