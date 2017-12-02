package dungtv.firebase.chat.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import dungtv.firebase.chat.R
import dungtv.firebase.chat.models.Post


class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var titleView: TextView = itemView.findViewById(R.id.post_title)
    var authorView: TextView = itemView.findViewById(R.id.post_author)
    var starView: ImageView = itemView.findViewById(R.id.star)
    var numStarsView: TextView = itemView.findViewById(R.id.post_num_stars)
    var bodyView: TextView = itemView.findViewById(R.id.post_body)

    fun bindToPost(post: Post, starClickListener: View.OnClickListener) {
        titleView.text = post.title
        authorView.text = post.author
        numStarsView.text = post.starCount.toString()
        bodyView.text = post.body

        starView.setOnClickListener(starClickListener)
    }
}
