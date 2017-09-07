package cz.koto.securityshowcase


/**
 * Keep server definitions available on one place.
 */
object SecurityConfig : SecurityBaseConfig() {


	val API_KOTINODE_PROTOCOL = "https"

	fun getRestEndpoint(): String = "$API_KOTINODE_PROTOCOL://kotopeky.cz/api/kotinode/"
	fun getGqlEndpoint(): String = "$API_KOTINODE_PROTOCOL://kotopeky.cz/graphql"

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
