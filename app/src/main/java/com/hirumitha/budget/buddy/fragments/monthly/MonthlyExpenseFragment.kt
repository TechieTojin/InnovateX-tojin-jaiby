package com.hirumitha.budget.buddy.fragments.monthly

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.hirumitha.budget.buddy.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MonthlyExpenseFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var monthYearText: TextView
    private lateinit var prevMonthButton: ImageButton
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
        initializeViews(view)
        setupMonthNavigation()
        updateMonthDisplay()
        loadMonthlyData()
        setupPieChart()
        updatePieChartData()
        setupExportButton()
    }

    private fun initializeViews(view: View) {
        pieChart = view.findViewById(R.id.expensePieChart)
        monthYearText = view.findViewById(R.id.monthYearText)
        prevMonthButton = view.findViewById(R.id.prevMonthButton)
        nextMonthButton = view.findViewById(R.id.nextMonthButton)
        totalIncomeValue = view.findViewById(R.id.totalIncomeValue)
        totalExpensesValue = view.findViewById(R.id.totalExpensesValue)
        netBalanceValue = view.findViewById(R.id.netBalanceValue)
        savingsRateValue = view.findViewById(R.id.savingsRateValue)
        exportButton = view.findViewById(R.id.exportButton)
    }

    private fun setupMonthNavigation() {
        prevMonthButton.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthDisplay()
            loadMonthlyData()
            updatePieChartData()
        }

        nextMonthButton.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthDisplay()
            loadMonthlyData()
            updatePieChartData()
        }
    }

    private fun updateMonthDisplay() {
        monthYearText.text = monthYearFormatter.format(calendar.time)
    }

    private fun loadMonthlyData() {
        // In a real app, this would load data from a database or API
        // For now, we'll use mock data
        val income = 5000.0
        val expenses = 3500.0
        val netBalance = income - expenses
        val savingsRate = if (income > 0) (netBalance / income) * 100 else 0.0

        totalIncomeValue.text = currencyFormatter.format(income)
        totalExpensesValue.text = currencyFormatter.format(expenses)
        netBalanceValue.text = currencyFormatter.format(netBalance)
        savingsRateValue.text = String.format("%.1f%%", savingsRate)

        // Set color based on positive or negative balance
        if (netBalance >= 0) {
            netBalanceValue.setTextColor(resources.getColor(R.color.pie_chart_positive, null))
        } else {
            netBalanceValue.setTextColor(resources.getColor(R.color.pie_chart_negative, null))
        }
    }

    private fun setupPieChart() {
        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleAlpha(0)
            holeRadius = 58f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            setUsePercentValues(true)
            legend.isEnabled = false
        }
    }

    private fun updatePieChartData() {
        // In a real app, this would use actual expense data
        // For now, we'll use mock data
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(30f, "Housing"))
        entries.add(PieEntry(20f, "Food"))
        entries.add(PieEntry(15f, "Transport"))
        entries.add(PieEntry(10f, "Entertainment"))
        entries.add(PieEntry(25f, "Other"))

        val dataSet = PieDataSet(entries, "Expense Categories")
        dataSet.apply {
            sliceSpace = 3f
            selectionShift = 5f
            colors = listOf(
                Color.rgb(64, 89, 128),
                Color.rgb(149, 165, 124),
                Color.rgb(217, 184, 162),
                Color.rgb(191, 134, 134),
                Color.rgb(179, 48, 80)
            )
        }

        val data = PieData(dataSet)
        data.apply {
            setValueFormatter(PercentFormatter(pieChart))
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }

        pieChart.data = data
        pieChart.invalidate()
    }

    private fun setupExportButton() {
        exportButton.setOnClickListener {
            // In a real app, this would generate and export a report
            // For now, we'll just show a message
            // Toast.makeText(context, "Report exported", Toast.LENGTH_SHORT).show()
        }
    }
} 