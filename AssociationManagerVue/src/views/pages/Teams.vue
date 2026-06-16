<template>
    <div class="page-container">
        <el-card class="query-card">
            <div slot="header" class="card-header">
                <i class="el-icon-search"></i>
                <span>信息查询</span>
            </div>
            <el-form :inline="true" :model="qryForm" class="query-form">
                <el-form-item>
                    <el-input v-model="qryForm.name" placeholder="输入社团名称" prefix-icon="el-icon-search"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-select v-model="qryForm.typeId" placeholder="请选择社团类型" clearable>
                        <el-option v-for="(item, index) in teamTypes" :key="index" :label="item.name" :value="item.id"></el-option>
                    </el-select>
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
                <span>社团列表</span>
                <el-button v-if="userType === 0" type="primary" @click="showAddWin">
                    <i class="el-icon-plus"></i> 新增
                </el-button>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="name" label="社团名称" min-width="150" align="center"></el-table-column>
                <el-table-column prop="typeName" label="社团类型" width="120" align="center"></el-table-column>
                <el-table-column prop="managerName" label="社团团长" width="100" align="center"></el-table-column>
                <el-table-column prop="createTime" label="建立时间" width="160" align="center"></el-table-column>
                <el-table-column prop="total" label="人数" width="80" align="center"></el-table-column>
                <el-table-column v-if="userType === 0" label="操作" width="180" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button type="primary" size="mini" @click="showUpdWin(scope.row)">
                            <i class="el-icon-edit"></i>
                        </el-button>
                        <el-button type="danger" size="mini" @click="delInfo(scope.row.id)">
                            <i class="el-icon-delete"></i>
                        </el-button>
                    </template>
                </el-table-column>
                <el-table-column v-if="userType === 2" label="操作" width="100" align="center" fixed="right">
                    <template slot-scope="scope">
                        <el-button type="success" size="mini" @click="apply(scope.row.id)">
                            <i class="el-icon-check"></i> 申请
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination v-if="pageTotal > 0" class="pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="pageIndex" :page-sizes="[10, 20, 50]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalInfo"></el-pagination>
        </el-card>

        <el-dialog :title="dialogTitle" width="500px" :visible.sync="showAddFlag" @close="handleDialogClose">
            <el-form :model="teamsForm" :rules="formRules" ref="teamsForm" label-width="90px">
                <el-form-item label="社团名称" prop="name">
                    <el-input v-model="teamsForm.name" placeholder="请输入社团名称"></el-input>
                </el-form-item>
                <el-form-item label="社团类型" prop="typeId">
                    <el-select v-model="teamsForm.typeId" placeholder="请选择社团类型" style="width: 100%">
                        <el-option v-for="(item, index) in teamTypes" :key="index" :label="item.name" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="团长ID" prop="manager">
                    <el-input v-model="teamsForm.manager" placeholder="请输入团长用户ID"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="修改社团" width="500px" :visible.sync="showUpdFlag" @close="handleDialogClose">
            <el-form :model="teamsForm" :rules="formRules" ref="teamsForm" label-width="90px">
                <el-form-item label="社团名称" prop="name">
                    <el-input v-model="teamsForm.name" placeholder="请输入社团名称"></el-input>
                </el-form-item>
                <el-form-item label="社团类型" prop="typeId">
                    <el-select v-model="teamsForm.typeId" placeholder="请选择社团类型" style="width: 100%">
                        <el-option v-for="(item, index) in teamTypes" :key="index" :label="item.name" :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="团长ID" prop="manager">
                    <el-input v-model="teamsForm.manager" placeholder="请输入团长用户ID"></el-input>
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
import { getAllTypes, getPageTeams, getLoginUser, addTeams, updTeams, delTeams, addApplyLogs } from "../../api";

export default {
    data() {
        return {
            teamTypes: [], userType: "", pageInfos: [], pageIndex: 1, pageSize: 10, pageTotal: 0, totalInfo: 0,
            loading: true, showAddFlag: false, showUpdFlag: false, dialogTitle: "添加社团",
            qryForm: { name: "", typeId: "" },
            teamsForm: { id: "", name: "", total: 1, manager: "", typeId: "" },
            formRules: {
                name: [{ required: true, message: "请输入社团名称", trigger: "blur" }],
                typeId: [{ required: true, message: "请选择社团类型", trigger: "change" }],
                manager: [{ required: true, message: "请输入团长ID", trigger: "blur" }],
            },
        };
    },
    methods: {
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPageTeams(pageIndex, pageSize, this.$store.state.token, this.qryForm.name, this.qryForm.typeId).then((resp) => {
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
        initForm() { this.teamsForm = { id: "", name: "", total: 1, manager: "", typeId: "" }; },
        showAddWin() { this.initForm(); this.dialogTitle = "添加社团"; this.showAddFlag = true; },
        showUpdWin(row) { this.teamsForm = { ...row }; this.showUpdFlag = true; },
        handleDialogClose() { this.$refs.teamsForm && this.$refs.teamsForm.resetFields(); },
        addInfo() {
            this.$refs.teamsForm.validate((valid) => {
                if (!valid) return;
                addTeams(this.teamsForm).then((resp) => {
                    if (resp.code === 0) {
                        this.$message.success(resp.msg);
                        this.getPageInfo(1, this.pageSize);
                        this.showAddFlag = false;
                    } else { this.$message.warning(resp.msg); }
                });
            });
        },
        updInfo() {
            this.$refs.teamsForm.validate((valid) => {
                if (!valid) return;
                updTeams(this.teamsForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showUpdFlag = false;
                });
            });
        },
        delInfo(id) {
            this.$confirm("即将删除该社团, 是否继续?", "确认", { type: "warning" }).then(() => {
                delTeams(id).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                });
            });
        },
        apply(id) {
            this.$confirm("确认申请加入该社团吗?", "确认", { type: "warning" }).then(() => {
                addApplyLogs({ teamId: id, status: 0, token: this.$store.state.token }).then((resp) => {
                    if (resp.code === 0) {
                        this.$message.success("申请已提交，请耐心等待");
                        this.getPageInfo(1, this.pageSize);
                    } else { this.$message.warning(resp.msg); }
                });
            });
        },
    },
    mounted() {
        this.getPageInfo(1, this.pageSize);
        getAllTypes().then((resp) => { this.teamTypes = resp.data; });
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
