package de.othr.archeologicalfieldwork.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.othr.archeologicalfieldwork.model.Site
import org.jetbrains.anko.AnkoLogger


const val NO_MENU: Int = -1

open class BaseView(private val viewRef: Int) : Fragment(), AnkoLogger {

    var basePresenter: BasePresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(viewRef, container, false)
    }

    fun initPresenter(presenter: BasePresenter): BasePresenter {
        basePresenter = presenter

        return presenter
    }

    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            basePresenter?.doActivityResult(requestCode, resultCode, data)
        }
    }

    open fun showSite(site: Site, visited: Boolean) {}
    open fun showSites(sites: List<Site>) {}
}