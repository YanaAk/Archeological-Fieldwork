package de.othr.archeologicalfieldwork.views

interface ProgressableForResult<S, E> {
    fun start()
    fun done(r: S)
    fun failure(r: E)
}