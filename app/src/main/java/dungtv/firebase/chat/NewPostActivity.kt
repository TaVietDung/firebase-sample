package dungtv.firebase.chat

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import dungtv.firebase.chat.models.Post
import dungtv.firebase.chat.models.User
import java.util.*

class NewPostActivity : BaseActivity() {

    // [START declare_database_ref]
    private var mDatabase: DatabaseReference? = null
    // [END declare_database_ref]

    private var mTitleField: EditText? = null
    private var mBodyField: EditText? = null
    private var mSubmitButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().reference
        // [END initialize_database_ref]

        mTitleField = findViewById(R.id.field_title)
        mBodyField = findViewById(R.id.field_body)
        mSubmitButton = findViewById(R.id.fab_submit_post)

        mSubmitButton!!.setOnClickListener { submitPost() }
    }

    private fun submitPost() {
        val title = mTitleField!!.text.toString()
        val body = mBodyField!!.text.toString()

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField!!.error = REQUIRED
            return
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField!!.error = REQUIRED
            return
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false)
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show()

        // [START single_value_read]
        val userId = uid
        mDatabase!!.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Get user value
                        val user = dataSnapshot.getValue(User::class.java)

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User $userId is unexpectedly null")
                            Toast.makeText(this@NewPostActivity,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, body)
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true)
                        finish()
                        // [END_EXCLUDE]
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                        // [START_EXCLUDE]
                        setEditingEnabled(true)
                        // [END_EXCLUDE]
                    }
                })
        // [END single_value_read]
    }

    private fun setEditingEnabled(enabled: Boolean) {
        mTitleField!!.isEnabled = enabled
        mBodyField!!.isEnabled = enabled
        if (enabled) {
            mSubmitButton!!.visibility = View.VISIBLE
        } else {
            mSubmitButton!!.visibility = View.GONE
        }
    }

    // [START write_fan_out]
    private fun writeNewPost(userId: String, username: String, title: String, body: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = mDatabase!!.child("posts").push().key
        val post = Post(userId, username, title, body)
        val postValues = post.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/posts/" + key, postValues)
        childUpdates.put("/user-posts/$userId/$key", postValues)

        mDatabase!!.updateChildren(childUpdates)
    }

    companion object {

        private val TAG = "NewPostActivity"
        private val REQUIRED = "Required"
    }
    // [END write_fan_out]
}
