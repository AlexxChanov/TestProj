package com.flametech.vaytoday.presentation.newReview

import com.flametech.vaytoday.data.pojo.CompanyReview
import com.flametech.vaytoday.domain.network.api.MainRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.lang.NullPointerException

private const val TAG = "NewReviewPresenter"

class NewReviewPresenter(val repository: MainRepository, val view: NewReviewView.View) :
    NewReviewView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun setReview(review: CompanyReview) {
        compositeDisposable.add(
            repository.setReview(review)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onSuccess("Ваш отзыв будет добавлен в течение 10 минут!")
                }, {
                    handleError(it)
                })

        )
    }

    override fun updateReview(review: CompanyReview, reviewsId: Int) {
        compositeDisposable.add(
            repository.updateReview(review, reviewsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onSuccess("Ваш отзыв успешно обновлен!")
                }, {
                    handleError(it)
                })
        )
    }

    override fun deleteReview(id: Int) {
        compositeDisposable.add(
            repository.deleteReview(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.onSuccess("Ваш отзыв успешно удален!")
                }, { handleDeleteReviewError(it) })
        )
    }

    private fun handleError(exception: Throwable){
        if (exception is HttpException){
            when(exception.code()) {
                401 -> view.unAuth()
            }
        } else view.onFailure(exception.message.toString())
    }

    private fun handleDeleteReviewError(exception: Throwable) {
        if (exception is NullPointerException ) {
            view.onSuccess("Ваш отзыв успешно удален!")
            }
         else {
             if (exception is HttpException){
                 when(exception.code()) {
                     401 -> view.unAuth()
                 }
             } else view.onFailure(exception.message.toString())
        }
    }

}