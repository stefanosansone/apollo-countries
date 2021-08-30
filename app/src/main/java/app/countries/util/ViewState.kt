package app.countries.util

//Classe per leggere lo stato della chiamata API ed utilizzarlo per gestire la mia UI
sealed class ViewState<T>(
    val value: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ViewState<T>(data)
    class Error<T>(message: String?, data: T? = null) : ViewState<T>(data, message)
    class Loading<T> : ViewState<T>()
}