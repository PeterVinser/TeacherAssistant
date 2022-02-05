package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.databinding.HomeworkListItemBinding
import com.piotrokninski.teacherassistant.model.adapteritem.HomeworkAdapterItem
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.util.AppConstants
import java.text.SimpleDateFormat

class HomeworkAdapter(
    private val clickListener: (Homework) -> Unit,
    private val viewType: String,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val homeworkItems = ArrayList<HomeworkAdapterItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            HOMEWORK_ITEM -> {
                HomeworkViewHolder(HomeworkViewHolder.initBinding(parent), context)
            }

            HEADER_ITEM -> {
                HeaderViewHolder(HeaderViewHolder.initBinding(parent), context)
            }

            else -> throw IllegalArgumentException("Unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            HOMEWORK_ITEM -> {
                (holder as HomeworkViewHolder).bind(
                    (homeworkItems[position] as HomeworkAdapterItem.HomeworkItem).homework,
                    clickListener,
                    viewType
                )
            }

            HEADER_ITEM -> {
                (holder as HeaderViewHolder).bind(
                    (homeworkItems[position] as HomeworkAdapterItem.HeaderItem)
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return homeworkItems.size
    }

    fun setHomework(homeworkItems: List<HomeworkAdapterItem>) {
        this.homeworkItems.clear()
        this.homeworkItems.addAll(homeworkItems)
        notifyDataSetChanged()

    }

    override fun getItemViewType(position: Int): Int {
        return when (homeworkItems[position]) {
            is HomeworkAdapterItem.HomeworkItem -> HOMEWORK_ITEM

            is HomeworkAdapterItem.HeaderItem -> HEADER_ITEM
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

        fun bind(headerAdapterItem: HomeworkAdapterItem.HeaderItem) {
            binding.headerItemTitle.text = context.getString(headerAdapterItem.titleId)
            binding.headerItemDivider.visibility = View.GONE
        }
    }

    companion object {
        const val HOMEWORK_ITEM = 1
        const val HEADER_ITEM = 2
    }
}