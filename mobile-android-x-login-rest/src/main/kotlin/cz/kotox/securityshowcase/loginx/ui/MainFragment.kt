package cz.kotox.routines.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import cz.kotox.routines.R
import cz.kotox.securityshowcase.core.arch.BaseFragment

/**
 * Fragment used to show how to navigate to another destination
 */
class MainFragment : BaseFragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?): View? {
		setHasOptionsMenu(true)
		return inflater.inflate(R.layout.main_fragment, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

//		view.findViewById<Button>(R.id.navigate_dest_bt)?.setOnClickListener(
//			Navigation.createNavigateOnClickListener(R.id.flow_step_one, null)
//		)

//		val options = NavOptions.Builder()
//			.setEnterAnim(R.anim.slide_in_right)
//			.setExitAnim(R.anim.slide_out_left)
//			.setPopEnterAnim(R.anim.slide_in_left)
//			.setPopExitAnim(R.anim.slide_out_right)
//			.build()

//		view.findViewById<Button>(R.id.navigate_dest_bt)?.setOnClickListener {
//			findNavController(it).navigate(R.id.text_recognition_navigation, null, options)
//		}
//		view.findViewById<Button>(R.id.navigate_action_bt)?.setOnClickListener(
//			Navigation.createNavigateOnClickListener(R.id.next_action, null)
//		)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.main_menu, menu)
	}
}
