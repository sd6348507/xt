package com.roemsoft.equipment.bean

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json


/**
 * {
 *      Result:1,
 *      Msg:'获取成功。',
 *      DataSet:{}
 * }
 */
data class HttpResult<T>(
    @Json(name = "Result") val result: Int,
    @Json(name = "Msg") val msg: String,
    @Json(name = "DataSet") val dataSet: T? = null
)

/**
 * 数据集
 * {
 *      totalCount:8,
 *      fields:'CustomerNo,CustomerName',
 *      data:[]
 * }
 */
data class DataSet<T>(
    @Json(name = "totalCount") val totalCount: Int = 0,
    @Json(name = "fields") val fields: String = "",
    @Json(name = "data") val data: List<T>
)

/**
 *  {
 *         "Maker":"彭靖惠"
 *  }
 */
data class User(
    @Json(name = "Maker") val name: String = ""
)

data class Equipment(
    val code: String = "",
    val name: String = "",
    val spec: String = "",
    val model: String = "",
    val unit: String = ""
) {
    val specStr: String
        get() = "规格: $spec"

    val modelStr: String
        get() = "型号: $model"

    val unitStr: String
        get() = "单位: $unit"
}

data class RepairBean(
    val no: String,
    val name: String,
    val date: String,
    val reason: String,
    val firm: String,
    val state: String,
    val count: String,
    val price: String,
    val total: String
)

/**
 * 设备借出归还未审核记录
 * {
 *      "TkNo":"GJ24090001",
 *      "CompanyName":"皇亚鞋业加工厂",
 *      "ItemName":"条码打印机",
 *      "GDPerson":"陈艳红(13684856179)"
 * }
 */
data class LRListData(
    @Json(name = "TkNo") val tkNo: String = "",
    @Json(name = "CompanyName") val provider: String = "",
    @Json(name = "ItemName") val archiveName: String = "",
    @Json(name = "GDPerson") val person: String = ""
)

data class PrinterData(
    val no: String,
    val name: String,
    val department: String,
    val worker: String,
    val area: String,
    val spec: String,
    val date: String
)

/**
 * 固定资产编码
 * {
 *  "GDNum":"江西工厂BG240004"
 * }
 */
data class AssetsNo(
    @Json(name = "GDNum") var no: String = ""
)

/**
 * 归属公司
 * {
 *      "CostDepartNo":"G", "CostDepartName":"江西工厂"
 * }
 */
data class CostCompany(
    @Json(name = "CostDepartNo") var no: String = "",
    @Json(name = "CostDepartName") var name: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(no)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CostCompany> {
        override fun createFromParcel(parcel: Parcel): CostCompany {
            return CostCompany(parcel)
        }

        override fun newArray(size: Int): Array<CostCompany?> {
            return arrayOfNulls(size)
        }
    }

}

/**
 * {
 *      "CostDepartNo":"G02",
 *      "GDUseDepartName":"业务部"
 * }
 */
data class Department(
    @Json(name = "CostDepartNo") var no: String = "",
    @Json(name = "GDUseDepartName") var name: String = ""
)

/**
 * {
 *      "GDPerson":"施德芝(18279946984)"
 * }
 */
data class Person(
    @Json(name = "GDPerson") var name: String = ""
)


/**
 * {
 *      "ItemNo":"BG0000002",
 *      "ItemName":"650风扇",
 *      "XHModel":"",
 *      "SpecName":"",
 *      "TkDate":"2024/9/24 0:00:00",
 *      "CompanyName":""
 * }
 */
data class AssetsData(
    @Json(name = "ItemNo") var no: String = "",
    @Json(name = "ItemName") var name: String = "",
    @Json(name = "XHModel") var brand: String = "",
    @Json(name = "SpecName") var spec: String = "",
    @Json(name = "TkDate") var date: String = "",
    @Json(name = "CompanyName") var manufacturer: String = ""
)


/**
 * {
 *      "ClassName":"办公设备",
 *      "ItemNo":"BG0000082",
 *      "ItemName":"塑料凳子",
 *      "XHModel":"",
 *      "SpecName":"",
 *      "UnitName":"张"
 * }
 */
data class ArchiveData(
    @Json(name = "ClassName") var category: String = "", // 类别名称
    @Json(name = "ItemNo") val no: String = "",
    @Json(name = "ItemName") var name: String = "",
    @Json(name = "XHModel") var brand: String = "", // 品牌
    @Json(name = "SpecName") var spec: String = "",
    @Json(name = "UnitName") var unit: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(no)
        parcel.writeString(name)
        parcel.writeString(brand)
        parcel.writeString(spec)
        parcel.writeString(unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArchiveData> {
        override fun createFromParcel(parcel: Parcel): ArchiveData {
            return ArchiveData(parcel)
        }

        override fun newArray(size: Int): Array<ArchiveData?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "$category:$no:$name:$brand:$spec:$unit"
    }

}

/**
 * {
 *      "ClassName":"机器设备"
 * }
 */
data class Category(
    @Json(name = "ClassName") val name: String = ""
)

/**
 * {
 *      TkNo,
 *      GDPerson,
 *      ItemName,
 *      OldGDPerson
 * }
 */
data class TransferListData(
    @Json(name = "TkNo") val tkNo: String = "",
    @Json(name = "GDPerson") val newPerson: String = "",
    @Json(name = "OldGDPerson") val oldPerson: String = "",
    @Json(name = "ItemName") val archiveName: String = ""
)

/**
 * 设备和基础信息
 */
data class ArchiveBaseData(
    @Json(name = "CostDepartNo") val costComNo: String = "",
    @Json(name = "GDUseDepartName") val department: String = "",
    @Json(name = "GDPerson") val person: String = "",
    @Json(name = "CFPostion") val area: String = "",
    @Json(name = "ItemNo") val archiveNo: String = "",
    @Json(name = "ItemName") val name: String = "",
    @Json(name = "XHModel") val brand: String = "",
    @Json(name = "SpecName") val spec: String = "",
    @Json(name = "UnitName") val unit: String = ""
)



/**
 * 供应商 往来单位
 * {
 *      "ProviderId":"{02BFDFE5-2BE1-4F12-95FF-054D256EFE7B}",
 *      "ProviderName":"福建锋特"
 * }
 */
data class ProviderData(
    @Json(name = "ProviderId") var id: String = "",
    @Json(name = "ProviderName") var name: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProviderData> {
        override fun createFromParcel(parcel: Parcel): ProviderData {
            return ProviderData(parcel)
        }

        override fun newArray(size: Int): Array<ProviderData?> {
            return arrayOfNulls(size)
        }
    }

}


data class  UpdateBean(
    var code: Int = 0,
    var name: String = ""
)