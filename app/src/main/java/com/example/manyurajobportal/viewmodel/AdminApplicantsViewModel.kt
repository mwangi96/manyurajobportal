package com.example.manyurajobportal.viewmodel.admin

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ApplicantsViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _applicants = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val applicants: StateFlow<List<Map<String, Any>>> = _applicants

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun loadApplicants(jobId: String) {
        _loading.value = true
        firestore.collection("applications")
            .document(jobId)
            .collection("applicants")
            .get()
            .addOnSuccessListener { snap ->
                _applicants.value = snap.documents.map { doc ->
                    val data = doc.data?.toMutableMap() ?: mutableMapOf()
                    data["id"] = doc.id
                    data
                }
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    fun updateStatus(jobId: String, applicationId: String, status: String) {
        firestore.collection("applications")
            .document(jobId)
            .collection("applicants")
            .document(applicationId)
            .update("status", status)
            .addOnSuccessListener {
                _applicants.value = _applicants.value.map { applicant ->
                    if (applicant["id"] == applicationId) {
                        applicant.toMutableMap().apply {
                            this["status"] = status
                        }
                    } else applicant
                }
            }
    }

}
