package com.example.upa_app.presentation.photo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.upa_app.domain.sessions.ConferenceDayIndexer
import com.example.upa_app.model.ConferenceDay
import com.example.upa_app.presentation.MainActivityViewModel
import com.example.upa_app.presentation.R
import com.example.upa_app.presentation.databinding.FragmentPhotoBinding
import com.example.upa_app.presentation.sessioncommon.SessionsAdapter
import com.example.upa_app.presentation.signin.setupProfileMenuItem
import com.example.upa_app.presentation.util.*
import com.example.upa_app.presentation.widget.BubbleDecoration
import com.example.upa_app.presentation.widget.FadingSnackbar
import com.example.upa_app.presentation.widget.JumpSmoothScroller
import com.example.upa_app.shared.analytics.AnalyticsActions
import com.example.upa_app.shared.analytics.AnalyticsHelper
import com.example.upa_app.shared.di.SearchScheduleEnabledFlag
import com.example.upa_app.shared.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class PhotoFragment : Fragment() {

    companion object {
        private const val DIALOG_NEED_TO_SIGN_IN = "dialog_need_to_sign_in"
        private const val DIALOG_CONFIRM_SIGN_OUT = "dialog_need_to_sign_in"
        private const val DIALOG_SCHEDULE_HINTS = "dialog_need_to_sign_in"
    }

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    @field:Named("tagViewPool")
    lateinit var tagViewPool: RecyclerView.RecycledViewPool

    @Inject
    @JvmField
    @SearchScheduleEnabledFlag
    var searchScheduleFeatureEnabled: Boolean = false


    private val photoViewModel: PhotoViewModel by viewModels()
    private val photoTwoPaneViewModel: PhotoTwoPaneViewModel by activityViewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var snackbar: FadingSnackbar

    private lateinit var scheduleRecyclerView: RecyclerView
    private lateinit var sessionsAdapter: SessionsAdapter
    private lateinit var scheduleScroller: JumpSmoothScroller

    private lateinit var dayIndicatorRecyclerView: RecyclerView
    private lateinit var dayIndicatorAdapter: DayIndicatorAdapter
    private lateinit var dayIndicatorItemDecoration: BubbleDecoration

    private lateinit var dayIndexer: ConferenceDayIndexer
    private var cachedBubbleRange: IntRange? = null

    private lateinit var binding: FragmentPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = photoViewModel
        }

        snackbar = binding.snackbar
        scheduleRecyclerView = binding.recyclerviewSchedule
        dayIndicatorRecyclerView = binding.dayIndicators
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up search menu item
        binding.toolbar.run {
            inflateMenu(R.menu.schedule_menu)
            menu.findItem(R.id.search).isVisible = searchScheduleFeatureEnabled
            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.search) {
                    analyticsHelper.logUiEvent("Navigation to Search", AnalyticsActions.CLICK)
                    openSearch()
                    true
                } else {
                    false
                }
            }
        }

        binding.toolbar.setupProfileMenuItem(mainActivityViewModel, this)

        // Pad the bottom of the RecyclerView so that the content scrolls up above the nav bar
        binding.recyclerviewSchedule.doOnApplyWindowInsets { v, insets, padding ->
            val systemInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.updatePadding(bottom = padding.bottom + systemInsets.bottom)
        }

        // Session list configuration
        sessionsAdapter = SessionsAdapter(
            tagViewPool,
            photoViewModel.showReservations,
            photoViewModel.timeZoneId,
            viewLifecycleOwner,
            photoTwoPaneViewModel, // OnSessionClickListener
            photoTwoPaneViewModel // OnSessionStarClickListener
        )
        scheduleRecyclerView.apply {
            adapter = sessionsAdapter
            (itemAnimator as DefaultItemAnimator).run {
                supportsChangeAnimations = false
                addDuration = 160L
                moveDuration = 160L
                changeDuration = 160L
                removeDuration = 120L
            }

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    onScheduleScrolled()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    photoViewModel.userHasInteracted = true
                }
            })
        }
        binding.swipeRefreshLayout.doOnNextLayout {
            setContentMaxWidth(it)
        }

        scheduleScroller = JumpSmoothScroller(view.context)

        dayIndicatorItemDecoration = BubbleDecoration(view.context)
        dayIndicatorRecyclerView.addItemDecoration(dayIndicatorItemDecoration)

        dayIndicatorAdapter = DayIndicatorAdapter(photoViewModel, viewLifecycleOwner)
        dayIndicatorRecyclerView.adapter = dayIndicatorAdapter

        // Start observing ViewModels
        launchAndRepeatWithViewLifecycle {
            launch {
                photoViewModel.scheduleUiData.collect { updateScheduleUi(it) }
            }

            // During conference, scroll to current event.
            launch {
                photoViewModel.scrollToEvent.collect { scrollEvent ->
                    if (scrollEvent.targetPosition != -1) {
                        scheduleRecyclerView.run {
                            post {
                                val lm = layoutManager as LinearLayoutManager
                                if (scrollEvent.smoothScroll) {
                                    scheduleScroller.targetPosition = scrollEvent.targetPosition
                                    lm.startSmoothScroll(scheduleScroller)
                                } else {
                                    lm.scrollToPositionWithOffset(scrollEvent.targetPosition, 0)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateScheduleUi(scheduleUiData: ScheduleUiData) {
        // Require everything to be loaded/
        val list = scheduleUiData.list ?: return
        val timeZoneId = scheduleUiData.timeZoneId ?: return
        val indexer = scheduleUiData.dayIndexer ?: return

        dayIndexer = indexer
        // Prevent building new indicators until we get scroll information.
        cachedBubbleRange = null
        if (indexer.days.isEmpty()) {
            // Special case: the results are empty, so we won't get valid scroll information.
            // Set a bogus range to and rebuild the day indicators.
            cachedBubbleRange = -1..-1
            rebuildDayIndicators()
        }

        sessionsAdapter.submitList(list)

        scheduleRecyclerView.run {
            // Recreate the decoration used for the sticky time headers
            clearDecorations()
            if (list.isNotEmpty()) {
                addItemDecoration(
                    ScheduleTimeHeadersDecoration(
                        context, list.map { it.session }, timeZoneId
                    )
                )
                addItemDecoration(
                    DaySeparatorItemDecoration(
                        context, indexer, timeZoneId
                    )
                )
            }
        }

        binding.executeAfter {
            isEmpty = list.isEmpty()
        }
    }

    private fun rebuildDayIndicators() {
        // cachedBubbleRange will get set once we have scroll information, so wait until then.
        val bubbleRange = cachedBubbleRange ?: return
        val indicators = if (dayIndexer.days.isEmpty()) {
            TimeUtils.ConferenceDays.map { day: ConferenceDay ->
                DayIndicator(day = day, enabled = false)
            }
        } else {
            dayIndexer.days.mapIndexed { index: Int, day: ConferenceDay ->
                DayIndicator(day = day, checked = index in bubbleRange)
            }
        }

        dayIndicatorAdapter.submitList(indicators)
        dayIndicatorItemDecoration.bubbleRange = bubbleRange
    }

    private fun onScheduleScrolled() {
        val layoutManager = (scheduleRecyclerView.layoutManager) as LinearLayoutManager
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()
        if (first < 0 || last < 0) {
            // When the list is empty, we get -1 for the positions.
            return
        }

        val firstDay = dayIndexer.dayForPosition(first) ?: return
        val lastDay = dayIndexer.dayForPosition(last) ?: return
        val highlightRange = dayIndexer.days.indexOf(firstDay)..dayIndexer.days.indexOf(lastDay)
        if (highlightRange != cachedBubbleRange) {
            cachedBubbleRange = highlightRange
            rebuildDayIndicators()
        }
    }


    private fun openSearch() {
        findNavController().navigate(PhotoFragmentDirections.toSearch())
    }

}