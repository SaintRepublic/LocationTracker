package com.saintrepublic.locationtracker.ui

import androidx.lifecycle.observe
import com.saintrepublic.locationtracker.R
import com.saintrepublic.locationtracker.adapters.HistoryAdapter
import com.saintrepublic.locationtracker.databinding.ActivityHistoryBinding
import com.saintrepublic.locationtracker.vm.HistoryActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {

    private val viewModel by viewModel<HistoryActivityViewModel>()

    private val adapter by lazy { HistoryAdapter(this) }

    override fun createBindings(): ActivityHistoryBinding {
        return ActivityHistoryBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@HistoryActivity

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener { onBackPressed() }

            recyclerView.adapter = adapter

            viewModel.databaseService.isDatabaseChangedEvent.observe(this@HistoryActivity) {
                it.let {
                    viewModel.load()
                }
            }
            viewModel.items.observe(this@HistoryActivity) {
                adapter.items = it.toTypedArray()
            }
            viewModel.load()

        }
    }


}