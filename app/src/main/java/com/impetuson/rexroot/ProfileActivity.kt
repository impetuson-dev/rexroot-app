package com.impetuson.rexroot

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity: AppCompatActivity(R.layout.myprofile_screen) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.primary_red)


    }
}