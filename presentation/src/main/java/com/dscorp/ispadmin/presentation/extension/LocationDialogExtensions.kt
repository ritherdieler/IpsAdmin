package com.dscorp.ispadmin.presentation.extension

import androidx.fragment.app.Fragment
import com.dscorp.ispadmin.CrossDialogFragment
import com.dscorp.ispadmin.R


fun Fragment.showLocationRationaleDialog(
) {
    CrossDialogFragment(
        text = getString(R.string.dont_have_your_location_permission),
        lottieResource = R.raw.location,
        positiveButtonText = getString(R.string.go_to_settings),
        onPositiveButtonClick = {
            openLocationSetting()
        }
    ).apply { isCancelable = false }.show(
        childFragmentManager,
        CrossDialogFragment::class.simpleName
    )
}