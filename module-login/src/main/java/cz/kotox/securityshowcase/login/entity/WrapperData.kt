package cz.kotox.securityshowcase.login.entity

import com.squareup.moshi.Json

class ServerResponseObject<T> {
	@Json(name = "dataValue")
	internal var data: T? = null
}

class ServerResponseList<T> {
	@Json(name = "dataValue")
	internal var data: List<T>? = null

}
