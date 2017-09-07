package cz.koto.securityshowcase.model.base

import com.google.gson.annotations.SerializedName

class ServerResponseList<T> {

	@SerializedName("dataValue")
	internal var data: List<T>? = null

}