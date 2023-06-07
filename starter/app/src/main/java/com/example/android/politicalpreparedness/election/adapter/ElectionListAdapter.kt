package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ViewholderElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
    ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent, clickListener)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ElectionViewHolder private constructor(
        val binding: ViewholderElectionBinding,
        private val clickListener: ElectionListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Election) {
            binding.election = item
            binding.clickListener = ElectionListener { election -> clickListener.onClick(election) }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, clickListener: ElectionListener): ElectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ViewholderElectionBinding.inflate(layoutInflater, parent, false)
                return ElectionViewHolder(binding, clickListener)
            }
        }
    }
}


class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}

class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}