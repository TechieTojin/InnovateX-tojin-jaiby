package com.example.budgetbuddy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hirumitha.budget.buddy.R
import com.example.budgetbuddy.models.ChatMessage

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_ASSISTANT = 2
    
    private val messages = mutableListOf<ChatMessage>()
    
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
    
    fun clear() {
        val size = messages.size
        messages.clear()
        notifyItemRangeRemoved(0, size)
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isFromUser) VIEW_TYPE_USER else VIEW_TYPE_ASSISTANT
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_user_message, parent, false)
                UserMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_assistant_message, parent, false)
                AssistantMessageViewHolder(view)
            }
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AssistantMessageViewHolder -> holder.bind(message)
        }
    }
    
    override fun getItemCount(): Int = messages.size
    
    inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        
        fun bind(message: ChatMessage) {
            messageTextView.text = message.message
            timeTextView.text = message.getFormattedTime()
        }
    }
    
    inner class AssistantMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        
        fun bind(message: ChatMessage) {
            messageTextView.text = message.message
            timeTextView.text = message.getFormattedTime()
        }
    }
} 