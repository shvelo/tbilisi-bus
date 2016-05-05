package com.tbilisi.bus.fragments

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.tbilisi.bus.R
import com.tbilisi.bus.data.FavoriteStore
import com.tbilisi.bus.data.HistoryStore

class FavoritesFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val createdView = inflater.inflate(R.layout.fragment_favorites, container, false)
        return createdView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.favorites, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_clear -> {
                clearFavorites()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun clearFavorites() {
        FavoriteStore.clearFavorites()
        Toast.makeText(context, R.string.favorites_cleared, Toast.LENGTH_SHORT).show()
    }
}
