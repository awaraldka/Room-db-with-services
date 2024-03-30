package com.locationTracker.modelClass

data class LocationData(
    val addressLine: String,
    val country: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val lat: String,
    val long: String,
    val deviceId: String,
    val speed: String
)