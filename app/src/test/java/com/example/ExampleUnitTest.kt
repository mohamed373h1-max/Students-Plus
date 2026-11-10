package com.example

import com.example.model.ActivationCode
import com.example.model.UserSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun activationCode_durationCalculation() {
    val code = ActivationCode(
      code = "ELITE-1234-30D",
      durationDays = 30,
      createdAt = System.currentTimeMillis()
    )
    assertEquals(30, code.remainingDays)
    assertFalse(code.isExpired)
  }

  @Test
  fun userSession_remainingDays_ownerAlwaysActive() {
    val ownerSession = UserSession(
      isActivated = true,
      studentName = "المالك",
      isOwner = true
    )
    assertTrue(ownerSession.isSubscriptionActive)
    assertEquals(999, ownerSession.remainingDays)
  }

  @Test
  fun userSession_student_activeSubscription() {
    val studentSession = UserSession(
      isActivated = true,
      studentName = "طالب النخبة",
      activationCode = "ELITE-30D",
      activatedTimestamp = System.currentTimeMillis(),
      durationDays = 30,
      isOwner = false
    )
    assertTrue(studentSession.isSubscriptionActive)
    assertTrue(studentSession.remainingDays > 0)
  }
}
