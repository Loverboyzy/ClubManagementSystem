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
                <span>缴费记录</span>
                <el-button v-if="userType === 1" type="primary" @click="showAddWin">
                    <i class="el-icon-plus"></i> 新增
                </el-button>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="teamName" label="社团名称" min-width="150" align="center"></el-table-column>
                <el-table-column prop="userName" label="成员姓名" width="100" align="center"></el-table-column>
                <el-table-column prop="userGender" label="性别" width="70" align="center"></el-table-column>
                <el-table-column prop="userPhone" label="电话" width="140" align="center"></el-table-column>
                <el-table-column prop="createTime" label="缴费时间" width="160" align="center"></el-table-column>
                <el-table-column prop="total" label="缴纳费用" width="120" align="center">
                    <template slot-scope="scope">
                        <span style="color: #f56c6c; font-weight: bold;">¥{{ scope.row.total }}</span>
                    </template>
                </el-table-column>
                <el-table-column v-if="userType === 1" label="操作" width="160" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button type="primary" size="mini" @click="showUpdWin(scope.row)">
                            <i class="el-icon-edit"></i>
                        </el-button>
                        <el-button type="danger" size="mini" @click="delInfo(scope.row.id)">
                            <i class="el-icon-delete"></i>
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-if="pageTotal > 0" class="pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageIndex" :page-sizes="[10, 20, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalInfo"></el-pagination>
        </el-card>

        <el-dialog title="添加缴费" width="500px" :visible.sync="showAddFlag" @close="handleDialogClose">
            <el-form :model="payLogsForm" :rules="formRules" ref="payLogsForm" label-width="90px">
                <el-form-item label="缴纳费用" prop="total">
                    <el-input v-model="payLogsForm.total" placeholder="请输入缴纳金额" type="number">
                        <template slot="append">元</template>
                    </el-input>
                </el-form-item>
                <el-form-item label="收费社团" prop="teamId">
                    <el-select v-model="payLogsForm.teamId" placeholder="请选择社团" style="width: 100%">
                        <el-option v-for="(item, index) in teams" :key="index" :label="item.name" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="缴费用户" prop="userId">
                    <el-input v-model="payLogsForm.userId" placeholder="请输入用户ID"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="修改缴费" width="500px" :visible.sync="showUpdFlag" @close="handleDialogClose">
            <el-form :model="payLogsForm" :rules="formRules" ref="payLogsForm" label-width="90px">
                <el-form-item label="缴纳费用" prop="total">
                    <el-input v-model="payLogsForm.total" placeholder="请输入缴纳金额" type="number">
                        <template slot="append">元</template>
                    </el-input>
                </el-form-item>
                <el-form-item label="收费社团" prop="teamId">
                    <el-select v-model="payLogsForm.teamId" placeholder="请选择社团" style="width: 100%">
                        <el-option v-for="(item, index) in teams" :key="index" :label="item.name" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="缴费用户" prop="userId">
                    <el-input v-model="payLogsForm.userId" placeholder="请输入用户ID"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="showUpdFlag = false">取 消</el-button>
                <el-button type="primary" @click="updInfo">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getAllTeamList, getManTeamList, getLoginUser, getPagePayLogs, addPayLogs, updPayLogs, delPayLogs } from "../../api";

export default {
    data() {
        return {
            teams: [], userType: "", pageInfos: [], pageIndex: 1, pageSize: 10, pageTotal: 0, totalInfo: 0,
            loading: true, showAddFlag: false, showUpdFlag: false,
            qryForm: { token: this.$store.state.token, teamName: "", userName: "" },
            payLogsForm: { id: "", total: "", teamId: "", userId: "" },
            formRules: {
                total: [{ required: true, message: "请输入缴纳费用", trigger: "blur" }],
                teamId: [{ required: true, message: "请选择社团", trigger: "change" }],
                userId: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
            },
        };
    },
    methods: {
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPagePayLogs(pageIndex, pageSize, this.qryForm.token, this.qryForm.teamName, this.qryForm.userName).then((resp) => {
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
        initForm() { this.payLogsForm = { id: "", total: "", teamId: "", userId: "" }; },
        showAddWin() { this.initForm(); this.showAddFlag = true; },
        showUpdWin(row) { this.payLogsForm = { ...row }; this.showUpdFlag = true; },
        handleDialogClose() { this.$refs.payLogsForm && this.$refs.payLogsForm.resetFields(); },
        addInfo() {
            this.$refs.payLogsForm.validate((valid) => {
                if (!valid) return;
                addPayLogs(this.payLogsForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showAddFlag = false;
                });
            });
        },
        updInfo() {
            this.$refs.payLogsForm.validate((valid) => {
                if (!valid) return;
                updPayLogs(this.payLogsForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showUpdFlag = false;
                });
            });
        },
        delInfo(id) {
            this.$confirm("即将删除该记录, 是否继续?", "确认", { type: "warning" }).then(() => {
                delPayLogs(id).then((resp) => {
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
            } else {
                getAllTeamList().then((resp) => { this.teams = resp.data; });
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
