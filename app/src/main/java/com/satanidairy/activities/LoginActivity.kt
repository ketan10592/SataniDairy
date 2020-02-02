package com.satanidairy.activities

import android.view.View
import com.satanidairy.R
import com.satanidairy.base.BaseActivity
import com.satanidairy.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
       when(v?.id){
           R.id.btn_temp -> {
               loginBinding?.text = null
           }

           R.id.txt_temp -> {

           }
       }
    }

    var loginBinding: ActivityLoginBinding? = null

    override val layoutResId: Int
        get() = R.layout.activity_login

    override fun init() {
        loginBinding = bindObject as ActivityLoginBinding
        loginBinding?.handler = this
        loginBinding?.text = "ddsgdfg"
    }
}
