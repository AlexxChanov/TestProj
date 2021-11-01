package com.flametech.vaytoday.presentation.newReview

import com.flametech.vaytoday.data.pojo.CompanyReview

interface NewReviewView {

    interface View{
        fun onSuccess(message: String)
        fun onFailure(message: String)
        fun unAuth()
    }

    interface Presenter{
        fun setReview(review: CompanyReview)
        fun updateReview(review: CompanyReview, id: Int)
        fun deleteReview(id: Int)
    }

}