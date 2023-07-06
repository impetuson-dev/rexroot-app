package com.impetuson.rexroot.model.profile

data class CandidateModelClass(
    var candidateFullName: String = "",
    var candidateEmail: String = "",
    var candidateMobileNumber: String = "",
    var candidateGender: String = "",
    var candidateCurrLocation: String = "",
    var candidateDesLocation: String = "",
    var candidateWillToRelocate: Int = 0,
    var candidateCoreSkills: String = "",
    var candidateSuppSkills: String = "",
)
