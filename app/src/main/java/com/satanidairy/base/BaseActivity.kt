package com.satanidairy.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {
    var disposable: Disposable? = null
    var bindObject: ViewDataBinding? = null
        private set

    abstract val layoutResId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindObject = DataBindingUtil.setContentView(this, layoutResId)
        init()
    }

    abstract fun init()
}
