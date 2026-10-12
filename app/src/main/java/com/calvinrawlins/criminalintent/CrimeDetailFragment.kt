package com.calvinrawlins.criminalintent

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.util.Date
import java.util.UUID

class CrimeDetailFragment : Fragment() {

    private lateinit var crimeDetailScrollView: ScrollView
    private lateinit var crimeTitleEditText: EditText
    private lateinit var crimeDateButton: Button
    private lateinit var crimeSolvedCheckBox: CheckBox
    private lateinit var crimePhoto: ImageView
    private lateinit var takePhotoButton: Button
    private lateinit var crimeReportButton: Button

    private val crimeId: UUID by lazy {
        val idString = requireNotNull(
            requireArguments().getString(ARG_CRIME_ID)
        )

        UUID.fromString(idString)
    }

    private val viewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(
            requireActivity().application,
            crimeId
        )
    }

    private lateinit var currentCrime: Crime

    private var photoFile: File? = null

    private var bindingCrime = false

    private val takePhoto =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {
                updatePhotoView()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_crime_detail,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        crimeDetailScrollView =
            view.findViewById(
                R.id.crimeDetailScrollView
            )

        crimeTitleEditText =
            view.findViewById(
                R.id.crimeTitleEditText
            )

        crimeDateButton =
            view.findViewById(
                R.id.crimeDateButton
            )

        crimeSolvedCheckBox =
            view.findViewById(
                R.id.crimeSolvedCheckBox
            )

        crimePhoto =
            view.findViewById(
                R.id.crimePhoto
            )

        takePhotoButton =
            view.findViewById(
                R.id.takePhotoButton
            )

        crimeReportButton =
            view.findViewById(
                R.id.crimeReportButton
            )

        requireActivity().title =
            getString(
                R.string.crime_details
            )

        setupMenu()
        setupDatePicker()
        setupListeners()

        crimeDetailScrollView.post {
            crimeDetailScrollView.scrollTo(
                0,
                0
            )
        }

        viewLifecycleOwner
            .lifecycleScope
            .launch {

                viewLifecycleOwner
                    .repeatOnLifecycle(
                        Lifecycle.State.STARTED
                    ) {

                        viewModel.crime.collect { crime ->

                            if (crime != null) {

                                currentCrime =
                                    crime

                                photoFile =
                                    File(
                                        requireContext()
                                            .filesDir,
                                        crime.photoFileName
                                    )

                                bindCrime(
                                    crime
                                )
                            }
                        }
                    }
            }
    }

    private fun setupListeners() {

        crimeTitleEditText
            .doOnTextChanged {
                    text,
                    _,
                    _,
                    _ ->

                if (
                    !bindingCrime &&
                    ::currentCrime.isInitialized
                ) {

                    currentCrime =
                        currentCrime.copy(
                            title =
                                text.toString()
                        )

                    viewModel.updateCrime(
                        currentCrime
                    )
                }
            }

        crimeSolvedCheckBox
            .setOnCheckedChangeListener {
                    _,
                    isChecked ->

                if (
                    !bindingCrime &&
                    ::currentCrime.isInitialized
                ) {

                    currentCrime =
                        currentCrime.copy(
                            isSolved =
                                isChecked
                        )

                    viewModel.updateCrime(
                        currentCrime
                    )
                }
            }

        crimeDateButton
            .setOnClickListener {

                if (
                    ::currentCrime.isInitialized
                ) {

                    DatePickerFragment
                        .newInstance(
                            currentCrime.date
                        )
                        .show(
                            parentFragmentManager,
                            "DatePicker"
                        )
                }
            }

        takePhotoButton
            .setOnClickListener {

                takeCrimePhoto()
            }

        crimeReportButton
            .setOnClickListener {

                sendCrimeReport()
            }
    }

    private fun setupDatePicker() {

        parentFragmentManager
            .setFragmentResultListener(
                DatePickerFragment.REQUEST_KEY,
                viewLifecycleOwner
            ) {
                    _,
                    bundle ->

                if (
                    !::currentCrime.isInitialized
                ) {
                    return@setFragmentResultListener
                }

                val selectedDate =
                    Date(
                        bundle.getLong(
                            DatePickerFragment
                                .BUNDLE_KEY_DATE
                        )
                    )

                currentCrime =
                    currentCrime.copy(
                        date =
                            selectedDate
                    )

                viewModel.updateCrime(
                    currentCrime
                )

                updateDateButton()
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
                        R.menu.fragment_crime_detail,
                        menu
                    )
                }

                override fun onMenuItemSelected(
                    menuItem: MenuItem
                ): Boolean {

                    return when (
                        menuItem.itemId
                    ) {

                        R.id.delete_crime -> {

                            if (
                                ::currentCrime
                                    .isInitialized
                            ) {

                                viewModel.deleteCrime(
                                    currentCrime
                                ) {

                                    parentFragmentManager
                                        .popBackStack()
                                }
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

    private fun bindCrime(
        crime: Crime
    ) {

        bindingCrime = true

        if (
            crimeTitleEditText
                .text
                .toString() != crime.title
        ) {

            crimeTitleEditText
                .setText(
                    crime.title
                )
        }

        crimeSolvedCheckBox
            .isChecked =
            crime.isSolved

        updateDateButton()
        updatePhotoView()

        bindingCrime = false
    }

    private fun updateDateButton() {

        if (
            !::currentCrime.isInitialized
        ) {
            return
        }

        crimeDateButton.text =
            DateFormat
                .getDateInstance(
                    DateFormat.FULL
                )
                .format(
                    currentCrime.date
                )
    }

    private fun takeCrimePhoto() {

        val file =
            photoFile
                ?: return

        val photoUri =
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )

        try {

            takePhoto.launch(
                photoUri
            )

        } catch (
            _: ActivityNotFoundException
        ) {

            Toast.makeText(
                requireContext(),
                R.string.no_camera_app,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updatePhotoView() {

        val file =
            photoFile

        if (
            file != null &&
            file.exists()
        ) {

            val bitmap =
                BitmapFactory
                    .decodeFile(
                        file.absolutePath
                    )

            crimePhoto
                .setImageBitmap(
                    bitmap
                )

        } else {

            crimePhoto
                .setImageDrawable(
                    null
                )
        }
    }

    private fun sendCrimeReport() {

        if (
            !::currentCrime.isInitialized
        ) {
            return
        }

        val solvedStatus =
            if (
                currentCrime.isSolved
            ) {

                getString(
                    R.string.solved
                )

            } else {

                getString(
                    R.string.unsolved
                )
            }

        val dateText =
            DateFormat
                .getDateInstance(
                    DateFormat.FULL
                )
                .format(
                    currentCrime.date
                )

        val report =
            getString(
                R.string.crime_report_text,
                currentCrime.title,
                dateText,
                solvedStatus
            )

        val sendIntent =
            Intent(
                Intent.ACTION_SEND
            ).apply {

                type =
                    "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    report
                )

                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(
                        R.string.crime_report_subject
                    )
                )
            }

        try {

            startActivity(
                Intent.createChooser(
                    sendIntent,
                    getString(
                        R.string.send_report
                    )
                )
            )

        } catch (
            _: ActivityNotFoundException
        ) {

            Toast.makeText(
                requireContext(),
                R.string.no_share_app,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {

        private const val ARG_CRIME_ID =
            "crimeId"

        fun newInstance(
            crimeId: UUID
        ): CrimeDetailFragment {

            return CrimeDetailFragment()
                .apply {

                    arguments =
                        Bundle().apply {

                            putString(
                                ARG_CRIME_ID,
                                crimeId
                                    .toString()
                            )
                        }
                }
        }
    }
}