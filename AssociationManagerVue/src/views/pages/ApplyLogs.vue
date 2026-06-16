<template>
    <div class="page-container">
        <el-card class="query-card">
            <div slot="header" class="card-header">
                <i class="el-icon-search"></i>
                <span>信息查询</span>
            </div>
            <el-form :inline="true" :model="qryForm" class="query-form">
                <el-form-item>
                    <el-input v-model="qryForm.teamName" placeholder="输入社团名称" prefix-icon="el-icon-search"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="qryForm.userName" placeholder="输入申请人姓名" prefix-icon="el-icon-search"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="getPageLikeInfo">
                        <i class="el-icon-search"></i> 搜索
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <div slot="header" class="card-header">
                <span>申请记录</span>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="teamName" label="社团名称" min-width="150" align="center"></el-table-column>
                <el-table-column prop="userName" label="申请人" width="100" align="center"></el-table-column>
                <el-table-column prop="userGender" label="性别" width="70" align="center"></el-table-column>
                <el-table-column prop="userPhone" label="电话" width="140" align="center"></el-table-column>
                <el-table-column prop="createTime" label="申请时间" width="160" align="center"></el-table-column>
                <el-table-column v-if="userType === 1" label="操作" width="180" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button v-if="scope.row.status === 0" type="success" size="mini" @click="updInfo(scope.row, 1)">
                            <i class="el-icon-check"></i> 通过
                        </el-button>
                        <el-button v-if="scope.row.status === 0" type="danger" size="mini" @click="updInfo(scope.row, 2)">
                            <i class="el-icon-close"></i> 驳回
                        </el-button>
                        <el-tag v-if="scope.row.status === 1" type="success" size="small">已通过</el-tag>
                        <el-tag v-if="scope.row.status === 2" type="danger" size="small">已驳回</el-tag>
                    </template>
                </el-table-column>
                <el-table-column v-else label="状态" width="100" align="center">
                    <template slot-scope="scope">
                        <el-tag :type="getStatusType(scope.row.status)" size="small">{{ getStatusText(scope.row.status) }}</el-tag>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-if="pageTotal > 0" class="pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageIndex" :page-sizes="[10, 20, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalInfo"></el-pagination>
        </el-card>
    </div>
</template>

<script>
import { getLoginUser, getPageApplyLogs, updApplyLogs } from "../../api";

export default {
    data() {
        return {
            userType: "", pageInfos: [], pageIndex: 1, pageSize: 10, pageTotal: 0, totalInfo: 0,
            loading: true,
            qryForm: { token: this.$store.state.token, teamName: "", userName: "" },
        };
    },
    methods: {
        getStatusType(status) {
            return { 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info';
        },
        getStatusText(status) {
            return { 0: '审核中', 1: '已通过', 2: '已驳回' }[status] || '未知';
        },
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPageApplyLogs(pageIndex, pageSize, this.qryForm.token, this.qryForm.teamName, this.qryForm.userName).then((resp) => {
                this.pageInfos = resp.data.data;
                this.pageIndex = resp.data.pageIndex;
                this.pageSize = resp.data.pageSize;
                this.pageTotal = resp.data.pageTotal;
                this.totalInfo = resp.data.count;
                this.loading = false;
            });
        },
        getPageLikeInfo() { this.getPageInfo(1, this.pageSize); },
        handleSizeChange(pageSize) { this.getPageInfo(this.pageIndex, pageSize); },
        handleCurrentChange(pageIndex) { this.getPageInfo(pageIndex, this.pageSize); },
        updInfo(data, status) {
            data.status = status;
            updApplyLogs(data).then((resp) => {
                this.$message.success(resp.msg);
                this.getPageInfo(1, this.pageSize);
            });
        },
    },
    mounted() {
        this.getPageInfo(1, this.pageSize);
        getLoginUser(this.$store.state.token).then((resp) => { this.userType = resp.data.type; });
    },
};
</script>

<style scoped>
.page-container { padding: 15px; }
.query-card, .table-card { margin-bottom: 15px; }
.card-header { display: flex; justify-content: space-between; align-items: center; font-size: 15px; font-weight: 500; }
.card-header i { margin-right: 8px; color: #409eff; }
.query-form { margin-top: 10px; }
.pagination { margin-top: 15px; text-align: center; }
</style>
