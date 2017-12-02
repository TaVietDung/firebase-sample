package dungtv.firebase.chat.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User {

    lateinit var username: String
    lateinit var email: String

    constructor()

    constructor(username: String, email: String) {
        this.username = username
        this.email = email
    }

}
