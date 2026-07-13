package com.keyloop.documentviewer.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Test

class VinTest {
    @Test
    fun `normalizes a valid VIN`() {
        val vin = Vin.parse(" wvwzzz1jzxw000001 ")

        assertThat(vin.value).isEqualTo("WVWZZZ1JZXW000001")
    }

    @Test
    fun `rejects a VIN with an excluded letter`() {
        assertThatIllegalArgumentException()
            .isThrownBy { Vin.parse("WVWZZZ1JZXW00000I") }
            .withMessageContaining("cannot contain I, O, or Q")
    }

    @Test
    fun `rejects a VIN with the wrong length`() {
        assertThatIllegalArgumentException()
            .isThrownBy { Vin.parse("SHORT") }
            .withMessageContaining("exactly 17 characters")
    }
}
