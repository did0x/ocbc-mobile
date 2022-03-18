package com.putrash.common.component

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout

class TextInputLayoutCore : TextInputLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        clearEditTextColorFilter()
    }

    override fun setError(error: CharSequence?) {
        super.setError(error)
        clearEditTextColorFilter()
    }

    private fun clearEditTextColorFilter() {
        val editText = editText
        val background = editText?.background
        background?.clearColorFilter()
    }
}