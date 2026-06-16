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
                    <el-input v-model="qryForm.activeName" placeholder="输入活动名称" prefix-icon="el-icon-search"></el-input>
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
                <span>活动列表</span>
                <el-button v-if="userType === 1" type="primary" @click="showAddWin">
                    <i class="el-icon-plus"></i> 新增
                </el-button>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="name" label="活动名称" min-width="150" align="center"></el-table-column>
                <el-table-column prop="teamName" label="发布社团" width="120" align="center"></el-table-column>
                <el-table-column prop="activeTime" label="活动时间" width="160" align="center"></el-table-column>
                <el-table-column prop="total" label="报名人数" width="100" align="center"></el-table-column>
                <el-table-column label="参与人员" width="120" align="center">
                    <template slot-scope="scope">
                        <el-popover trigger="click" @show="getActivePeople(scope.row.id)" width="500">
                            <el-table :data="activeLogs" border max-height="300">
                                <el-table-column type="index" width="50" align="center"></el-table-column>
                                <el-table-column prop="userName" label="姓名" align="center"></el-table-column>
                                <el-table-column prop="userPhone" label="电话" width="120" align="center"></el-table-column>
                                <el-table-column prop="createTime" label="报名时间" width="160" align="center"></el-table-column>
                            </el-table>
                            <el-button slot="reference" type="primary" size="mini">查看</el-button>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column v-if="userType === 0" label="操作" width="100" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button type="danger" size="mini" @click="delInfo(scope.row.id)">
                            <i class="el-icon-delete"></i>
                        </el-button>
                    </template>
                </el-table-column>
                <el-table-column v-if="userType === 2" label="操作" width="120" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-popover trigger="click" width="500">
                            <div style="padding: 10px;">
                                <el-button type="success" size="mini" @click="active(scope.row.id)" style="margin-bottom: 15px;">
                                    <i class="el-icon-check"></i> 我要报名
                                </el-button>
                                <el-descriptions :column="1" size="small" border>
                                    <el-descriptions-item label="活动标题">{{ scope.row.name }}</el-descriptions-item>
                                    <el-descriptions-item label="发布社团">{{ scope.row.teamName }}</el-descriptions-item>
                                    <el-descriptions-item label="活动时间">{{ scope.row.activeTime }}</el-descriptions-item>
                                    <el-descriptions-item label="活动概述">{{ scope.row.comm }}</el-descriptions-item>
                                    <el-descriptions-item label="活动要求">{{ scope.row.ask }}</el-descriptions-item>
                                    <el-descriptions-item label="活动详情">{{ scope.row.detail }}</el-descriptions-item>
                                </el-descriptions>
                            </div>
                            <el-button slot="reference" type="primary" size="mini">详情</el-button>
                        </el-popover>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-if="pageTotal > 0" class="pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageIndex" :page-sizes="[10, 20, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalInfo"></el-pagination>
        </el-card>

        <el-dialog title="发布活动" width="600px" :visible.sync="showAddFlag" @close="handleDialogClose">
            <el-form :model="activitiesForm" :rules="formRules" ref="activitiesForm" label-width="90px">
                <el-form-item label="活动名称" prop="name">
                    <el-input v-model="activitiesForm.name" placeholder="请输入活动名称"></el-input>
                </el-form-item>
                <el-form-item label="活动时间" prop="activeTime">
                    <el-date-picker v-model="activitiesForm.activeTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择活动时间" style="width: 100%"></el-date-picker>
                </el-form-item>
                <el-form-item label="发布社团" prop="teamId">
                    <el-select v-model="activitiesForm.teamId" placeholder="请选择发布社团" style="width: 100%">
                        <el-option v-for="(item, index) in teams" :key="index" :label="item.name" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="活动概述" prop="comm">
                    <el-input v-model="activitiesForm.comm" type="textarea" rows="3" placeholder="请输入活动概述"></el-input>
                </el-form-item>
                <el-form-item label="活动要求" prop="ask">
                    <el-input v-model="activitiesForm.ask" type="textarea" rows="3" placeholder="请输入活动要求"></el-input>
                </el-form-item>
                <el-form-item label="活动详情" prop="detail">
                    <el-input v-model="activitiesForm.detail" type="textarea" rows="4" placeholder="请输入活动详情"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo">发 布</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { addActiveLogs, getManTeamList, getLoginUser, getActiveLogs, getPageActivities, addActivities, delActivities } from "../../api";

export default {
    data() {
        return {
            teams: [], userType: "", activeLogs: [], pageInfos: [], pageIndex: 1, pageSize: 10, pageTotal: 0, totalInfo: 0,
            loading: true, showAddFlag: false,
            qryForm: { token: this.$store.state.token, teamName: "", activeName: "" },
            activitiesForm: { id: "", name: "", comm: "", detail: "", ask: "", total: 1, activeTime: "", teamId: "" },
            formRules: {
                name: [{ required: true, message: "请输入活动名称", trigger: "blur" }],
                activeTime: [{ required: true, message: "请选择活动时间", trigger: "change" }],
                teamId: [{ required: true, message: "请选择发布社团", trigger: "change" }],
            },
        };
    },
    methods: {
        getActivePeople(activeId) {
            getActiveLogs(activeId).then((resp) => { this.activeLogs = resp.data || []; });
        },
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPageActivities(pageIndex, pageSize, this.qryForm.token, this.qryForm.teamName, this.qryForm.activeName).then((resp) => {
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
        initForm() { this.activitiesForm = { id: "", name: "", comm: "", detail: "", ask: "", total: 1, activeTime: "", teamId: "" }; },
        showAddWin() { this.initForm(); this.showAddFlag = true; },
        handleDialogClose() { this.$refs.activitiesForm && this.$refs.activitiesForm.resetFields(); },
        active(id) {
            addActiveLogs({ token: this.$store.state.token, activeId: id }).then((resp) => {
                if (resp.code === 0) {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                } else { this.$message.warning(resp.msg); }
            });
        },
        addInfo() {
            this.$refs.activitiesForm.validate((valid) => {
                if (!valid) return;
                addActivities(this.activitiesForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showAddFlag = false;
                });
            });
        },
        delInfo(id) {
            this.$confirm("删除活动将同时删除报名记录, 是否继续?", "确认", { type: "warning" }).then(() => {
                delActivities(id).then((resp) => {
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
