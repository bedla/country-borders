package cz.bedla.countries.utils

inline fun <T> measureTimeMillis(reportMillis: (Long, T) -> Unit, block: () -> T): T {
    val start = System.currentTimeMillis()
    return block().also {
        reportMillis(System.currentTimeMillis() - start, it)
    }
}
