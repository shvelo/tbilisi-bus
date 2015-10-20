package com.tbilisi.bus

import android.os.Bundle
import android.app.Activity
import kotlinx.android.synthetic.activity_schedule.*

class ScheduleActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

}
