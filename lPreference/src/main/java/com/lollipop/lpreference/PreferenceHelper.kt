package com.lollipop.lpreference

import android.content.Context
import android.text.TextUtils
import androidx.collection.ArraySet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lpreference.info.BasePreferenceInfo
import com.lollipop.lpreference.info.PreferenceInfo
import com.lollipop.lpreference.value.ColorArray
import com.lollipop.lpreference.value.ImageArray
import com.lollipop.lpreference.value.PreferenceValue
import com.lollipop.lpreference.view.ItemBuilder

/**
 * @author lollipop
 * @date 2020-01-17 00:06
 * 偏好设置的辅助器
 */
class PreferenceHelper(private val group: RecyclerView) {

    companion object {

        inline fun <reified T> put(context: Context, key: String, value: T) {
            if (TextUtils.isEmpty(key)) {
                return
            }
            val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit()
            when (value) {
                is String -> {
                    preference.putString(key, value)
                }
                is Int -> {
                    preference.putInt(key, value)
                }
                is Boolean -> {
                    preference.putBoolean(key, value)
                }
                is Float -> {
                    preference.putFloat(key, value)
                }
                is Double -> {
                    preference.putFloat(key, value.toFloat())
                }
                is Long -> {
                    preference.putLong(key, value)
                }
                is PreferenceValue -> {
                    preference.putString(key, value.serialization())
                }
                is Set<*> -> {
                    if (value.isEmpty()) {
                        preference.putStringSet(key, ArraySet())
                    } else {
                        val set = ArraySet<String>()
                        for (v in value) {
                            when (v) {
                                null -> {
                                    set.add("")
                                }
                                is String -> {
                                    set.add(v)
                                }
                                else -> {
                                    set.add(v.toString())
                                }
                            }
                        }
                    }
                }
                else -> {
                    preference.putString(key, value.toString())
                }
            }
            preference.apply()
        }

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T> get(context: Context, key: String, def: T): T {
            if (TextUtils.isEmpty(key)) {
                return def
            }
            val preference = context.getSharedPreferences(key, Context.MODE_PRIVATE)
            val value = when (def) {
                is PreferenceValue -> {
                    def.parse(preference.getString(key, def.serialization())?: def.serialization())
                    def
                }
                is String -> {
                    preference.getString(key, def)
                }
                is Int -> {
                    preference.getInt(key, def)
                }
                is Boolean -> {
                    preference.getBoolean(key, def)
                }
                is Float -> {
                    preference.getFloat(key, def)
                }
                is Long -> {
                    preference.getLong(key, def)
                }
                is Set<*> -> {
                    preference.getStringSet(key, def as Set<String>)
                }
                else -> def
            }?:def
            return value as T
        }

        fun findItemByKey(key: String, data: ArrayList<PreferenceInfo>): BasePreferenceInfo<*>? {
            for (info in data) {
                if (info is BasePreferenceInfo<*> && info.key == key) {
                    return info
                }
            }
            return null
        }

        fun getColor(context: Context, key: String, def: ColorArray): ColorArray {
            return get(context, key, def)
        }

        fun getImage(context: Context, key: String, def: ImageArray): ImageArray {
            return get(context, key, def)
        }

    }

    private val context: Context
        get() = group.context

    private val data = ArrayList<PreferenceInfo>()

    private val adapter: PreferenceAdapter by lazy {
        PreferenceAdapter(data)
    }

    fun onPreferenceChange(listener:  ((BasePreferenceInfo<*>) -> Unit)?) {
        adapter.onPreferenceChangeListener = listener
    }

    private fun init() {
        if (group.layoutManager == null) {
            group.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        if (group.adapter == null) {
            group.adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }

    fun addItem(vararg info: PreferenceInfo) {
        for (i in info) {
            if (i is BasePreferenceInfo<*>) {
                checkItem(i.key)
            }
            data.add(i)
        }
    }

    fun removeItem(key: String) {
        findItemByKey(key)?.let {
            data.remove(it)
        }
    }

    fun <T> getValue(key: String): T? {
        for (info in data) {
            if (info is BasePreferenceInfo<*> && info.key == key) {
                return try {
                    info.getValue(context) as T
                } catch (e: ClassCastException) {
                    null
                }
            }
        }
        return null
    }

    fun build(run: ItemBuilder.() -> Unit) {
        val builder = ItemBuilder()
        builder.apply(run)
        for (item in builder.itemList) {
            addItem(item)
        }
        init()
    }

    private fun checkItem(key: String) {
        if (findItemByKey(key) != null) {
            throw RuntimeException("Already contains an item with the same key")
        }
    }

    private fun findItemByKey(key: String): BasePreferenceInfo<*>? {
        return findItemByKey(key, data)
    }

}