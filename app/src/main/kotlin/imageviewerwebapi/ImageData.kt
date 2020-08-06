package imageviewerwebapi

import android.os.Parcel
import android.os.Parcelable

data class ImageData(val url: String?, val title: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    //метод упаковывает объект для передачи
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    //статическое поле - генерирует объект класса-передатчика
    companion object CREATOR : Parcelable.Creator<ImageData> {
        override fun createFromParcel(parcel: Parcel): ImageData {
            return ImageData(parcel)
        }

        override fun newArray(size: Int): Array<ImageData?> {
            return arrayOfNulls(size)
        }
    }
}
