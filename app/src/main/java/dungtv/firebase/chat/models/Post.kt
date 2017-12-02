package dungtv.firebase.chat.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Post {

    lateinit var uid: String
    lateinit var author: String
    lateinit var title: String
    lateinit var body: String

    constructor()

    constructor(uid: String, author: String, title: String, body: String) {
        this.uid = uid
        this.author = author
        this.title = title
        this.body = body
    }

    var starCount = 0
    var stars: HashMap<String, Boolean> = HashMap()

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("uid", uid)
        result.put("author", author)
        result.put("title", title)
        result.put("body", body)
        result.put("starCount", starCount)
        result.put("stars", stars)

        return result
    }

}
