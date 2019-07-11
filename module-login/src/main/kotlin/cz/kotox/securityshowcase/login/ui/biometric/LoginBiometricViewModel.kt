package cz.kotox.securityshowcase.login.ui.biometric

import cz.kotox.securityshowcase.core.arch.BaseViewModel
import cz.kotox.securityshowcase.login.entity.AuthCredentials
import javax.inject.Inject

class LoginBiometricViewModel @Inject constructor(
	var testCredentials: AuthCredentials
) : BaseViewModel()
