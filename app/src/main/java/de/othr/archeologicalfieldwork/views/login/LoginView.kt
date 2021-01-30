package de.othr.archeologicalfieldwork.views.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.BaseView
import kotlinx.android.synthetic.main.activity_login.*

class LoginView : BaseView(R.layout.activity_login) {

    lateinit var presenter: LoginPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = initPresenter(LoginPresenter(this)) as LoginPresenter

        setHasOptionsMenu(true)

        val username = view.findViewById<EditText>(R.id.username)
        val password = view.findViewById<EditText>(R.id.password)
        val login = view.findViewById<Button>(R.id.login)
        val loading = view.findViewById<ProgressBar>(R.id.loading)

        username.afterTextChanged {
            checkInput()
        }

        password.afterTextChanged {
            checkInput()
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            presenter.doLoginOrSignup(username.text.toString(), password.text.toString())
            loading.visibility = View.INVISIBLE
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
        Toast.makeText(requireActivity().applicationContext, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show()
    }

    fun setLoginError() {
        password.error = getString(R.string.invalid_password__wrong_password)
        Toast.makeText(requireActivity().applicationContext, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
    }

    fun showProgressBar() {
        loading.isIndeterminate = true
        loading.isVisible = true
    }

    fun hideProgressBar() {
        loading.isIndeterminate = false
        loading.isVisible = false
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
