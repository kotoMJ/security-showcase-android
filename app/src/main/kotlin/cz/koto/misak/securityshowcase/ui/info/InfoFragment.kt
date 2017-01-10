package cz.koto.misak.securityshowcase.ui.info

import android.content.Intent
import cz.kinst.jakub.viewmodelbinding.ViewModelBindingConfig
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.databinding.FragmentInfoBinding
import cz.koto.misak.securityshowcase.ui.BaseFragment


class InfoFragment : BaseFragment<FragmentInfoBinding, InfoViewModel>() {


    override fun getViewModelBindingConfig() =
            ViewModelBindingConfig(R.layout.fragment_info, InfoViewModel::class.java)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun newInstance() = InfoFragment()
    }
}