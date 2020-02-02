package com.satanidairy.base

import android.app.Activity
import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {
    protected var disposable: Disposable? = null
    protected var activity: AppCompatActivity? = null
    var bindingObj: ViewDataBinding? = null
        private set

    abstract val layoutResId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingObj = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        return bindingObj!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    abstract fun init()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context as AppCompatActivity?
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as AppCompatActivity?
    }

    protected fun disPoseApiCall() {
        if (disposable != null && !disposable!!.isDisposed())
            disposable!!.dispose()
    }

    override fun onDestroy() {
        disPoseApiCall()
        super.onDestroy()
    }
}
