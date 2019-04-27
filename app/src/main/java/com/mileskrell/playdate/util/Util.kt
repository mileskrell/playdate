package com.mileskrell.playdate.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// From https://stackoverflow.com/a/1109108
fun hideSoftKeyboard(view: View?) {
    if (view != null) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
