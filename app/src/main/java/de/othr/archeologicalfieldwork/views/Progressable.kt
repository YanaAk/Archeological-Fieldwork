package de.othr.archeologicalfieldwork.views

interface Progressable {
    fun start()
    fun done()
    fun failure()
}