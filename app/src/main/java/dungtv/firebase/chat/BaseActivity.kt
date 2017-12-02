package dungtv.firebase.chat

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth


open class BaseActivity : AppCompatActivity() {

    private var mProgressDialog: ProgressDialog? = null

    val uid: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.setMessage("Loading...")
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }


}
