/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dungtv.firebase.chat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import dungtv.firebase.chat.fragment.MyPostsFragment
import dungtv.firebase.chat.fragment.MyTopPostsFragment
import dungtv.firebase.chat.fragment.RecentPostsFragment
import dungtv.firebase.chat.widget.NavigationTabBar
import dungtv.firebase.chat.widget.OnTabBarSelectedIndexListener
import java.util.*

class MainActivity : BaseActivity() {

    private var mPagerAdapter: FragmentPagerAdapter? = null
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            private val mFragments = arrayOf<Fragment>(RecentPostsFragment(), MyPostsFragment(), MyTopPostsFragment())
            private val mFragmentNames = arrayOf(getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts), getString(R.string.heading_my_top_posts))
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }

            override fun getPageTitle(position: Int): CharSequence {
                return mFragmentNames[position]
            }
        }
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container)
        mViewPager!!.adapter = mPagerAdapter
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(mViewPager)

        // Button launches NewPostActivity
        findViewById<View>(R.id.fab_new_post).setOnClickListener {
            startActivity(Intent(this@MainActivity, NewPostActivity::class.java)) }

        initUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        return when (i) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initUI() {

        val colors = resources.getStringArray(R.array.default_preview)

        val navigationTabBar = findViewById<View>(R.id.ntb_horizontal) as NavigationTabBar

        navigationTabBar.models = initModelTabBar(colors)
        navigationTabBar.setViewPager(mViewPager, 0)

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.isBehaviorEnabled = true

        initAction(navigationTabBar)
    }

    private fun initModelTabBar(colors: Array<String>): ArrayList<NavigationTabBar.Model> {
        val models = ArrayList<NavigationTabBar.Model>()
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .title("Recent")
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_second),
                        Color.parseColor(colors[1]))
                        .title("My Posts")
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_third),
                        Color.parseColor(colors[2]))
                        .title("My Top Posts")
                        .build()
        )


        return models
    }

    private fun initAction(navigationTabBar: NavigationTabBar) {
        navigationTabBar.onTabBarSelectedIndexListener = object : OnTabBarSelectedIndexListener {
            override fun onStartTabSelected(model: NavigationTabBar.Model, index: Int) {}

            override fun onEndTabSelected(model: NavigationTabBar.Model, index: Int) {
                model.hideBadge()
            }
        }
        navigationTabBar.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

//        mFab.setOnClickListener {
//            for (i in 0 until navigationTabBar.models.size) {
//                val model = navigationTabBar.models[i]
//                navigationTabBar.postDelayed({
//                    val title = Random().nextInt(15).toString()
//                    if (!model.isBadgeShowed) {
//                        model.setBadgeTitle(title)
//                        model.showBadge()
//                    } else
//                        model.updateBadgeTitle(title)
//                }, (i * 100).toLong())
//            }
//
//            mCoordinatorLayout.postDelayed({
//                val snackBar = Snackbar.make(navigationTabBar, "Coordinator NTB", Snackbar.LENGTH_SHORT)
//                snackBar.view.setBackgroundColor(Color.parseColor("#9b92b3"))
//                (snackBar.view.findViewById<View>(R.id.snackbar_text) as TextView)
//                        .setTextColor(Color.parseColor("#423752"))
//                snackBar.show()
//            }, 1000)
//        }
    }

}
