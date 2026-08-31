package com.google.android.safetycore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.safetycore.R
import com.google.android.safetycore.databinding.ActivityGameListBinding
import com.google.android.safetycore.manager.GameManager
import com.google.android.safetycore.model.GameInfo

class GameListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameListBinding
    private lateinit var adapter: GameAdapter
    private val gameManager by lazy { GameManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.tvTitle.text = "🎮 Supported Games"
        binding.rvGames.layoutManager = LinearLayoutManager(this)
        adapter = GameAdapter(gameManager.getAllGames())
        binding.rvGames.adapter = adapter
        binding.btnBack.setOnClickListener { finish() }
    }

    inner class GameAdapter(private val games: List<GameInfo>) :
        RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

        inner class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tv_game_name)
            val packageName: TextView = view.findViewById(R.id.tv_package)
            val maxFps: TextView = view.findViewById(R.id.tv_max_fps)
            val switch: Switch = view.findViewById(R.id.switch_game)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_game, parent, false)
            return GameViewHolder(view)
        }

        override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
            val game = games[position]
            holder.name.text = game.name
            holder.packageName.text = game.packageName
            holder.maxFps.text = "Max ${game.maxFps} FPS"
            holder.switch.isChecked = game.enabled
            holder.switch.setOnCheckedChangeListener { _, isChecked ->
                gameManager.setGameEnabled(game.packageName, isChecked)
            }
        }

        override fun getItemCount(): Int = games.size
    }
}
