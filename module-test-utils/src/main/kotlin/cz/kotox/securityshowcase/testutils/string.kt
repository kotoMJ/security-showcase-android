package cz.kotox.securityshowcase.testutils

fun getRandomPassword(): String {
	val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	var passWord = ""
	for (i in 0..31) {
		passWord += chars[Math.floor(Math.random() * chars.length).toInt()]
	}
	return passWord
}