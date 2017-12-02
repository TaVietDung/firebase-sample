package dungtv.firebase.chat.models

import com.google.firebase.database.IgnoreExtraProperties

// [START comment_class]
@IgnoreExtraProperties
class Comment {

    lateinit var uid: String
    lateinit var author: String
    lateinit var text: String

    constructor()

    constructor(uid: String, author: String, text: String) {
        this.uid = uid
        this.author = author
        this.text = text
    }
}
