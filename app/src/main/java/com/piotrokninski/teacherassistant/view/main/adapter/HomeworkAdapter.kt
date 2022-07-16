package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.databinding.HomeworkListItemBinding
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.util.AppConstants
import java.text.SimpleDateFormat

class HomeworkAdapter(
    private val clickListener: (Homework) -> Unit,
    private val viewType: String,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val homeworkItems = ArrayList<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {

            Item.Header.ID -> {
                HeaderViewHolder(HeaderViewHolder.initBinding(parent), context)
            }

            Item.Homework.ID -> {
                HomeworkViewHolder(HomeworkViewHolder.initBinding(parent), context)
            }

            else -> throw IllegalArgumentException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Item.Header.ID -> {
                (holder as HeaderViewHolder).bind(
                    (homeworkItems[position] as Item.Header)
                )
            }

            Item.Homework.ID -> {
                (holder as HomeworkViewHolder).bind(
                    (homeworkItems[position] as Item.Homework).homework,
                    clickListener,
                    viewType
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return homeworkItems.size
    }

    fun setItems(homeworkItems: List<Item>) {
        this.homeworkItems.clear()
        this.homeworkItems.addAll(homeworkItems)
        notifyDataSetChanged()

    }

    override fun getItemViewType(position: Int): Int {
        return when (homeworkItems[position]) {
            is Item.Header -> Item.Header.ID
            is Item.Homework -> Item.Homework.ID
        }
    }

    class HomeworkViewHolder(
        private val binding: HomeworkListItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup) : HomeworkListItemBinding{
                val layoutInflater = LayoutInflater.from(parent.context)
                return HomeworkListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(
            homework: Homework,
            clickListener: (Homework) -> Unit,
            viewType: String
        ) {
            binding.homework = homework
            binding.homeworkItemLayout.setOnClickListener { clickListener(homework) }

            val simpleFormat = SimpleDateFormat.getInstance()

            binding.homeworkItemDate.text = simpleFormat.format(homework.dueDate!!)

            when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> {

                    binding.homeworkItemUserProfession.text = context.getString(
                        R.string.course_item_profession_text,
                        context.getString(R.string.tutor_title_text)
                    )
                    binding.homeworkItemUserFullName.text = homework.tutorFullName
                }

                AppConstants.VIEW_TYPE_TUTOR -> {
                    binding.homeworkItemUserProfession.text = context.getString(
                        R.string.course_item_profession_text,
                        context.getString(R.string.student_title_text)
                    )
                    binding.homeworkItemUserFullName.text = homework.studentFullName
                }
            }
        }
    }

    class HeaderViewHolder(
        private val binding: HeaderListItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup) : HeaderListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return HeaderListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(headerAdapterItem: Item.Header) {
            binding.headerItemTitle.text = context.getString(headerAdapterItem.titleId)
            binding.headerItemDivider.visibility = View.GONE
        }
    }

    sealed class Item {

        abstract val id: String


        data class Header(val titleId: Int) : Item() {
            override val id = titleId.toString()

            companion object {
                const val ID = 0
            }
        }

        data class Homework(val homework: com.piotrokninski.teacherassistant.model.course.Homework) : Item() {
            override val id = homework.toString()

            companion object {
                const val ID = 1
            }
        }
    }
}