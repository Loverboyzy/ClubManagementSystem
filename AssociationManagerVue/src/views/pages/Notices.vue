<template>
    <div class="page-container">
        <el-card class="query-card">
            <div slot="header" class="card-header">
                <i class="el-icon-search"></i>
                <span>信息查询</span>
            </div>
            <el-form :inline="true" :model="qryForm" class="query-form">
                <el-form-item>
                    <el-input v-model="qryForm.title" placeholder="输入通知标题" prefix-icon="el-icon-search"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="qryForm.teamName" placeholder="输入社团名称" prefix-icon="el-icon-search"></el-input>
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
                <span>通知列表</span>
                <el-button v-if="userType !== 2" type="primary" @click="showAddWin">
                    <i class="el-icon-plus"></i> 新增
                </el-button>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="title" label="通知标题" min-width="200" align="center"></el-table-column>
                <el-table-column label="发布社团" width="120" align="center">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.teamName" type="success" size="small">{{ scope.row.teamName }}</el-tag>
                        <el-tag v-else type="info" size="small">系统通知</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="160" align="center"></el-table-column>
                <el-table-column prop="detail" label="通知内容" show-overflow-tooltip></el-table-column>
                <el-table-column v-if="userType !== 2" label="操作" width="100" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button v-if="userType === 0 || (userType === 1 && scope.row.teamId)" type="danger" size="mini" @click="delInfo(scope.row.id)">
                            <i class="el-icon-delete"></i>
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-if="pageTotal > 0" class="pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageIndex" :page-sizes="[10, 20, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalInfo"></el-pagination>
        </el-card>

        <el-dialog :title="dialogTitle" width="550px" :visible.sync="showAddFlag" @close="handleDialogClose">
            <el-form :model="noticesForm" :rules="formRules" ref="noticesForm" label-width="90px">
                <el-form-item label="通知标题" prop="title">
                    <el-input v-model="noticesForm.title" placeholder="请输入通知标题"></el-input>
                </el-form-item>
                <el-form-item v-if="userType === 1" label="发布社团" prop="teamId">
                    <el-select v-model="noticesForm.teamId" placeholder="请选择发布社团" style="width: 100%">
                        <el-option v-for="(item, index) in teams" :key="index" :label="item.name" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="通知内容" prop="detail">
                    <el-input v-model="noticesForm.detail" type="textarea" rows="4" placeholder="请输入通知内容"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getManTeamList, getLoginUser, getPageNotices, addNotices, delNotices } from "../../api";

export default {
    data() {
        return {
            teams: [], userType: "", pageInfos: [], pageIndex: 1, pageSize: 10, pageTotal: 0, totalInfo: 0,
            loading: true, showAddFlag: false, dialogTitle: "发布通知",
            qryForm: { token: this.$store.state.token, teamName: "", title: "" },
            noticesForm: { id: "", title: "", detail: "", teamId: null },
            formRules: {
                title: [{ required: true, message: "请输入通知标题", trigger: "blur" }],
                detail: [{ required: true, message: "请输入通知内容", trigger: "blur" }],
            },
        };
    },
    methods: {
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPageNotices(pageIndex, pageSize, this.qryForm.token, this.qryForm.title, this.qryForm.teamName).then((resp) => {
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
        initForm() { this.noticesForm = { id: "", title: "", detail: "", teamId: null }; },
        showAddWin() { this.initForm(); this.dialogTitle = "发布通知"; this.showAddFlag = true; },
        handleDialogClose() { this.$refs.noticesForm && this.$refs.noticesForm.resetFields(); },
        addInfo() {
            this.$refs.noticesForm.validate((valid) => {
                if (!valid) return;
                addNotices(this.noticesForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showAddFlag = false;
                });
            });
        },
        delInfo(id) {
            this.$confirm("即将删除该通知, 是否继续?", "确认", { type: "warning" }).then(() => {
                delNotices(id).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                });
            });
        },
    },
    mounted() {
        this.getPageInfo(1, this.pageSize);
        getLoginUser(this.$store.state.token).then((resp) => {
            this.userType = resp.data.type;
            if (resp.data.type === 1) {
                getManTeamList(resp.data.id).then((resp) => { this.teams = resp.data; });
            }
        });
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
