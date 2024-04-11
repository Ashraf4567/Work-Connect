package com.example.workconnect.utils

sealed class TransactionResult {
    class Success() : TransactionResult()
    data class Failure(val exception: Exception?) : TransactionResult()
}
