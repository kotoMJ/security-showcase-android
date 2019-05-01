package cz.kotox.securityshowcase.login

import cz.kotox.securityshowcase.core.arch.BaseViewModel
import cz.kotox.securityshowcase.login.entity.AuthCredentials
import javax.inject.Inject

class LoginViewModel @Inject constructor(
	var testCredentials: AuthCredentials
) : BaseViewModel()