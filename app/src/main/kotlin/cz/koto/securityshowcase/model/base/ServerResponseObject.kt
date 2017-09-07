package cz.koto.securityshowcase.model.base

import com.google.gson.annotations.SerializedName

class ServerResponseObject<T> {

	@SerializedName("dataValue")
	internal var data: T? = null

}