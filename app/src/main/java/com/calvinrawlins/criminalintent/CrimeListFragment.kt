package com.calvinrawlins.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calvinrawlins.criminalintent.databinding.FragmentCrimeListBinding
import com.calvinrawlins.criminalintent.databinding.ListItemCrimeBinding
import kotlinx.coroutines.launch
import java.text.DateFormat

class CrimeListFragment : Fragment() {

    private var _binding:
            FragmentCrimeListBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            CrimeListViewModel by viewModels()

    private val crimeAdapter =
        CrimeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentCrimeListBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        requireActivity().title =
            getString(R.string.app_name)

        binding.crimeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.crimeRecyclerView.adapter =
            crimeAdapter

        setupMenu()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner
                .repeatOnLifecycle(
                    Lifecycle.State.STARTED
                ) {
                    viewModel.crimes.collect { crimes ->
                        crimeAdapter.submitList(crimes)
                    }
                }
        }
    }

    private fun setupMenu() {

        val menuHost: MenuHost =
            requireActivity()

        menuHost.addMenuProvider(
            object : MenuProvider {

                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(
                        R.menu.fragment_crime_list,
                        menu
                    )
                }

                override fun onMenuItemSelected(
                    menuItem: MenuItem
                ): Boolean {

                    return when (menuItem.itemId) {

                        R.id.new_crime -> {

                            val crime = Crime()

                            viewModel.addCrime(crime) {
                                showCrime(crime)
                            }

                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun showCrime(
        crime: Crime
    ) {
        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                CrimeDetailFragment
                    .newInstance(crime.id)
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private inner class CrimeHolder(
        private val itemBinding:
        ListItemCrimeBinding
    ) : RecyclerView.ViewHolder(
        itemBinding.root
    ) {

        fun bind(
            crime: Crime
        ) {

            itemBinding.crimeTitle.text =
                if (crime.title.isBlank()) {
                    getString(
                        R.string.untitled_crime
                    )
                } else {
                    crime.title
                }

            itemBinding.crimeDate.text =
                DateFormat
                    .getDateInstance(
                        DateFormat.MEDIUM
                    )
                    .format(crime.date)

            itemBinding.solvedImage.visibility =
                if (crime.isSolved) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            itemBinding.root
                .setOnClickListener {
                    showCrime(crime)
                }
        }
    }

    private inner class CrimeAdapter :
        ListAdapter<Crime, CrimeHolder>(
            CrimeDiffCallback()
        ) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CrimeHolder {

            val itemBinding =
                ListItemCrimeBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                    parent,
                    false
                )

            return CrimeHolder(
                itemBinding
            )
        }

        override fun onBindViewHolder(
            holder: CrimeHolder,
            position: Int
        ) {
            holder.bind(
                getItem(position)
            )
        }
    }

    private class CrimeDiffCallback :
        DiffUtil.ItemCallback<Crime>() {

        override fun areItemsTheSame(
            oldItem: Crime,
            newItem: Crime
        ): Boolean {
            return oldItem.id ==
                    newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Crime,
            newItem: Crime
        ): Boolean {
            return oldItem ==
                    newItem
        }
    }
}