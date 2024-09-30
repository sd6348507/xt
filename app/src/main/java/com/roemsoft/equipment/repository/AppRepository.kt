package com.roemsoft.equipment.repository

import com.roemsoft.common.bean.RespResult
import com.roemsoft.equipment.api.ApiService
import com.roemsoft.equipment.api.HttpConfig
import com.roemsoft.equipment.bean.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.lang.Exception


class AppRepository(private val api: ApiService) {

    private val io = Dispatchers.IO

    suspend fun login(username: String, password: String): Flow<RespResult<User>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_USER_NO}=$username;${HttpConfig.REQ_STR_USER_PW}=$password"
                val resp = api.login(reqStr = reqStr)
                if (resp.dataSet != null) {
                    if (resp.dataSet.data.isEmpty()) {
                        emit(RespResult.Failure("登录失败，账号或密码错误"))
                    } else {
                        emit(RespResult.Success(resp.dataSet.data[0]))
                    }
                } else {
                    emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(io)
    }



    /**
     * 查询设备档案列表
     * ReqStr=ClassName=办公设备;ItemName=塑料凳子;XHModel=;SpecName=
     */
    suspend fun fetchArchiveList(category: String = "", name: String = "", brand: String, spec: String = ""): Flow<RespResult<DataSet<ArchiveData>>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_ARCHIVE_CATEGORY}=$category;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_NAME}=$name;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_BRAND}=$brand;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_SPEC}=$spec"
                val resp = api.fetchArchiveList(reqStr = reqStr)
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 获取设备类别名称列表
     */
    suspend fun fetchCategoryList(): Flow<RespResult<DataSet<Category>>> {
        return flow {
            try {
                val resp = api.fetchCategoryList()
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 设备档案信息 提交
     * ReqStr=ClassName=办公设备;ItemNo=123456;ItemName=凳子;XHModel=222;SpecName=333;UnitName=个;UserName=PX007
     */
    suspend fun submitArchive(category: String, no: String, name: String, brand: String, spec: String, unit: String, user: String): Flow<RespResult<Any?>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_ARCHIVE_CATEGORY}=$category;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_NO}=$no;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_NAME}=$name;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_BRAND}=$brand;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_SPEC}=$spec;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_UNIT}=$unit;" +
                        "${HttpConfig.REQ_STR_USER}=$user"
                val resp = api.submitArchive(reqStr = reqStr)
                if (resp.result == 1) {
                    emit(RespResult.Success(Unit))
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }


    /**
     * 获取归属公司列表
     */
    suspend fun fetchCostCompanyList(): Flow<RespResult<DataSet<CostCompany>>> {
        return flow {
            try {
                val resp = api.fetchCostCompanyList()
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 获取部门列表列表
     * ReqStr=CostDepartNo=
     */
    suspend fun fetchDepartmentList(companyNo: String): Flow<RespResult<DataSet<Department>>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_COST_COM_NO}=$companyNo"
                val resp = api.fetchDepartmentList(reqStr = reqStr)
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 获取归属公司列表
     */
    suspend fun fetchPersonList(): Flow<RespResult<DataSet<Person>>> {
        return flow {
            try {
                val resp = api.fetchPersonList()
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }


    /**
     * 盘点入库 提交
     * ReqStr=CostDepartNo=;GDNum=;GDUseDepartName=;GDPerson=;CFPostion=;ItemNo=;Qty=;LYType=;Maker=
     */
    suspend fun submitCheckIn(costComNo: String, assetsNo: String, department: String, person: String, area: String,
                              archiveNo: String, qty: String, source: String, user: String): Flow<RespResult<AssetsNo>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_COST_COM_NO}=$costComNo;" +
                        "${HttpConfig.REQ_STR_ASSETS_NO}=$assetsNo;" +
                        "${HttpConfig.REQ_STR_DEPARTMENT}=$department;" +
                        "${HttpConfig.REQ_STR_PERSON}=$person;" +
                        "${HttpConfig.REQ_STR_AREA}=$area;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_NO}=$archiveNo;" +
                        "${HttpConfig.REQ_STR_QTY}=$qty;" +
                        "${HttpConfig.REQ_STR_SOURCE}=$source;" +
                        "${HttpConfig.REQ_STR_MAKER}=$user"
                val resp = api.submitCheckIn(reqStr = reqStr)
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        if (resp.dataSet.data.isEmpty()) {
                            emit(RespResult.Failure("查询不到数据"))
                        } else {
                            emit(RespResult.Success(resp.dataSet.data[0]))
                        }
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }


    /**
     * 资产查询根据固定资产编码
     * ReqStr=GDNum=
     */
    suspend fun assetsSearch(assetsNo: String): Flow<RespResult<DataSet<AssetsData>>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_ASSETS_NO}=$assetsNo"
                val resp = api.assetsSearch(reqStr = reqStr)
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }


    /**
     * 设备和基础信息查询 根据固定资产编码
     * ReqStr=GDNum=
     */
    suspend fun archiveBaseInfoSearch(assetsNo: String): Flow<RespResult<DataSet<ArchiveBaseData>>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_ASSETS_NO}=$assetsNo"
                val resp = api.archiveBaseInfoSearch(reqStr = reqStr)
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 设备转移未审核列表
     * ReqStr=Maker=罗江萍(18879971948)
     */
    suspend fun fetchTransferList(user: String): Flow<RespResult<DataSet<TransferListData>>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_MAKER}=$user"
                val resp = api.fetchTransferList(reqStr = reqStr)
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 设备转移 提交
     * ReqStr=CostDepartNo=P;GDNum=PBG240520;OldGDUseDepartName=版师组;OldGDPerson=陈艳红(13684856179);OldCFPostion=财务室;
     *     GDUseDepartName=成型A线;GDPerson=边二飞(18059509977);CFPostion=成型办公室;ItemNo=BG0000137;Qty=1;Maker=管理员
     */
    suspend fun transferSubmit(costComNo: String, assetsNo: String,
                               oldDepartment: String, oldPerson: String, oldArea: String,
                               department: String, person: String, area: String,
                               archiveNo: String, qty: String, user: String): Flow<RespResult<Any?>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_COST_COM_NO}=$costComNo;" +
                        "${HttpConfig.REQ_STR_ASSETS_NO}=$assetsNo;" +
                        "${HttpConfig.REQ_STR_DEPARTMENT_OLD}=$oldDepartment;" +
                        "${HttpConfig.REQ_STR_PERSON_OLD}=$oldPerson;" +
                        "${HttpConfig.REQ_STR_AREA_OLD}=$oldArea;" +
                        "${HttpConfig.REQ_STR_DEPARTMENT}=$department;" +
                        "${HttpConfig.REQ_STR_PERSON}=$person;" +
                        "${HttpConfig.REQ_STR_AREA}=$area;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_NO}=$archiveNo;" +
                        "${HttpConfig.REQ_STR_QTY}=$qty;" +
                        "${HttpConfig.REQ_STR_MAKER}=$user"
                val resp = api.transferSubmit(reqStr = reqStr)
                if (resp.result == 1) {
                    emit(RespResult.Success(Unit))
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 设备借出未审核列表
     */
    suspend fun fetchLendList(): Flow<RespResult<DataSet<LRListData>>> {
        return flow {
            try {
                val resp = api.fetchLendList()
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 设备借出 提交
     * ReqStr=CostDepartNo=P;CompanyName=皇亚鞋业加工厂;GDNum=PBG240520;GDUseDepartName=版师组;
     *      GDPerson=陈艳红(13684856179);CFPostion=财务室;ItemNo=BG0000137;Qty=1;Maker=管理员
     */
    suspend fun lendSubmit(costComNo: String, provider: String, assetsNo: String,
                               department: String, person: String, area: String,
                               archiveNo: String, qty: String, user: String): Flow<RespResult<Any?>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_COST_COM_NO}=$costComNo;" +
                        "${HttpConfig.REQ_STR_COMPANY}=$provider;" +
                        "${HttpConfig.REQ_STR_ASSETS_NO}=$assetsNo;" +
                        "${HttpConfig.REQ_STR_DEPARTMENT}=$department;" +
                        "${HttpConfig.REQ_STR_PERSON}=$person;" +
                        "${HttpConfig.REQ_STR_AREA}=$area;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_NO}=$archiveNo;" +
                        "${HttpConfig.REQ_STR_QTY}=$qty;" +
                        "${HttpConfig.REQ_STR_MAKER}=$user"
                val resp = api.lendSubmit(reqStr = reqStr)
                if (resp.result == 1) {
                    emit(RespResult.Success(Unit))
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 设备归还未审核列表
     */
    suspend fun fetchReturnList(): Flow<RespResult<DataSet<LRListData>>> {
        return flow {
            try {
                val resp = api.fetchReturnList()
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 设备归还 提交
     * ReqStr=CostDepartNo=P;CompanyName=皇亚鞋业加工厂;GDNum=PBG240520;GDUseDepartName=版师组;
     *      GDPerson=陈艳红(13684856179);CFPostion=财务室;ItemNo=BG0000137;Qty=1;Maker=管理员
     */
    suspend fun returnSubmit(costComNo: String, provider: String, assetsNo: String,
                           department: String, person: String, area: String,
                           archiveNo: String, qty: String, user: String): Flow<RespResult<Any?>> {
        return flow {
            try {
                val reqStr = "${HttpConfig.REQ_STR_COST_COM_NO}=$costComNo;" +
                        "${HttpConfig.REQ_STR_COMPANY}=$provider;" +
                        "${HttpConfig.REQ_STR_ASSETS_NO}=$assetsNo;" +
                        "${HttpConfig.REQ_STR_DEPARTMENT}=$department;" +
                        "${HttpConfig.REQ_STR_PERSON}=$person;" +
                        "${HttpConfig.REQ_STR_AREA}=$area;" +
                        "${HttpConfig.REQ_STR_ARCHIVE_NO}=$archiveNo;" +
                        "${HttpConfig.REQ_STR_QTY}=$qty;" +
                        "${HttpConfig.REQ_STR_MAKER}=$user"
                val resp = api.returnSubmit(reqStr = reqStr)
                if (resp.result == 1) {
                    emit(RespResult.Success(Unit))
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 获取往来单位供应商列表
     */
    suspend fun fetchProviderList(): Flow<RespResult<DataSet<ProviderData>>> {
        return flow {
            try {
                val resp = api.fetchProviderList()
                if (resp.result == 1) {
                    if (resp.dataSet != null) {
                        emit(RespResult.Success(resp.dataSet))
                    } else {
                        emit(RespResult.Error(Exception("缺少必要的[DataSet]参数")))
                    }
                } else {
                    emit(RespResult.Failure(resp.msg))
                }
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(Dispatchers.IO)
    }


    suspend fun checkVersion(): Flow<RespResult<UpdateBean>> {
        return flow {
            try {
                val resp = api.getVersion()
                emit(RespResult.Success(resp))
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(io)
    }

    // 开始下载
    suspend fun download(): Flow<RespResult<ResponseBody>> {
        return flow {
            try {
                val resp = api.downloadApk()
                emit(RespResult.Success(resp))
            } catch (e: Exception) {
                emit(RespResult.Error(e))
            }
        }.flowOn(io)
    }
}