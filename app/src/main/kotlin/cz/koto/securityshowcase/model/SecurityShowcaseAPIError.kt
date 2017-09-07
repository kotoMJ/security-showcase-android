package cz.koto.securityshowcase.model

import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

@Parcel data class SecurityShowcaseAPIError(
		var statusCode: Int? = null,
		var title: String? = null,
		@SerializedName("message") var message: String? = null)

