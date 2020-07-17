package com.saintrepublic.locationtracker.util

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("app:goneIf")
fun bindGoneIf(view: View, hide: Boolean) {
    view.visibility = when (hide) {
        true -> View.GONE
        false -> View.VISIBLE
    }
}

@BindingAdapter("app:goneIfNot")
fun bindGoneIfNot(view: View, hide: Boolean) {
    view.visibility = when (hide) {
        true -> View.VISIBLE
        false -> View.GONE
    }
}