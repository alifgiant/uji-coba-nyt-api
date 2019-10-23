package com.linkaja.exam.view.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.linkaja.exam.R
import com.linkaja.exam.view.fragment.FavoriteFragment
import com.linkaja.exam.view.fragment.HomeFragment
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.design.bottomNavigationView
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.px2dip
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.verticalLayout

class MainActivity : AppCompatActivity() {

    private val fragments = listOf(
        HomeFragment.newInstance(),
        FavoriteFragment.newInstance()
    )

    private val ui: MainUI by lazy {
        MainUI { position ->
            replaceFragment(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)
        replaceFragment(0)
    }

    private fun replaceFragment(position: Int) {
        supportFragmentManager.beginTransaction().replace(frameContainer, fragments[position])
            .commit()
    }

    class MainUI(
        private val switchTab: (position: Int) -> Unit
    ) : AnkoComponent<MainActivity> {
        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            verticalLayout {
                frameLayout {
                    id = frameContainer
                    backgroundColor = R.color.colorAccent
                }.lparams(
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    weight = 1f
                )
                bottomNavigationView {
                    inflateMenu(R.menu.menu_home)
                    elevation = px2dip(4)
                    setOnNavigationItemSelectedListener { item ->

                        switchTab(
                            when (item.itemId) {
                                R.id.menu_home -> 0
                                else -> 1
                            }
                        )
                        true
                    }
                }.lparams(
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    companion object {
        @IdRes
        val frameContainer = View.generateViewId()
    }
}
