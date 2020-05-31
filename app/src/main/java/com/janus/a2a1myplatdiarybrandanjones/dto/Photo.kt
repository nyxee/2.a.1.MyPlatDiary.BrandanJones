package com.janus.a2a1myplatdiarybrandanjones.dto

import android.accounts.AuthenticatorDescription
import java.util.*

data class Photo(var localUri: String="", var remoteUri: String="", var description: String="", var dateTaken: Date= Date(), var id: String="" ) {
}