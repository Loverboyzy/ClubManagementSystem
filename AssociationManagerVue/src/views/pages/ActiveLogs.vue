<template>
    <div class="page-container">
        <el-card class="query-card">
            <div slot="header" class="card-header">
                <i class="el-icon-search"></i>
                <span>信息查询</span>
            </div>
            <el-form :inline="true" :model="qryForm" class="query-form">
                <el-form-item>
                    <el-input v-model="qryForm.name" placeholder="输入活动名称" prefix-icon="el-icon-search"></el-input>
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
                <span>报名记录</span>
                <el-button type="primary" @click="showAddWin">
                    <i class="el-icon-plus"></i> 新增
                </el-button>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="createTime" label="报名时间" width="160" align="center"></el-table-column>
                <el-table-column prop="activeId" label="活动ID" width="100" align="center"></el-table-column>
                <el-table-column prop="userId" label="用户ID" width="100" align="center"></el-table-column>
                <el-table-column label="操作" width="140" align="center" fixed="right">
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

        <el-dialog title="添加记录" width="450px" :visible.sync="showAddFlag" @close="handleDialogClose">
            <el-form :model="activeLogsForm" :rules="formRules" ref="activeLogsForm" label-width="90px">
                <el-form-item label="报名时间" prop="createTime">
                    <el-date-picker v-model="activeLogsForm.createTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择时间" style="width: 100%"></el-date-picker>
                </el-form-item>
                <el-form-item label="活动ID" prop="activeId">
                    <el-input v-model="activeLogsForm.activeId" placeholder="请输入活动ID"></el-input>
                </el-form-item>
                <el-form-item label="用户ID" prop="userId">
                    <el-input v-model="activeLogsForm.userId" placeholder="请输入用户ID"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="修改记录" width="450px" :visible.sync="showUpdFlag" @close="handleDialogClose">
            <el-form :model="activeLogsForm" :rules="formRules" ref="activeLogsForm" label-width="90px">
                <el-form-item label="报名时间" prop="createTime">
                    <el-date-picker v-model="activeLogsForm.createTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择时间" style="width: 100%"></el-date-picker>
                </el-form-item>
                <el-form-item label="活动ID" prop="activeId">
                    <el-input v-model="activeLogsForm.activeId" placeholder="请输入活动ID"></el-input>
                </el-form-item>
                <el-form-item label="用户ID" prop="userId">
                    <el-input v-model="activeLogsForm.userId" placeholder="请输入用户ID"></el-input>
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
import { getPageActiveLogs, addActiveLogs, updActiveLogs, delActiveLogs } from "../../api";

export default {
    data() {
        return {
            pageInfos: [], pageIndex: 1, pageSize: 10, pageTotal: 0, totalInfo: 0,
            loading: true, showAddFlag: false, showUpdFlag: false,
            qryForm: { name: "" },
            activeLogsForm: { id: "", createTime: "", activeId: "", userId: "" },
            formRules: {
                createTime: [{ required: true, message: "请选择报名时间", trigger: "change" }],
                activeId: [{ required: true, message: "请输入活动ID", trigger: "blur" }],
                userId: [{ required: true, message: "请输入用户ID", trigger: "blur" }],
            },
        };
    },
    methods: {
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPageActiveLogs(pageIndex, pageSize, this.qryForm.name).then((resp) => {
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
        initForm() { this.activeLogsForm = { id: "", createTime: "", activeId: "", userId: "" }; },
        showAddWin() { this.initForm(); this.showAddFlag = true; },
        showUpdWin(row) { this.activeLogsForm = { ...row }; this.showUpdFlag = true; },
        handleDialogClose() { this.$refs.activeLogsForm && this.$refs.activeLogsForm.resetFields(); },
        addInfo() {
            this.$refs.activeLogsForm.validate((valid) => {
                if (!valid) return;
                addActiveLogs(this.activeLogsForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showAddFlag = false;
                    this.initForm();
                });
            });
        },
        updInfo() {
            this.$refs.activeLogsForm.validate((valid) => {
                if (!valid) return;
                updActiveLogs(this.activeLogsForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showUpdFlag = false;
                    this.initForm();
                });
            });
        },
        delInfo(id) {
            this.$confirm("即将删除该记录, 是否继续?", "确认", { type: "warning" }).then(() => {
                delActiveLogs(id).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
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
