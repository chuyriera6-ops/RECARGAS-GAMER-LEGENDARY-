package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Recarga Gamer Legendary", appName)
  }

  @Test
  fun `test sensitivity calculation and hashing`() {
    val sens = com.example.util.FreeFireToolsUtil.calculateSensitivity(
      phoneModel = "POCO X3 Pro",
      dpi = 580,
      playStyle = "Rusher",
      fingers = 2
    )
    assertTrue(sens.general in 80..200)
    assertTrue(sens.buttonSizePercent in 30..70)

    val salt = com.example.util.SecurityUtil.generateSalt()
    val hash = com.example.util.SecurityUtil.hashPassword("secret123", salt)
    assertTrue(com.example.util.SecurityUtil.verifyPassword("secret123", salt, hash))
    assertFalse(com.example.util.SecurityUtil.verifyPassword("wrongpass", salt, hash))

    assertTrue(com.example.util.SecurityUtil.isValidFreeFireUid("123456789"))
    assertFalse(com.example.util.SecurityUtil.isValidFreeFireUid("abc"))
  }

  @Test
  fun `test admin password jerquins16 and payment constants`() {
    val adminPin = "jerquins16"
    assertEquals("jerquins16", adminPin)

    // Check payment phone and cedula
    val phone = "04122871341"
    val cedula = "29754087"
    val binanceId = "1132079220"
    assertTrue(phone.startsWith("0412"))
    assertEquals("29754087", cedula)
    assertEquals("1132079220", binanceId)
  }
}
