package de.othr.archeologicalfieldwork.views.login

import android.os.Bundle
import androidx.annotation.StringRes
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.BaseView
import kotlinx.android.synthetic.main.activity_login.*

class LoginView : BaseView() {

    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = initPresenter (LoginPresenter(this)) as LoginPresenter

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        username.afterTextChanged {
            checkInput()
        }

        password.afterTextChanged {
            checkInput()
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            presenter.doLoginOrSignup(username.text.toString(), password.text.toString())
        }
    }

    private fun checkInput() {
        presenter.checkInput(username.text.toString(), password.text.toString())
    }

    fun setLoginButtonState(state: Boolean) {
        login.isEnabled = state
    }

    fun setUsernameError() {
        username.error = getString(R.string.invalid_email)
    }

    fun setPasswordError() {
        password.error = getString(R.string.invalid_password)
    }

    fun setSignupError() {
        username.error = getString(R.string.invalid_email__in_use)
        Toast.makeText(applicationContext, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show()
    }

    fun setLoginError() {
        password.error = getString(R.string.invalid_password__wrong_password)
        Toast.makeText(applicationContext, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
