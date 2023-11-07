package sample.crud.config

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.Duration.Companion.milliseconds

object ProjectConfig : AbstractProjectConfig() {
    // Global timeout 설정
    override val timeout = 3000.milliseconds
}