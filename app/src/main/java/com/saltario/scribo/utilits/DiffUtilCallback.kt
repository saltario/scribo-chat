package com.saltario.scribo.utilits

import androidx.recyclerview.widget.DiffUtil
import com.saltario.scribo.models.Common

/**
Используется для обновления RecycleView
 Чтобы обновить и отобразить только те элементы, которые новые
 и не перерисовывать весь список

 */

class DiffUtilCallback(
    private val oldList: List<Common>,
    private val newList: List<Common>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    // Сравниваем по времени (лонг), так как у сообщения нету id
    // Первичная проверка
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].time == newList[newItemPosition].time

    // Проверка семантическая (по контенту)
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}