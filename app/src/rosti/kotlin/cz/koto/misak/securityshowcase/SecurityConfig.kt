package cz.koto.misak.securityshowcase


/**
 * Keep server definitions available on one place.
 */
object SecurityConfig : SecurityBaseConfig() {


    //TODO use http temporarily - for https is necessary to provide trust to certificate
    val API_KOTINODE_PROTOCOL = "http"

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
