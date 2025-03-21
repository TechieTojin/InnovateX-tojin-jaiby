package com.example.budgetbuddy.fragments.assistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hirumitha.budget.buddy.R
import com.example.budgetbuddy.adapters.ChatAdapter
import com.example.budgetbuddy.models.ChatMessage
import com.example.budgetbuddy.utils.FinancialAssistant
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class AIAssistantFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var suggestionsChipGroup: ChipGroup
    
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var financialAssistant: FinancialAssistant

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ai_assistant, container, false)
        
        // Initialize views
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        messageEditText = view.findViewById(R.id.messageEditText)
        sendButton = view.findViewById(R.id.sendButton)
        suggestionsChipGroup = view.findViewById(R.id.suggestionsChipGroup)
        
        // Initialize chat adapter
        chatAdapter = ChatAdapter()
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter
        
        // Initialize financial assistant
        financialAssistant = FinancialAssistant()
        
        // Set up click listeners
        setupClickListeners()
        
        // Add welcome message
        addAssistantMessage("Hello! I'm your TojTrack financial assistant. How can I help you today?")
        
        return view
    }
    
    private fun setupClickListeners() {
        // Send button click listener
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageEditText.text.clear()
            }
        }
        
        // Suggestion chips click listeners
        setupSuggestionChips()
    }
    
    private fun setupSuggestionChips() {
        val suggestionChip1 = suggestionsChipGroup.findViewById<Chip>(R.id.suggestionChip1)
        val suggestionChip2 = suggestionsChipGroup.findViewById<Chip>(R.id.suggestionChip2)
        val suggestionChip3 = suggestionsChipGroup.findViewById<Chip>(R.id.suggestionChip3)
        val suggestionChip4 = suggestionsChipGroup.findViewById<Chip>(R.id.suggestionChip4)
        
        suggestionChip1.setOnClickListener { sendMessage(suggestionChip1.text.toString()) }
        suggestionChip2.setOnClickListener { sendMessage(suggestionChip2.text.toString()) }
        suggestionChip3.setOnClickListener { sendMessage(suggestionChip3.text.toString()) }
        suggestionChip4.setOnClickListener { sendMessage(suggestionChip4.text.toString()) }
    }
    
    private fun sendMessage(message: String) {
        // Add user message to chat
        addUserMessage(message)
        
        // Generate assistant response
        financialAssistant.generateResponse(message) { response ->
            addAssistantMessage(response)
        }
    }
    
    private fun addUserMessage(message: String) {
        val chatMessage = ChatMessage(
            message = message,
            isFromUser = true
        )
        chatAdapter.addMessage(chatMessage)
        scrollToBottom()
    }
    
    private fun addAssistantMessage(message: String) {
        val chatMessage = ChatMessage(
            message = message,
            isFromUser = false
        )
        chatAdapter.addMessage(chatMessage)
        scrollToBottom()
    }
    
    private fun scrollToBottom() {
        chatRecyclerView.post {
            chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }
    }
} 