package org.duckdns.pynetti.forecast

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter

class MyCursorAdapter(context: Context, cursor: Cursor, flags: Int) :
    CursorAdapter(context, cursor, flags) {

    private val cursorInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemViewType(position: Int): Int {
        val cursor = getItem(position) as Cursor
        return when (cursor.getString(cursor.getColumnIndex("type"))) {
            "title" -> 0
            "favorite" -> 1
            "result" -> 2
            else -> 0
        }
    }

    override fun getViewTypeCount(): Int {
        return 3
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        if (cursor.getString(cursor.getColumnIndex("type")) == "favorite") {
            val imageButton = view.findViewById<ImageView>(R.id.favoriteButton)
            imageButton.setImageResource(R.drawable.ic_star_yellow_24dp)
            imageButton.setOnClickListener {
                val favoriteString =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                if (MainActivity.favoritesList.contains(favoriteString)) {
                    MainActivity.favoritesList.remove(favoriteString)
                    imageButton.setImageResource(R.drawable.ic_star_border_yellow_24dp)
                } else {
                    MainActivity.favoritesList.add(favoriteString)
                    imageButton.setImageResource(R.drawable.ic_star_yellow_24dp)
                }
            }
        }

        val textViewTitle = view.findViewById<TextView>(R.id.itemLabel)
        val title = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        textViewTitle.text = title
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return when (cursor.getString(cursor.getColumnIndex("type"))) {
            "title" -> cursorInflater.inflate(R.layout.search_title_item, parent, false)
            "favorite" -> cursorInflater.inflate(R.layout.favorite_item, parent, false)
            "result" -> cursorInflater.inflate(R.layout.search_item, parent, false)
            else -> cursorInflater.inflate(R.layout.search_item, parent, false)
        }

    }

}