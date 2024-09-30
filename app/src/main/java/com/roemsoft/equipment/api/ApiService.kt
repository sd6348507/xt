package com.roemsoft.equipment.api

import com.roemsoft.equipment.bean.*
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    // 添加 suspend 可以不使用 Call
    // Retrofit2.6.0 支持接口suspend函数配合协程使用
    // 原理是解析判断是否为挂起函数
    // 如果是挂起函数 则使用协程CallAdapted 源码中使用 suspendCancellableCoroutine<> {  } 方法 当协程取消，Call也会取消请求
    // 因此，当接口为挂起函数时，协程被取消，请求也会取消
    // suspendCancellableCoroutine<> {  }  不是创建新的携程而是主动挂起当前携程 执行方块体内容 最后方块体调用resume恢复携程
    // 链接 https://www.jianshu.com/p/c9f123c21d82

    // 登录
    // ReqType=APPUserLogin&ReqStr=UserNo=PX007;UserPwd=123456
    @FormUrlEncoded
    @POST(HttpConfig.URL_PATH)
    suspend fun login(
        @Field(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_LOGIN,
        @Field(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<User>>


    // 查询设备档案列表
    // ReqType=GDItemSearch&ReqStr=ClassName=办公设备;ItemName=塑料凳子;XHModel=;SpecName=
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchArchiveList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_ARCHIVE_LIST,
        @Query(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<ArchiveData>>

    // 获取设备类别名称列表
    // reqType=GDItemClass
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchCategoryList(@Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_CATEGORY): HttpResult<DataSet<Category>>

    // 设备档案信息 提交
    // ReqType=PostGDItem&ReqStr=ClassName=办公设备;ItemNo=123456;ItemName=凳子;XHModel=222;SpecName=333;UnitName=个;UserName=PX007
    @FormUrlEncoded
    @POST(HttpConfig.URL_PATH)
    suspend fun submitArchive(
        @Field(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_ARCHIVE_SUBMIT,
        @Field(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<Any?>


    // 获取归属公司列表
    // ReqType=GetDepartlist
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchCostCompanyList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_COST_COMPANY
    ): HttpResult<DataSet<CostCompany>>

    // 获取责任部门列表
    // ReqType=GetDepart&ReqStr=CostDepartNo=
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchDepartmentList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_DEPARTMENT,
        @Query(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<Department>>

    // 获取责任人列表
    // ReqType=GetPerson
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchPersonList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_PERSON
    ): HttpResult<DataSet<Person>>


    // 盘点入库 提交
    // ReqType=CHPDSMIn&ReqStr=CostDepartNo=;GDNum=;GDUseDepartName=;GDPerson=;CFPostion=;ItemNo=;Qty=;LYType=;Maker=
    @FormUrlEncoded
    @POST(HttpConfig.URL_PATH)
    suspend fun submitCheckIn(
        @Field(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_CHECKIN_SUBMIT,
        @Field(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<AssetsNo>>


    // 资产查询 根据固定资产编码
    // ReqType=GDNumSearch&ReqStr=GDNum=
    @GET(HttpConfig.URL_PATH)
    suspend fun assetsSearch(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_ASSETS_SEARCH,
        @Query(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<AssetsData>>


    // 设备和基础信息查询 根据固定资产编码
    // ReqType=GDSearch&ReqStr=GDNum=
    @GET(HttpConfig.URL_PATH)
    suspend fun archiveBaseInfoSearch(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_ARCHIVE_BASE_SEARCH,
        @Query(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<ArchiveBaseData>>

    // 设备转移未审核列表
    // ReqType=GDZY&ReqStr=Maker=罗江萍(18879971948)
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchTransferList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_TRANSFER_LIST,
        @Query(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<TransferListData>>

    // 设备转移 提交
    // ReqType=GDZYIn&ReqStr=CostDepartNo=P;GDNum=PBG240520;OldGDUseDepartName=版师组;OldGDPerson=陈艳红(13684856179);OldCFPostion=财务室;
    // GDUseDepartName=成型A线;GDPerson=边二飞(18059509977);CFPostion=成型办公室;ItemNo=BG0000137;Qty=1;Maker=管理员
    @FormUrlEncoded
    @POST(HttpConfig.URL_PATH)
    suspend fun transferSubmit(
        @Field(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_TRANSFER_SUBMIT,
        @Field(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<Any?>

    // 设备借出未审核列表
    // ReqType=GDJC
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchLendList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_LEND_LIST
    ): HttpResult<DataSet<LRListData>>

    // 设备借出 提交
    // ReqType=GDJCOut&ReqStr=CostDepartNo=P;CompanyName=皇亚鞋业加工厂;GDNum=PBG240520;GDUseDepartName=版师组;
    // GDPerson=陈艳红(13684856179);CFPostion=财务室;ItemNo=BG0000137;Qty=1;Maker=管理员
    @FormUrlEncoded
    @POST(HttpConfig.URL_PATH)
    suspend fun lendSubmit(
        @Field(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_LEND_SUBMIT,
        @Field(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<Any?>

    // 设备归还未审核列表
    // ReqType=GDGH
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchReturnList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_RETURN_LIST
    ): HttpResult<DataSet<LRListData>>

    // 设备归还 提交
    // ReqType=GDGHIn&ReqStr=CostDepartNo=P;CompanyName=皇亚鞋业加工厂;GDNum=PBG240520;GDUseDepartName=版师组;
    // GDPerson=陈艳红(13684856179);CFPostion=财务室;ItemNo=BG0000137;Qty=1;Maker=管理员
    @FormUrlEncoded
    @POST(HttpConfig.URL_PATH)
    suspend fun returnSubmit(
        @Field(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_RETURN_SUBMIT,
        @Field(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<Any?>

    // 获取往来单位供应商列表
    // ReqType=GetProvider
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchProviderList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_PROVIDER_SEARCH
    ): HttpResult<DataSet<ProviderData>>

/*

    // 根据指令单号获取部件列表
    // ReqType=InstructionMtlDetail&ReqStr=ino=21142-2
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchPartNameList(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_PART_NAME,
        @Query(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<PartBean>>

    // 备料日报 查询
    // ReqType=InstructionMtlDetailMX&ReqStr=INo=21142-2;PartName=%E8%88%8C%E9%9D%A2
    @GET(HttpConfig.URL_PATH)
    suspend fun searchBlrb(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_BLRB_SEARCH,
        @Query(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<TaskDaily>>




    // 获取组别
    // ReqType=SiteApp
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchSite(@Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_SITE): HttpResult<DataSet<Site>>

    // 中转发料 查询
    // ReqType=EProStgTkMCBL&ReqStr=INo=19200A-1%E8%A1%A5%E5%8D%95
    @GET(HttpConfig.URL_PATH)
    suspend fun fetchConduit(
        @Query(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_CONDUIT,
        @Query(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<DataSet<ConduitBean>>

    // 中转发料 提交
    // ReqType=EProStgTkMCDetailapp&ReqStr=INo=19200A-1;SizeNo=36;QTY=20;SiteName
    @FormUrlEncoded
    @POST(HttpConfig.URL_PATH)
    suspend fun submitConduit(
        @Field(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_CONDUIT_SUBMIT,
        @Field(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<Unit>




    // 备料部件设定 提交
    // ReqType=InstructionMtlDetailSD&ReqStr=INo=21015;ISBL=1;PartName=舌里
    @FormUrlEncoded
    @POST(HttpConfig.URL_PATH)
    suspend fun bjSettingSubmit(
        @Field(HttpConfig.REQ_TYPE) reqType: String = HttpConfig.REQ_TYPE_BLBJSD_SUBMIT,
        @Field(HttpConfig.REQ_STR) reqStr: String
    ): HttpResult<Unit>
*/


    /**-------------- 更新 --------------*/

    /**
     * 获取APP版本号
     * version_name,version_code
     */
    @GET("/ajax/${com.roemsoft.common.api.HttpConfig.VERSION_URL}")
    suspend fun getVersion(): UpdateBean

    /**
     * 下载
     */
    @Streaming
    @GET("/ajax/${com.roemsoft.common.api.HttpConfig.DOWNLOAD_URL}")
    suspend fun downloadApk(): ResponseBody
}