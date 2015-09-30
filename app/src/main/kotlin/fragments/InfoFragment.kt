package com.tbilisi.bus.fragments

import android.os.Bundle
import android.app.Fragment
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.webkit.WebView
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer
import com.mapbox.mapboxsdk.views.MapView
import com.tbilisi.bus.R

public class InfoFragment : Fragment() {
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
