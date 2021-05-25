package com.example.studydesaapp.ui.ui.profile.leaderboard

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.studydesaapp.R
import com.example.studydesaapp.databinding.ItemRankBinding
import com.example.studydesaapp.ui.entity.Faculty

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    private var listLeaderboard = ArrayList<Faculty>()

    class LeaderboardViewHolder(private val binding: ItemRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val number = binding.tvItemRank
        fun bind(rank: Faculty) {
            with(binding) {
                tvItemFacultyName.text = rank.name
                tvItemFacultyScore.text = rank.score
            }
        }
    }

    fun setListLeaderboard(leaderboard: List<Faculty>?) {
        if (leaderboard == null) return
        this.listLeaderboard.clear()
        this.listLeaderboard.addAll(leaderboard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val itemRankBinding =
            ItemRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderboardViewHolder(itemRankBinding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val rank = listLeaderboard[position]
        holder.number.text = (position + 1).toString()
        holder.bind(rank)

    }

    override fun getItemCount(): Int {
        return listLeaderboard.size
    }


}