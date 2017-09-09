package cz.koto.securityshowcase.ui.info

import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.databinding.FragmentInfoBinding
import cz.koto.securityshowcase.ui.BaseViewModel

class InfoViewModel : BaseViewModel<FragmentInfoBinding>() {

//	val state = ObservableField(StatefulLayout.State.CONTENT)


	override fun onViewModelCreated() {
		super.onViewModelCreated()
	}


	override fun onResume() {
		super.onResume()
		activity.showToolbar(true)
		activity.getSupportActionBar()?.setIcon(R.drawable.ic_security_24dp)
		activity.getSupportActionBar()?.setTitle("")
	}


	override fun onViewAttached(firstAttachment: Boolean) {
		super.onViewAttached(firstAttachment)
		activity.getBinding().executePendingBindings()
		activity.getSupportActionBar()?.setIcon(R.drawable.ic_security_24dp)
		activity.getSupportActionBar()?.setTitle("")
	}


	override fun onViewDetached(finalDetachment: Boolean) {
		super.onViewDetached(finalDetachment)
	}

	override fun onViewModelDestroyed() {
		super.onViewModelDestroyed()
	}

}