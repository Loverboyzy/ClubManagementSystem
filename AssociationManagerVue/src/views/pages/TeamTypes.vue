<template>
    <div class="page-container">
        <el-card class="query-card">
            <div slot="header" class="card-header">
                <i class="el-icon-search"></i>
                <span>信息查询</span>
            </div>
            <el-form :inline="true" :model="qryForm" class="query-form">
                <el-form-item>
                    <el-input v-model="qryForm.name" placeholder="输入类型名称" prefix-icon="el-icon-search"></el-input>
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
                <span>社团类型</span>
                <el-button type="primary" @click="showAddWin">
                    <i class="el-icon-plus"></i> 新增
                </el-button>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="name" label="类型名称" min-width="200" align="center"></el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" align="center"></el-table-column>
                <el-table-column label="操作" width="160" align="center" fixed="right">
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

        <el-dialog :title="dialogTitle" width="450px" :visible.sync="showAddFlag" @close="handleDialogClose">
            <el-form :model="teamTypesForm" :rules="formRules" ref="teamTypesForm" label-width="90px">
                <el-form-item label="类型名称" prop="name">
                    <el-input v-model="teamTypesForm.name" placeholder="请输入类型名称"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="修改类型" width="450px" :visible.sync="showUpdFlag" @close="handleDialogClose">
            <el-form :model="teamTypesForm" :rules="formRules" ref="teamTypesForm" label-width="90px">
                <el-form-item label="类型名称" prop="name">
                    <el-input v-model="teamTypesForm.name" placeholder="请输入类型名称"></el-input>
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
import { getPageTeamTypes, addTeamTypes, updTeamTypes, delTeamTypes } from "../../api";

export default {
    data() {
        return {
            pageInfos: [], pageIndex: 1, pageSize: 10, pageTotal: 0, totalInfo: 0,
            loading: true, showAddFlag: false, showUpdFlag: false, dialogTitle: "添加类型",
            qryForm: { name: "" },
            teamTypesForm: { id: "", name: "" },
            formRules: { name: [{ required: true, message: "请输入类型名称", trigger: "blur" }] },
        };
    },
    methods: {
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPageTeamTypes(pageIndex, pageSize, this.qryForm.name).then((resp) => {
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
        initForm() { this.teamTypesForm = { id: "", name: "" }; },
        showAddWin() { this.initForm(); this.dialogTitle = "添加类型"; this.showAddFlag = true; },
        showUpdWin(row) { this.teamTypesForm = { ...row }; this.showUpdFlag = true; },
        handleDialogClose() { this.$refs.teamTypesForm && this.$refs.teamTypesForm.resetFields(); },
        addInfo() {
            this.$refs.teamTypesForm.validate((valid) => {
                if (!valid) return;
                addTeamTypes(this.teamTypesForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showAddFlag = false;
                    this.initForm();
                });
            });
        },
        updInfo() {
            this.$refs.teamTypesForm.validate((valid) => {
                if (!valid) return;
                updTeamTypes(this.teamTypesForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showUpdFlag = false;
                    this.initForm();
                });
            });
        },
        delInfo(id) {
            this.$confirm("即将删除该类型, 是否继续?", "确认", { type: "warning" }).then(() => {
                delTeamTypes(id).then((resp) => {
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
