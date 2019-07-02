package cz.kotox.securityshowcase.login.ui.keystorecompat

import cz.kotox.securityshowcase.core.arch.BaseViewModel
import cz.kotox.securityshowcase.login.entity.AuthCredentials
import javax.inject.Inject

class LoginCompatViewModel @Inject constructor(
	var testCredentials: AuthCredentials
) : BaseViewModel()
