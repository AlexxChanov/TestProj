package com.flametech.vaytoday.presentation.newReview

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.flametech.vaytoday.MainActivity
import com.flametech.vaytoday.R
import com.flametech.vaytoday.data.pojo.Business
import com.flametech.vaytoday.data.pojo.CompanyReview
import com.flametech.vaytoday.data.pojo.UserReview
import com.flametech.vaytoday.databinding.FragmentNewReviewBinding
import com.flametech.vaytoday.domain.network.api.MainRepository
import com.flametech.vaytoday.presentation.reviews.NeedAuthDialog
import com.flametech.vaytoday.presentation.reviews.UpdateAddReviewFragmentCallback
import com.flametech.vaytoday.utils.Consts
import com.flametech.vaytoday.viewBinding

private const val TAG = "NewReviewFragment"

class NewReviewFragment : Fragment(R.layout.fragment_new_review), NewReviewView.View {

    private val binding by viewBinding(FragmentNewReviewBinding::bind)

    private lateinit var currentCompany: Business
    private var currentReview: UserReview? = null
    lateinit var presenter: NewReviewPresenter
    var rating: Int? = null
    lateinit var updateCallback: UpdateAddReviewFragmentCallback

    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            updateCallback = context as MainActivity
        } catch (t: Throwable){
            Log.d(TAG,t.message.toString())
        }
    }

    private fun init(){
        arguments?.getParcelable<Business>(Consts.CURRENT_COMPANY)?.let {
            currentCompany = it
        }
        arguments?.getParcelable<UserReview>(Consts.CURRENT_REVIEW)?.let {
            currentReview = it
        }
        presenter = NewReviewPresenter(MainRepository(), this)
        initClickListeners()
    }

    private fun initClickListeners(){
        currentReview?.let{
            binding.newReviewChangeBtn.visibility = View.VISIBLE
            binding.newReviewSendBtn.visibility = View.GONE
            binding.newReviewText.setText(it.text)
            binding.newReviewRating.rating = it.rating.toFloat()
            rating = it.rating
            binding.newReviewToolbarTitle.text = resources.getString(R.string.edit_review)
            binding.newReviewDeleteBtn.visibility = View.VISIBLE
        }
        binding.newReviewDeleteBtn.setOnClickListener { presenter.deleteReview(currentReview!!.id) }
        binding.newReviewBackBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.newReviewChangeBtn.setOnClickListener {
            if (checkFields()){
                presenter.updateReview(CompanyReview(currentCompany.id, binding.newReviewText.text.toString(), rating!!), currentReview!!.id)
            } else {
                Toast.makeText(requireContext(), R.string.fill_out_review_and_rate, Toast.LENGTH_SHORT).show()
            }
        }
        binding.newReviewRating.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, p1, _ -> rating = p1.toInt() }
        binding.newReviewSendBtn.setOnClickListener {
            if (checkFields()){
                presenter.setReview(CompanyReview(currentCompany.id, binding.newReviewText.text.toString(), rating!!))
            }
        }
    }

    private fun checkFields() : Boolean {
        if (binding.newReviewText.text.isNullOrEmpty()|| binding.newReviewText.text.isBlank()){
            Toast.makeText(requireContext(), R.string.fill_text_of_the_review, Toast.LENGTH_SHORT).show()
            return false
        }

        if (rating == null){
            Toast.makeText(requireContext(), R.string.rate_it, Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.newReviewText.text.length < 5) {
            Toast.makeText(requireContext(), R.string.feedback_must_be_more_than_5_characters, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onSuccess(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_LONG).show()
        updateCallback.update()
        requireActivity().onBackPressed()
    }

    override fun onFailure(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun unAuth() {
        NeedAuthDialog().show(requireActivity().supportFragmentManager,null)
    }
}