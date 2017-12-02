package dungtv.firebase.chat.widget

// Out listener for selected index
interface OnTabBarSelectedIndexListener {
    fun onStartTabSelected(model: NavigationTabBar.Model, index: Int)

    fun onEndTabSelected(model: NavigationTabBar.Model, index: Int)
}