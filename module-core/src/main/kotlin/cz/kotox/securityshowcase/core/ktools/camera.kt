package cz.kotox.securityshowcase.core.ktools

enum class CameraType(val extension: String, val mimeType: String) {
	IMAGE("jpg", "image/jpeg"),
	VIDEO("mp4", "video/mp4")
}