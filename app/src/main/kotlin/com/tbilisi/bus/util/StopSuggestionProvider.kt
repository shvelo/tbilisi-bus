package com.tbilisi.bus.util

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.tbilisi.bus.data.BusStop
import io.realm.Realm

class StopSuggestionProvider: ContentProvider() {
    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        val query = uri?.lastPathSegment?.toLowerCase()
        if(query != null) {
            val cursor = MatrixCursor(arrayOf( "_ID", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA))
            val results = Realm.getDefaultInstance().where(BusStop::class.java).contains("name", query).or().contains("name_en", query).findAll()
            results.forEach {
                cursor.addRow(arrayOf(it.id, LocalizationHelper.getLocalizedStopName(it), it.id, it.id))
            }
            return cursor
        } else {
            return null
        }
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        return null
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri?): String? {
        return null
    }
}