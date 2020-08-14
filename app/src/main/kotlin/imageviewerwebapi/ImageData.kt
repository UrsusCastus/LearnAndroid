package imageviewerwebapi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageData(val url: String, val title: String) : Parcelable
