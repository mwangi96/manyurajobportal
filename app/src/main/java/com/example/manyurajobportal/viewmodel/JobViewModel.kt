package com.example.manyurajobportal.viewmodel

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.manyurajobportal.data.model.Job
import com.example.manyurajobportal.data.remote.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import java.util.*

class JobViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    // ----------------------
    // Job Posting with Firestore & Storage
    // ----------------------
    fun postJob(
        job: Job,
        imageUri: Uri?,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            if (imageUri == null) {
                onResult(false, "Please select an image")
                return@launch
            }

            try {
                val jobId = UUID.randomUUID().toString()
                val storageRef = FirebaseStorage.getInstance().reference.child("jobs/$jobId.jpg")
                storageRef.putFile(imageUri).await()  // Upload image

                val imageUrl = storageRef.downloadUrl.await().toString()
                val jobData = job.copy(jobId = jobId, imageUrl = imageUrl, timestamp = System.currentTimeMillis())

                FirebaseFirestore.getInstance()
                    .collection("jobs")
                    .document(jobId)
                    .set(jobData)
                    .addOnSuccessListener {
                        onResult(true, "Job posted successfully!")
                    }
                    .addOnFailureListener { e ->
                        onResult(false, "Failed to post job: ${e.message}")
                    }

            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            }
        }
    }

    // ----------------------
    // Countries & Cities (API)
    // ----------------------
    val countryList = mutableStateListOf<String>()
    val cityList = mutableStateListOf<String>()
    val selectedCountry = mutableStateOf("")
    val selectedCity = mutableStateOf("")

    fun fetchCountries() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.countriesApi.getCountries()
                countryList.clear()
                countryList.addAll(response.map { it.name.common }.sorted())
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(app, "Failed to fetch countries", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchCities(country: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.citiesApi.getCities(country)
                cityList.clear()
                cityList.addAll(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(app, "Failed to fetch cities", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
