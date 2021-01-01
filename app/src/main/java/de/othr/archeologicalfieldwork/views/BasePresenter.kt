package de.othr.archeologicalfieldwork.views

import android.content.Intent
import de.othr.archeologicalfieldwork.MainActivity

open class BasePresenter(var view: BaseView?) {

    var app: MainActivity =  view?.application as MainActivity

    open fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    }

    open fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    }

    open fun onDestroy() {
        view = null
    }
}