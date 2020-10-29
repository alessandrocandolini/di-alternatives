package com.alessandrocandolini.business
//
//import java.util.*
//
//interface NowProvider {
//    suspend fun now(): Date
//}
//
//// leaking the underlying source (you are not really hiding the source)
//// this bubbles up to the level of the usecase/business logic
//
//class AddTodoUsecase(val nowProvider: NowProvider) {
//
//    suspend fun save(title: String, date: Date?): Boolean {
//        return if (title == "") {
//            false
//        } else if (date != null && date < nowProvider.now()) {
//            false
//        } else {
//            true
//        }
//    }
//
//}
//
//class AddTodoUsecase2(val nowProvider: NowProvider) {
//
//    suspend fun save(title: String, date: Date?): Result {
//        val now = nowProvider.now()
//        return save2(title, date, now)
//    }
//
//    companion object {
//        fun save2(title: String, date: Date?, now: Date): Result =
//            if (title == "") {
//                Result.NO_TITLE
//            } else if (date != null && date < now) {
//                Result.INVALID_DATE
//            } else {
//                Result.SUCCESS
//            }
//    }
//}
//
//// pure
//enum class Result {
//    NO_TITLE, INVALID_DATE, SUCCESS
//}
//
//object TodoDateValidation { // namespace
//
//    fun save(title: String, date: Date?, now: Date): Result =
//        if (title == "") {
//            Result.NO_TITLE
//        } else if (date != null && date < now) {
//            Result.INVALID_DATE
//        } else {
//            Result.SUCCESS
//        }
//
//}
//
//val now : Observable<Date> = TODO() // source
//
//interface NowApi {
//    @Get
//    fun now() : Single<Date> // source
//}
//
//interface View {
//    fun title() : Observable<String> // source (text field)
//    fun date() : Observable<Date> // date calendar picker
//}
//
//class Presenter (
//    val view : View,
//    val api : NowApi,
//) {
//
//    fun onStart() {
//        Observable.combineLatest(view.title(), view.date())
//            .flatMap(...) // to retrieve the now
//        .map { save(it.first, it.second, it.third) }
//
//        }
//    }
//
//
//}
//
//
//
//
//
