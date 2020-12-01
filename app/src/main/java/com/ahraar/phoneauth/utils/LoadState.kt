package com.ahraar.phoneauth.utils

class ErrorMessage {
    companion object {
        var errorMessage: String? = "Something went wrong"
    }
}

enum class LoadState {
    SUCCESS, FAILURE, LOADING
}
