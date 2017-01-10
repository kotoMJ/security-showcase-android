package cz.koto.misak.securityshowcase.model

import org.parceler.Parcel


@Parcel(Parcel.Serialization.BEAN) data class UserProfileResponse(
        var unreadMessageCount: Int? = null,
        var iconId: Int? = null,
        var firstName: String? = null,
        var lastName: String? = null)