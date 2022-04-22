package com.example.upa_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.upa_app.R
import com.example.upa_app.databinding.ActivityMainBinding
import com.example.upa_app.presentation.MainActivityViewModel
import com.example.upa_app.presentation.util.HeightTopWindowInsetsListener
import com.example.upa_app.shared.di.CodelabsEnabledFlag
import com.example.upa_app.shared.di.ExploreArEnabledFlag
import com.example.upa_app.shared.di.MapFeatureEnabledFlag
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        /** Key for an int extra defining the initial navigation target. */
        const val EXTRA_NAVIGATION_ID = "extra.NAVIGATION_ID"

        private const val NAV_ID_NONE = -1

        private const val DIALOG_SIGN_IN = "dialog_sign_in"
        private const val DIALOG_SIGN_OUT = "dialog_sign_out"

        private val TOP_LEVEL_DESTINATIONS = setOf(
            com.example.upa_app.presentation.R.id.navigation_feed,
            com.example.upa_app.presentation.R.id.navigation_photo,
            com.example.upa_app.presentation.R.id.navigation_map,
            com.example.upa_app.presentation.R.id.navigation_info,
            com.example.upa_app.presentation.R.id.navigation_agenda,
            com.example.upa_app.presentation.R.id.navigation_codelabs,
            com.example.upa_app.presentation.R.id.navigation_settings,
        )
    }


    private val test = com.google.android.material.R.styleable.BottomSheetBehavior_Layout
    @Inject
    @JvmField
    @MapFeatureEnabledFlag
    var mapFeatureEnabled: Boolean = false

    @Inject
    @JvmField
    @CodelabsEnabledFlag
    var codelabsFeatureEnabled: Boolean = false

    @Inject
    @JvmField
    @ExploreArEnabledFlag
    var exploreArFeatureEnabled: Boolean = false

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private var currentNavId = NAV_ID_NONE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.statusBarScrim.setOnApplyWindowInsetsListener(HeightTopWindowInsetsListener)

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment


        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentNavId = destination.id
            // TODO: hide nav if not a top-level destination?
        }


        // Either of two different navigation views might exist depending on the configuration.
        binding.bottomNavigation?.apply {
            configureNavMenu(menu)
            setupWithNavController(navController)
            setOnItemReselectedListener { } // prevent navigating to the same item
        }
        binding.navigationRail?.apply {
            configureNavMenu(menu)
            setupWithNavController(navController)
            setOnItemReselectedListener { } // prevent navigating to the same item
        }
    }


    private fun configureNavMenu(menu: Menu) {
        menu.findItem(com.example.upa_app.presentation.R.id.navigation_map)?.isVisible = mapFeatureEnabled
        menu.findItem(com.example.upa_app.presentation.R.id.navigation_codelabs)?.isVisible = codelabsFeatureEnabled
        menu.findItem(com.example.upa_app.presentation.R.id.navigation_explore_ar)?.apply {
            // Handle launching new activities, otherwise assume the destination is handled
            // by the nav graph. We want to launch a new Activity for only the AR menu item.
            isVisible = exploreArFeatureEnabled
            setOnMenuItemClickListener {
//                if (connectivityManager.activeNetworkInfo?.isConnected == true) {
//                    if (viewModel.arCoreAvailability.value?.isSupported == true) {
//                        analyticsHelper.logUiEvent(
//                            "Navigate to Explore I/O ARCore supported",
//                            AnalyticsActions.CLICK
//                        )
//                        openExploreAr()
//                    } else {
//                        analyticsHelper.logUiEvent(
//                            "Navigate to Explore I/O ARCore NOT supported",
//                            AnalyticsActions.CLICK
//                        )
//                        openArCoreNotSupported()
//                    }
//                } else {
//                    openNoConnection()
//                }
                true
            }
        }
    }
}