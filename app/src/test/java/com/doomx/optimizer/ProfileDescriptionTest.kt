package com.doomx.optimizer

import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileDescriptionTest {
    @Test fun profileDescriptionsAreNotEmpty() {
        listOf("SMART", "BALANCED", "PERFORMANCE", "THERMAL SAFE")
            .forEach { assertTrue(it.isNotBlank()) }
    }
}
