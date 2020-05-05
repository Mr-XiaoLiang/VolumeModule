package com.lollipop.lpreference.dialog

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lpreference.R
import com.lollipop.lpreference.util.*
import com.lollipop.lpreference.value.PhotoInfo
import com.lollipop.lpreference.view.CirclePointView
import kotlinx.android.synthetic.main.fragment_images_panel.*

/**
 * @author lollipop
 * @date 2020-02-14 22:02
 * 图片选择的对话框
 */
class ImagesPanelDialogFragment : BaseDialog() {

    companion object {
        private const val SPAN_COUNT = 4
        private const val REQUEST_PERMISSION_READ = 233

        fun show(context: Context, selected: ArrayList<Uri>? = null, max: Int = -1,
                 callback: (Array<Uri>) -> Unit) {
            ImagesPanelDialogFragment().apply {
                selectedImagesCallback = callback
                maxSize = max
                selected?.let { info ->
                    presetUriList.addAll(info)
                }
            }.show(context, "ImagesPanelDialogFragment")
        }
    }

    override val contextId: Int
        get() = R.layout.fragment_images_panel

    private val photoAlbumHelper = PhotoAlbumHelper()

    private var maxSize = -1

    private var selectedImagesCallback: ((Array<Uri>) -> Unit)? = null

    private var presetUriList = ArrayList<Uri>()

    private val adapter: PhotoAdapter by lazy {
        PhotoAdapter(photoAlbumHelper.data, SPAN_COUNT, {
            isItemChecked(it)
        }, {
            onItemClick(it)
        }, {
            onItemLongClick(it)
        })
    }

    private val scaleImageHelper: ScaleImageHelper by lazy {
        ScaleImageHelper.with(previewView)
    }

    private fun isItemChecked(position: Int): Int {
        val index = photoAlbumHelper.isChecked(position)
        if (index < 0) {
            return 0
        }
        return if (maxSize == 1) { -1 } else  { index + 1 }
    }

    private fun onItemClick(holder: PhotoHolder): Boolean {
        val position = holder.adapterPosition
        if (position < 0 || position >= photoAlbumHelper.data.size) {
            return false
        }
        val info = photoAlbumHelper.get(position)
        val selectedIndex = photoAlbumHelper.selectedIndex(info)
        if (selectedIndex < 0) {
            if (maxSize == 1) {
                while (photoAlbumHelper.selectedSize > 0) {
                    val oldInfo = photoAlbumHelper.selected.removeAt(0)
                    adapter.notifyItemChanged(photoAlbumHelper.data.indexOf(oldInfo))
                }
                photoAlbumHelper.selected.add(info)
                updateSelectedSize()
                return true
            }
            if (maxSize > 1 && maxSize <= photoAlbumHelper.selectedSize) {
                return false
            }
            photoAlbumHelper.selected.add(info)
            updateSelectedSize()
            return true
        }
        photoAlbumHelper.selected.remove(info)
        updateSelectedSize()
        for (index in selectedIndex until photoAlbumHelper.selectedSize) {
            val updatePosition = photoAlbumHelper.positionBySelectedIndex(index)
            adapter.notifyItemChanged(updatePosition)
        }
        return true
    }

