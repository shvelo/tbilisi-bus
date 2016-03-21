package com.tbilisi.bus.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.tbilisi.bus.R

class InfoFragment : Fragment() {
    var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_info, container, false)
        webView = createdView.findViewById(R.id.webView) as WebView
        showInfo()
        return createdView
    }

    fun showInfo() {
        val url = getString(R.string.info_uri)
        webView?.loadUrl(url)
    }
}
