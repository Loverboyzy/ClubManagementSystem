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
                    <el-input v-model="qryForm.userName" placeholder="输入成员姓名" prefix-icon="el-icon-search"></el-input>
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
                <span>社团成员</span>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="userId" label="成员ID" width="80" align="center"></el-table-column>
                <el-table-column prop="userName" label="成员姓名" width="100" align="center"></el-table-column>
                <el-table-column prop="userGender" label="性别" width="70" align="center"></el-table-column>
                <el-table-column prop="userAge" label="年龄" width="70" align="center"></el-table-column>
                <el-table-column prop="userPhone" label="电话" width="140" align="center"></el-table-column>
                <el-table-column prop="teamName" label="所属社团" min-width="150" align="center"></el-table-column>
                <el-table-column prop="createTime" label="入团时间" width="160" align="center"></el-table-column>
                <el-table-column label="操作" width="100" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button type="danger" size="mini" @click="delInfo(scope.row.id)">
                            <i class="el-icon-delete"></i>
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-if="pageTotal > 0" class="pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageIndex" :page-sizes="[10, 20, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalInfo"></el-pagination>
        </el-card>
    </div>
</template>

<script>
import { getPageMembers, delMembers } from "../../api";

export default {
    data() {
        return {
            pageInfos: [], pageIndex: 1, pageSize: 10, pageTotal: 0, totalInfo: 0,
            loading: true,
            qryForm: { token: this.$store.state.token, teamName: "", userName: "" },
        };
    },
    methods: {
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPageMembers(pageIndex, pageSize, this.qryForm.token, this.qryForm.teamName, this.qryForm.userName).then((resp) => {
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
        delInfo(id) {
            this.$confirm("移除成员将同时移除相关记录, 是否继续?", "确认", { type: "warning" }).then(() => {
                delMembers(id).then((resp) => {
                    if (resp.code === 0) {
                        this.$message.success(resp.msg);
                        this.getPageInfo(1, this.pageSize);
                    } else { this.$message.warning(resp.msg); }
                });
            });
        },
    },
    mounted() { this.getPageInfo(1, this.pageSize); },
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