    private fun onItemLongClick(holder: PhotoHolder) {
        val position = holder.adapterPosition
        if (position < 0 || position >= photoAlbumHelper.data.size) {
            return
        }
        if (floatingImageView.visibility != View.VISIBLE) {
            if (floatingImageView.isPortrait()) {
                floatingImageView.translationX = floatingImageView.width.toFloat()
                floatingImageView.animate().let { animator ->
                    animator.cancel()
                    animator.translationX(0F)
                    animator.lifecycleBinding {
                        onStart {
                            floatingImageView.visibility = View.VISIBLE
                            removeThis(it)
                        }
                    }
                    animator.start()
                }
            } else {
                floatingImageView.visibility = View.VISIBLE
            }
        }
        scaleImageHelper.reset()
        photoAlbumHelper.get(position).loadTo(previewView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagesGroup.layoutManager = GridLayoutManager(imagesGroup.context, SPAN_COUNT)
        imagesGroup.adapter = adapter
        adapter.notifyDataSetChanged()
        photoAlbumHelper.onComplete {
            adapter.notifyDataSetChanged()
            if (photoAlbumHelper.isEmpty) {
                statusView.visibility = View.VISIBLE
                statusView.setImageResource(R.drawable.ic_folder_open_black_24dp)
            } else {
                statusView.visibility = View.INVISIBLE
            }
            photoAlbumHelper.selected.clear()
            photoAlbumHelper.data.forEach { info ->
                for (preset in presetUriList) {
                    if (info.path == preset) {
                        photoAlbumHelper.selected.add(info)
                    }
                }
            }
            updateSelectedSize()
        }

        photoAlbumHelper.onError {
            adapter.notifyDataSetChanged()
            statusView.visibility = View.VISIBLE
            statusView.setImageResource(R.drawable.ic_error_outline_black_24dp)
        }

        closePreviewBtn.setOnClickListener {
            if (closePreviewBtn.isPortrait()) {
                floatingImageView.animate().let { animator ->
                    animator.cancel()
                    animator.translationX(floatingImageView.width.toFloat())
                    animator.lifecycleBinding {
                        onEnd {
                            floatingImageView.visibility = View.INVISIBLE
                            removeThis(it)
                        }
                    }
                    animator.start()
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedSize() {
        if (maxSize == 1) {
            if (sizeView.visibility != View.GONE) {
                sizeView.visibility = View.GONE
            }
            return
        }
        if (sizeView.visibility != View.VISIBLE) {
            sizeView.visibility = View.VISIBLE
        }
        if (maxSize < 1) {
            sizeView.text = photoAlbumHelper.selectedSize.toString()
        } else {
            sizeView.text = "${photoAlbumHelper.selectedSize}/$maxSize"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        photoAlbumHelper.onComplete(null)
        photoAlbumHelper.onError(null)
        val uriArray = Array(photoAlbumHelper.selectedSize) {
            photoAlbumHelper.selected[it].path
        }
        selectedImagesCallback?.invoke(uriArray)
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (it.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    it.requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION_READ)
                    return
                }
            }
            photoAlbumHelper.initData(it)
        }
    }

    private class PhotoAdapter(private val data: ArrayList<PhotoInfo>,
                               private val emptyItemSize: Int,
                               private val isChecked: (Int) -> Int,
                               private val onClickListener: (PhotoHolder) -> Boolean,
                               private val onLongClick: (PhotoHolder) -> Unit):
        RecyclerView.Adapter<PhotoHolder>() {

        companion object {
            private const val ITEM_NORMAL = 0
            private const val ITEM_EMPTY = 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            return PhotoHolder.create(parent,
                viewType == ITEM_EMPTY,
                isChecked, onClickListener, onLongClick)
        }

        override fun getItemCount(): Int {
            return data.size + emptyItemSize
        }

        override fun getItemViewType(position: Int): Int {
            return if (position < data.size) { ITEM_NORMAL } else { ITEM_EMPTY }
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            if (getItemViewType(position) == ITEM_NORMAL) {
                holder.onBind(data[position])
            }
        }

    }

    private class PhotoHolder
        private constructor(private val isEmpty: Boolean,
                            private val isChecked: (Int) -> Int,
                            private val onClickListener: (PhotoHolder) -> Boolean,
                            private val onLongClick: (PhotoHolder) -> Unit,
                            view: View) :
            RecyclerView.ViewHolder(view) {
        companion object {
            fun create(group: ViewGroup, isEmpty: Boolean, isChecked: (Int) -> Int,
                       onClick: (PhotoHolder) -> Boolean,
                       onLongClick: (PhotoHolder) -> Unit): PhotoHolder {
                return PhotoHolder(isEmpty, isChecked, onClick, onLongClick,
                    LayoutInflater.from(group.context)
                        .inflate(R.layout.item_image_panel, group, false))
            }
        }

        init {
            if (isEmpty) {
                itemView.visibility = View.INVISIBLE
            } else {
                itemView.setOnClickListener {
                    onItemClick()
                }
                itemView.setOnLongClickListener{
                    onLongClick(this)
                    true
                }
            }
        }

        private val imageView: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.imageView)
        }

        private val numberView: CirclePointView by lazy {
            itemView.findViewById<CirclePointView>(R.id.numberView)
        }

        private val checkedIconView: ImageView by lazy {
            itemView.findViewById<ImageView>(R.id.checkedIconView)
        }

        fun onBind(info: PhotoInfo) {
            if (isEmpty) {
                return
            }
            info.loadTo(imageView)
            checkItemStatus(false)
        }

        private fun onItemClick() {
            if (isEmpty) {
                return
            }
            if (onClickListener(this)) {
                checkItemStatus(true)
            }
        }

        private fun checkItemStatus(isAnimation: Boolean) {
            val status = isChecked(adapterPosition)
            val checked = status != 0
            val number = if (status > 0) { status.toString() } else { "" }
            if (isAnimation) {
                if (checked) {
                    numberView.autoText = number
                    animationChecked(numberView)
                    if (number.isEmpty()) {
                        animationChecked(checkedIconView)
                    }
                } else {
                    animationNotChecked(checkedIconView)
                    animationNotChecked(numberView)
                }
            } else {
                numberView.autoText = number
                numberView.visibility = if (checked) { View.VISIBLE } else { View.INVISIBLE }
                checkedIconView.visibility = if (checked && number.isEmpty()) { View.VISIBLE } else { View.INVISIBLE }
            }
        }

        private fun animationChecked(view: View) {
            view.scaleX = 0F
            view.animate().let { animator ->
                animator.cancel()
                animator.scaleX(1F)
                animator.lifecycleBinding {
                    onStart {
                        if (view.visibility != View.VISIBLE) {
                            view.visibility = View.VISIBLE
                        }
                        removeThis(it)
                    }
                }
                animator.start()
            }
        }

        private fun animationNotChecked(view: View) {
            if (view.visibility != View.VISIBLE) {
                return
            }
            view.animate().let { animator ->
                animator.cancel()
                animator.scaleX(0F)
                animator.lifecycleBinding {
                    onEnd {
                        view.visibility = View.INVISIBLE
                        removeThis(it)
                    }
                }
                animator.start()
            }
        }

    }

}