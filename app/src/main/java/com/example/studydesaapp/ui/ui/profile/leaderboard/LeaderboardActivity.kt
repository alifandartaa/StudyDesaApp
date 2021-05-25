package com.example.studydesaapp.ui.ui.profile.leaderboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studydesaapp.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {

    lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityLeaderboardBinding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(activityLeaderboardBinding.root)

        supportActionBar?.title = "Leaderboard Fakultas"

        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            LeaderboardViewModel::class.java)
        leaderboardAdapter = LeaderboardAdapter()
        setupListLeaderboard(viewModel, activityLeaderboardBinding)
    }

    private fun setupListLeaderboard(
        viewModel: LeaderboardViewModel,
        activityLeaderboardBinding: ActivityLeaderboardBinding
    ) {
        activityLeaderboardBinding.progressBar.visibility = View.VISIBLE

        viewModel.loadFacultyFromFirebase()

        viewModel.getListFacultyLeaderboard().observe(this, { rankItems ->
            if(rankItems != null){
                leaderboardAdapter.setListLeaderboard(rankItems)
                activityLeaderboardBinding.progressBar.visibility = View.GONE
                leaderboardAdapter.notifyDataSetChanged()
//                leaderboardAdapter.notifyItemChanged(0)
            }
        })

        with( activityLeaderboardBinding.rvLeaderboard){
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = leaderboardAdapter
        }
    }
}