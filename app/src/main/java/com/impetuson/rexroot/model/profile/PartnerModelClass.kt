package com.impetuson.rexroot.model.profile

import com.google.errorprone.annotations.Keep

data class PartnerModelClass (
    var partnerFullName: String = "",
    var partnerEmail: String = "",
    var partnerMobileNumber: String = "",
    var partnerAddress: String = "",
    var partnerIndDomain: String = "",
    var partnerSector: String = ""
    )