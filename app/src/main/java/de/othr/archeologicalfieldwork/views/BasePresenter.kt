package de.othr.archeologicalfieldwork.views

import android.content.Intent
import de.othr.archeologicalfieldwork.main.MainApp

open class BasePresenter(var view: BaseView?) {

    var app: MainApp =  view?.activity?.application as MainApp

    open fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {}

    open fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {}

    fun logout() {
        app.userStore.logout()
    }

    open fun onDestroy() {
        view = null
    }
}