package cz.koto.misak.securityshowcase


/**
 * Keep server definitions available on one place.
 */
object SecurityConfig : SecurityBaseConfig() {


    val API_KOTINODE_PROTOCOL = "https"

    fun getApiEndpoint(): String = "$API_KOTINODE_PROTOCOL://kotopeky.cz/api/kotinode/"


    /**
     * RostiFlavour related alternative for localFlavour url replacement.
     * @param urlString
     * *
     * @return
     */
    fun replaceUrl(urlString: String): String {
        return urlString
    }
}
