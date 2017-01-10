package cz.koto.misak.securityshowcase.ui.info

import android.databinding.ObservableField
import cz.kinst.jakub.view.StatefulLayout
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.databinding.FragmentInfoBinding
import cz.koto.misak.securityshowcase.ui.BaseViewModel

class InfoViewModel : BaseViewModel<FragmentInfoBinding>() {

    val state = ObservableField(StatefulLayout.State.CONTENT)


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
        binding.executePendingBindings()
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