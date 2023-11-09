package sample.crud.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import kotlin.time.Duration.Companion.milliseconds

object ProjectConfig : AbstractProjectConfig() {
    // Global timeout 설정
    override val timeout = 3000.milliseconds
    // 테스트의 Leaf 마다 새로운 인스턴스
    override val isolationMode = IsolationMode.InstancePerLeaf
}