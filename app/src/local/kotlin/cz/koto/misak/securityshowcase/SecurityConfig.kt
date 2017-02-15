package cz.koto.misak.securityshowcase


/**
 * Keep server definitions available on one place.
 */
object SecurityConfig : SecurityBaseConfig() {

    //Genymotion emulator
    val API_KOTINODE_IP = "10.0.3.2"

    //Google emulator
    //val API_KOTINODE_IP = "10.0.2.2"

    val API_KOTINODE_PORT = "8080";

    val API_KOTINODE_PROTOCOL = "http"//HTTP is used for local development purpose only.

    fun getApiEndpoint(): String = "$API_KOTINODE_PROTOCOL://$API_KOTINODE_IP:$API_KOTINODE_PORT"


    /**
     * LocalFlavour related url replacement.
     * @param urlString
     * *
     * @return
     */
    fun replaceUrl(urlString: String?): String? {
        if (urlString == null) return urlString
        return urlString.replace("localhost", API_KOTINODE_IP)
    }
}
