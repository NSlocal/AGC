package com.google.android.safetycore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.safetycore.R
import com.google.android.safetycore.content.ScanHistoryItem
import com.google.android.safetycore.content.ScanHistoryManager
import com.google.android.safetycore.content.ScanStatus
import com.google.android.safetycore.databinding.ActivityHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val dateFormat = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val history = ScanHistoryManager.getHistory(this)
        binding.recyclerView.adapter = HistoryAdapter(history)

        binding.btnClear.setOnClickListener {
            ScanHistoryManager.clearHistory(this)
            finish()
        }
    }

    inner class HistoryAdapter(private val items: List<ScanHistoryItem>) :
        RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTime: TextView = view.findViewById(R.id.tv_time)
            val tvStatus: TextView = view.findViewById(R.id.tv_status)
            val tvReason: TextView = view.findViewById(R.id.tv_reason)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_scan_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvTime.text = dateFormat.format(Date(item.timestamp))
            holder.tvStatus.text = when (item.status) {
                ScanStatus.ALLOWED -> "✅ Diizinkan"
                ScanStatus.WARNING -> "⚠️ Peringatan"
                ScanStatus.BLOCKED -> "🚫 Diblokir"
                ScanStatus.UNCLASSIFIED -> "❓ Tidak Diklasifikasikan"
            }
            holder.tvReason.text = item.reason ?: "-"
        }

        override fun getItemCount(): Int = items.size
    }
}
