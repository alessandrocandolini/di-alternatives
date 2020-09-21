import org.gradle.api.artifacts.dsl.DependencyHandler

object KoTest {

    private fun kotest(s : String) : NameId = "kotest-$s".toNameId()

    private val kotestGroup = "io.kotest".toGroupId()
    private val kotestNames : List<NameId> = listOf(
        "runner-junit5",
        "assertions-core",
        "property"
    ).map(::kotest)

    private val kotestVersion = "4.2.5".toVersion()

    private val dependencies = kotestNames.map { d -> kotestGroup + d + kotestVersion + DependencyConfig.TEST_IMPLEMENTATION}

    fun DependencyHandler.addKotest() {
        implementDependencies(dependencies)
    }

}