package ru.yourok.torrserve.ui.fragments.play.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.graphics.ColorUtils
import ru.yourok.torrserve.R
import ru.yourok.torrserve.app.App
import ru.yourok.torrserve.ext.append
import ru.yourok.torrserve.server.api.Viewed
import ru.yourok.torrserve.server.models.torrent.FileStat
import ru.yourok.torrserve.server.models.torrent.Torrent
import ru.yourok.torrserve.utils.Format
import ru.yourok.torrserve.utils.ThemeUtil
import ru.yourok.torrserve.utils.TorrentHelper
import java.io.File


class TorrentFilesAdapter : BaseAdapter() {
    private var files: List<FileStat> = listOf()
    private val typeFile = 0
    private val typeButton = 1
    var viewed = listOf<Viewed>()

    fun update(torrent: Torrent, viewed: List<Viewed>?) {
        files = TorrentHelper.getPlayableFiles(torrent)
        if (viewed != null) this.viewed = viewed
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val type: Int = getItemViewType(position)
        val themedContext = ContextThemeWrapper(App.context, ThemeUtil.selectedTheme)
        val color1 = ColorUtils.setAlphaComponent(ThemeUtil.getColorFromAttr(themedContext, R.attr.colorBright), 140)
        val color2 = ColorUtils.setAlphaComponent(ThemeUtil.getColorFromAttr(themedContext, R.attr.colorBright), 250)

        val vi = view ?: if (type == typeFile) LayoutInflater.from(parent?.context).inflate(R.layout.torrent_files_item, parent, false)
        else LayoutInflater.from(parent?.context).inflate(R.layout.torrent_files_button, parent, false)
        if (files.size > 1 && position == count - 1) return vi

        val file = files[position]
        val tvFileName = vi.findViewById<TextView>(R.id.tvFileName)
//        var title = ""
//        val path = File(file.path).parent.split("/")
//        path.forEach { it ->
//            title += "$it/"
//        }
        val path = File(file.path).parent
        if (!path.isNullOrEmpty()) { // split path
            tvFileName.apply {
                text = ""
                append("$path/\n", color1) // path
                append(File(file.path).name, color2) // file
            }
        }
//        title += File(file.path).name
//        vi.findViewById<TextView>(R.id.tvFileName)?.text = title

        val size = Format.byteFmt(file.length)
        vi.findViewById<TextView>(R.id.tvFileSize)?.text = size

        vi.findViewById<ImageView>(R.id.ivViewed)?.apply {
            visibility = View.GONE
            for (it in viewed) {
                if (it.file_index == file.id) {
                    visibility = View.VISIBLE
                    break
                }
            }
        }
        return vi
    }

    override fun getItem(p0: Int): Any? {
        if (p0 < 0 || p0 >= count) return null
        // play from beginning
        if (files.size > 1 && p0 == count - 1) return files[0]
        return files[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return if (files.size > 1) files.size + 1
        else files.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (files.size > 1 && position == count - 1) typeButton else typeFile
    }

    override fun getViewTypeCount(): Int {
        return 2
    }
}