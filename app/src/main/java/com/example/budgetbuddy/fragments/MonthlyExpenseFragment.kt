package com.example.budgetbuddy.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hirumitha.budget.buddy.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MonthlyExpenseFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var monthlyExpenseTitle: TextView
    private lateinit var monthYearText: TextView
    private lateinit var previousMonthButton: ImageButton
    private lateinit var nextMonthButton: ImageButton
    private lateinit var totalIncomeValue: TextView
    private lateinit var totalExpensesValue: TextView
    private lateinit var netBalanceValue: TextView
    private lateinit var savingsRateValue: TextView
    private lateinit var exportButton: MaterialButton
    
    private val calendar = Calendar.getInstance()
    private val monthYearFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val currencyFormatter = NumberFormat.getCurrencyInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_monthly_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        initViews(view)
        
        // Set up month navigation
        setupMonthNavigation()
        
        // Load initial data
        updateMonthDisplay()
        loadMonthlyData()
        setupPieChart()
    }
    
    private fun initViews(view: View) {
        pieChart = view.findViewById(R.id.expensePieChart)
        monthlyExpenseTitle = view.findViewById(R.id.monthlyExpenseTitle)
        monthYearText = view.findViewById(R.id.monthYearText)
        previousMonthButton = view.findViewById(R.id.prevMonthButton)
        nextMonthButton = view.findViewById(R.id.nextMonthButton)
        totalIncomeValue = view.findViewById(R.id.totalIncomeValue)
        totalExpensesValue = view.findViewById(R.id.totalExpensesValue)
        netBalanceValue = view.findViewById(R.id.netBalanceValue)
        savingsRateValue = view.findViewById(R.id.savingsRateValue)
        exportButton = view.findViewById(R.id.exportButton)
        
        // Set up export button
        exportButton.setOnClickListener {
            exportMonthlyReport()
        }
    }
    
    private fun setupMonthNavigation() {
        previousMonthButton.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthDisplay()
            loadMonthlyData()
        }
        
        nextMonthButton.setOnClickListener {
            // Don't allow navigating to future months
            val currentCalendar = Calendar.getInstance()
            if (calendar.get(Calendar.YEAR) < currentCalendar.get(Calendar.YEAR) ||
                (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                 calendar.get(Calendar.MONTH) < currentCalendar.get(Calendar.MONTH))) {
                calendar.add(Calendar.MONTH, 1)
                updateMonthDisplay()
                loadMonthlyData()
            } else {
                Toast.makeText(context, "Cannot view future months", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateMonthDisplay() {
        val monthYearText = monthYearFormatter.format(calendar.time)
        this.monthYearText.text = monthYearText
        monthlyExpenseTitle.text = "$monthYearText Expenses"
    }
    
    private fun loadMonthlyData() {
        // In a real app, this would load data from a database or API
        // For now, we'll use mock data
        
        // Mock income and expense data
        val income = 3500.0
        val expenses = 2850.0
        val netBalance = income - expenses
        val savingsRate = (netBalance / income) * 100
        
        // Update UI with formatted values
        totalIncomeValue.text = currencyFormatter.format(income)
        totalExpensesValue.text = currencyFormatter.format(expenses)
        netBalanceValue.text = currencyFormatter.format(netBalance)
        savingsRateValue.text = String.format("%.1f%%", savingsRate)
        
        // Update pie chart
        updatePieChartData()
    }
    
    private fun setupPieChart() {
        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Expenses"
            setCenterTextSize(16f)
            setUsePercentValues(true)
            legend.isEnabled = false // We're using our custom legend
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(12f)
            setDrawEntryLabels(false)
        }
    }
    
    private fun updatePieChartData() {
        // In a real app, this would use actual expense categories from a database
        // For now, we'll use mock data
        val entries = ArrayList<PieEntry>()
        
        // Mock expense categories and amounts
        entries.add(PieEntry(42.1f, "Housing"))
        entries.add(PieEntry(22.8f, "Food"))
        entries.add(PieEntry(15.8f, "Transportation"))
        entries.add(PieEntry(12.3f, "Entertainment"))
        entries.add(PieEntry(7.0f, "Other"))
        
        val dataSet = PieDataSet(entries, "Expense Categories")
        dataSet.apply {
            colors = listOf(
                resources.getColor(R.color.pie_chart_positive, null),
                resources.getColor(R.color.teal_200, null),
                resources.getColor(R.color.pie_chart_negative, null),
                resources.getColor(R.color.purple_200, null),
                resources.getColor(R.color.teal_700, null)
            )
            sliceSpace = 3f
            selectionShift = 5f
        }
        
        val data = PieData(dataSet)
        data.apply {
            setValueFormatter(PercentFormatter(pieChart))
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }
        
        pieChart.data = data
        pieChart.invalidate() // refresh
    }
    
    private fun exportMonthlyReport() {
        // In a real app, this would generate a PDF or CSV report
        // For now, just show a toast
        val monthYear = monthYearFormatter.format(calendar.time)
        Toast.makeText(
            context,
            "Exporting $monthYear financial report...",
            Toast.LENGTH_LONG
        ).show()
    }
} 