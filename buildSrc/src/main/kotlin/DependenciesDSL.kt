import DependencyConfig.*
import org.gradle.api.artifacts.dsl.DependencyHandler

data class GroupId(val value: String)
data class NameId(val value: String)
data class Version(val value: String)

fun String.toGroupId() = GroupId(this)
fun String.toNameId() = NameId(this)
fun String.toVersion() = Version(this)

operator fun GroupId.plus(nameId: NameId): GroupAndName = GroupAndName(this, nameId)

data class GroupAndName(
    val group: GroupId,
    val name: NameId
)

data class TypeSafeDependency(
    val group: GroupId,
    val name: NameId,
    val version: Version
)

operator fun GroupAndName.plus(version: Version): TypeSafeDependency =
    TypeSafeDependency(this.group, this.name, version)

enum class DependencyConfig {
    IMPLEMENTATION,
    API,
    TEST_IMPLEMENTATION
}

data class DependencyWithConfig(
    val dependency: TypeSafeDependency,
    val config: DependencyConfig
)

operator fun TypeSafeDependency.plus(config: DependencyConfig): DependencyWithConfig =
    DependencyWithConfig(this, config)

private fun TypeSafeDependency.toRawDependency(): String = "${group.value}:${name.value}:${version.value}"

private fun DependencyConfig.toConfigurationName(): String = when (this) {
    IMPLEMENTATION -> "implementation"
    API -> "api"
    TEST_IMPLEMENTATION -> "testImplementation"
}

private fun DependencyWithConfig.toConfigurationName(): String = this.config.toConfigurationName()
private fun DependencyWithConfig.toRawDependency(): String = this.dependency.toRawDependency()


fun DependencyHandler.implementDependencies(d: List<DependencyWithConfig>) {
    d.forEach{ p -> implementDependency(p) }
}

fun DependencyHandler.implementDependency(d: DependencyWithConfig) {
    add(d.toConfigurationName(), d.toRawDependency())
}

