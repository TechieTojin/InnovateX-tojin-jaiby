package com.hirumitha.budget.buddy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.hirumitha.budget.buddy.R
import com.hirumitha.budget.buddy.database.TransactionDBHelper
import com.hirumitha.budget.buddy.databinding.FragmentInsightsBinding
import com.hirumitha.budget.buddy.utils.BudgetAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dbHelper: TransactionDBHelper
    private lateinit var budgetAnalyzer: BudgetAnalyzer
    
    // Sample monthly income and budget limits for demo
    private val monthlyIncome = 5000f
    private val budgetLimits = mapOf(
        "Food" to 800f,
        "Transport" to 300f,
        "Shopping" to 400f,
        "Entertainment" to 300f,
        "Bills" to 1000f,
        "Housing" to 1500f,
        "Health" to 200f,
        "Education" to 200f,
        "Other" to 300f
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize components
        dbHelper = TransactionDBHelper(requireContext())
        budgetAnalyzer = BudgetAnalyzer()
        
        // Set up loading state initially
        showLoadingState(true)
        
        // Load data
        loadData()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun loadData() {
        lifecycleScope.launch {
            try {
                // Get all transactions from database
                val transactions = withContext(Dispatchers.IO) {
                    dbHelper.getAllTransactions()
                }
                
                if (transactions.isEmpty()) {
                    showEmptyState()
                    return@launch
                }
                
                // Generate AI insights
                val insights = budgetAnalyzer.analyzeSpending(
                    transactions,
                    monthlyIncome,
                    budgetLimits
                )
                
                // Set up spending charts
                setupCategoryPieChart(transactions)
                setupMonthlyBarChart(transactions)
                
                // Display AI insights
                displayInsights(insights)
                
                // Show the content
                binding.contentGroup.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error loading insights: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                showEmptyState()
            } finally {
                showLoadingState(false)
            }
        }
    }
    
    private fun setupCategoryPieChart(transactions: List<com.hirumitha.budget.buddy.models.TransactionModel>) {
        // Calculate spending by category
        val spendingByCategory = transactions
            .filter { it.type == "expense" }
            .groupBy { it.category }
            .mapValues { entry -> 
                entry.value.sumOf { it.amount.toDouble() }.toFloat() 
            }
        
        val totalSpending = spendingByCategory.values.sum()
        
        // Create pie chart entries
        val entries = ArrayList<PieEntry>()
        
        spendingByCategory.forEach { (category, amount) ->
            val percentage = (amount / totalSpending) * 100
            if (percentage > 1) { // Only show categories with more than 1%
                entries.add(PieEntry(percentage, category))
            }
        }
        
        // Set up pie chart dataset
        val dataSet = PieDataSet(entries, "Expense Categories")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        
        // Create pie data
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        
        // Setup chart
        binding.pieChart.apply {
            this.data = data
            description.isEnabled = false
            setUsePercentValues(true)
            setEntryLabelTextSize(12f)
            legend.textSize = 12f
            centerText = "Spending by Category"
            setCenterTextSize(16f)
            isDrawHoleEnabled = true
            setHoleColor(android.R.color.transparent)
            animateY(1000)
            invalidate()
        }
    }
    
    private fun setupMonthlyBarChart(transactions: List<com.hirumitha.budget.buddy.models.TransactionModel>) {
        // Group spending by month
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -5) // Show last 6 months
        
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        
        // Format for month labels and grouping
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val monthYearFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        for (i in 0 until 6) {
            val month = monthYearFormat.format(calendar.time)
            val monthLabel = monthFormat.format(calendar.time)
            
            // Sum all expenses for this month
            val monthlyExpenses = transactions
                .filter { 
                    try {
                        val transDate = dateFormat.parse(it.date)
                        transDate?.let { date ->
                            monthYearFormat.format(date) == month && it.type == "expense"
                        } ?: false
                    } catch (e: Exception) {
                        false
                    }
                }
                .sumOf { it.amount.toDouble() }
                .toFloat()
            
            entries.add(BarEntry(i.toFloat(), monthlyExpenses))
            labels.add(monthLabel)
            
            calendar.add(Calendar.MONTH, 1)
        }
        
        // Create dataset
        val dataSet = BarDataSet(entries, "Monthly Expenses")
        dataSet.colors = listOf(resources.getColor(R.color.primary, null))
        dataSet.valueTextSize = 10f
        
        // Create bar data
        val data = BarData(dataSet)
        
        // Setup chart
        binding.barChart.apply {
            this.data = data
            description.isEnabled = false
            xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = true
            animateY(1000)
            invalidate()
        }
    }
    
    private fun displayInsights(insights: List<String>) {
        binding.insightsContainer.removeAllViews()
        
        insights.forEach { insight ->
            val view = layoutInflater.inflate(R.layout.item_insight, binding.insightsContainer, false)
            val textView = view.findViewById<android.widget.TextView>(R.id.tv_insight)
            textView.text = insight
            binding.insightsContainer.addView(view)
        }
    }
    
    private fun showEmptyState() {
        binding.contentGroup.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
        binding.emptyStateText.text = "Add some transactions to see AI-powered insights and analytics here."
    }
    
    private fun showLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.contentGroup.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
} 