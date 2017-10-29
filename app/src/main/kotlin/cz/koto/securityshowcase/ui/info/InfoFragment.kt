package cz.koto.securityshowcase.ui.info

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.koto.securityshowcase.databinding.FragmentInfoBinding
import cz.koto.securityshowcase.ui.BaseArchFragment


class InfoFragment : BaseArchFragment() {

	private lateinit var viewDataBinding: FragmentInfoBinding
	private lateinit var viewModel: InfoViewModel

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {

		viewModel = ViewModelProviders.of(this).get(InfoViewModel::class.java)

		viewDataBinding = FragmentInfoBinding.inflate(inflater, container, false)
		viewDataBinding.viewModel = viewModel
		//viewDataBinding.executePendingBindings()

		setHasOptionsMenu(true)
		return viewDataBinding.root
	}


//	override fun onCreate(savedInstanceState: Bundle?) {
//		setupViewModel(R.layout.fragment_info, InfoViewModel::class.java)
//		super.onCreate(savedInstanceState)
//	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
	}

	companion object {
		fun newInstance() = InfoFragment()
	}
}