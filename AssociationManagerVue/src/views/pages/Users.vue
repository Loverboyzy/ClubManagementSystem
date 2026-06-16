<template>
    <div class="page-container">
        <el-card class="query-card">
            <div slot="header" class="card-header">
                <i class="el-icon-search"></i>
                <span>信息查询</span>
            </div>
            <el-form :inline="true" :model="qryForm" class="query-form">
                <el-form-item>
                    <el-input v-model="qryForm.userName" placeholder="输入用户账号" prefix-icon="el-icon-search"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="qryForm.name" placeholder="输入用户姓名" prefix-icon="el-icon-search"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="qryForm.phone" placeholder="输入联系电话" prefix-icon="el-icon-phone"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="getPageLikeInfo">
                        <i class="el-icon-search"></i> 搜索
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="table-card">
            <div slot="header" v-if="userType === 0" class="card-header">
                <span>用户列表</span>
                <el-button type="primary" @click="showAddWin">
                    <i class="el-icon-plus"></i> 新增
                </el-button>
            </div>
            <el-table :data="pageInfos" v-loading="loading" border stripe>
                <el-table-column type="index" width="60" align="center"></el-table-column>
                <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
                <el-table-column prop="userName" label="账号" width="120" align="center"></el-table-column>
                <el-table-column prop="name" label="姓名" width="100" align="center"></el-table-column>
                <el-table-column prop="gender" label="性别" width="70" align="center"></el-table-column>
                <el-table-column prop="age" label="年龄" width="70" align="center"></el-table-column>
                <el-table-column prop="phone" label="电话" width="140" align="center"></el-table-column>
                <el-table-column prop="address" label="地址" show-overflow-tooltip></el-table-column>
                <el-table-column label="身份" width="100" align="center">
                    <template slot-scope="scope">
                        <el-tag :type="getRoleType(scope.row.type)" size="small">{{ getRoleText(scope.row.type) }}</el-tag>
                    </template>
                </el-table-column>
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
            <el-pagination
                v-if="pageTotal > 0"
                class="pagination"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="pageIndex"
                :page-sizes="[10, 20, 50]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="totalInfo">
            </el-pagination>
        </el-card>

        <el-dialog :title="dialogTitle" width="600px" :visible.sync="showAddFlag" @close="handleDialogClose">
            <el-form :model="usersForm" :rules="formRules" ref="usersForm" label-width="90px">
                <el-form-item label="用户角色">
                    <el-radio-group v-model="usersForm.type">
                        <el-radio :label="0">系统管理员</el-radio>
                        <el-radio :label="1">社团管理员</el-radio>
                        <el-radio :label="2">用户</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户账号" prop="userName">
                            <el-input v-model="usersForm.userName" placeholder="请输入账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="用户密码" prop="passWord">
                            <el-input v-model="usersForm.passWord" type="password" placeholder="请输入密码" show-password></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户姓名" prop="name">
                            <el-input v-model="usersForm.name" placeholder="请输入姓名"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="用户年龄" prop="age">
                            <el-input v-model="usersForm.age" placeholder="请输入年龄"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户性别">
                            <el-radio-group v-model="usersForm.gender">
                                <el-radio label="男">男</el-radio>
                                <el-radio label="女">女</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话" prop="phone">
                            <el-input v-model="usersForm.phone" placeholder="请输入电话"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="联系地址">
                    <el-input v-model="usersForm.address" type="textarea" rows="2" placeholder="请输入地址"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="修改信息" width="600px" :visible.sync="showUpdFlag" @close="handleDialogClose">
            <el-form :model="usersForm" :rules="formRules" ref="usersForm" label-width="90px">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户账号" prop="userName">
                            <el-input v-model="usersForm.userName" placeholder="请输入账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="用户密码">
                            <el-input v-model="usersForm.passWord" type="password" placeholder="留空则不修改" show-password></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户姓名" prop="name">
                            <el-input v-model="usersForm.name" placeholder="请输入姓名"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="用户年龄" prop="age">
                            <el-input v-model="usersForm.age" placeholder="请输入年龄"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户性别">
                            <el-radio-group v-model="usersForm.gender">
                                <el-radio label="男">男</el-radio>
                                <el-radio label="女">女</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话" prop="phone">
                            <el-input v-model="usersForm.phone" placeholder="请输入电话"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="联系地址">
                    <el-input v-model="usersForm.address" type="textarea" rows="2" placeholder="请输入地址"></el-input>
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
import { getPageUsers, addUsers, updUsers, delUsers, getLoginUser } from "../../api";

export default {
    data() {
        return {
            userType: "",
            pageInfos: [],
            pageIndex: 1,
            pageSize: 10,
            pageTotal: 0,
            totalInfo: 0,
            loading: true,
            showAddFlag: false,
            showUpdFlag: false,
            dialogTitle: "添加用户",
            qryForm: { userName: "", name: "", phone: "" },
            usersForm: { id: "", userName: "", passWord: "", name: "", gender: "", age: "", phone: "", address: "" },
            formRules: {
                userName: [{ required: true, message: "请输入账号", trigger: "blur" }],
                passWord: [{ required: true, message: "请输入密码", trigger: "blur" }],
                name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
                age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
                phone: [{ required: true, message: "请输入电话", trigger: "blur" }],
            },
        };
    },
    methods: {
        getRoleType(type) {
            return { 0: 'danger', 1: 'warning', 2: 'info' }[type] || 'info';
        },
        getRoleText(type) {
            return { 0: '系统管理员', 1: '社团管理员', 2: '用户' }[type] || '未知';
        },
        getPageInfo(pageIndex, pageSize) {
            this.loading = true;
            getPageUsers(pageIndex, pageSize, this.qryForm.userName, this.qryForm.name, this.qryForm.phone).then((resp) => {
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
        initForm() {
            this.usersForm = { id: "", userName: "", passWord: "", name: "", gender: "", age: "", phone: "", address: "" };
        },
        showAddWin() {
            this.initForm();
            this.dialogTitle = "添加用户";
            this.showAddFlag = true;
        },
        showUpdWin(row) {
            this.usersForm = { ...row, passWord: "" };
            this.showUpdFlag = true;
        },
        handleDialogClose() {
            this.$refs.usersForm && this.$refs.usersForm.resetFields();
        },
        addInfo() {
            this.$refs.usersForm.validate((valid) => {
                if (!valid) return;
                this.usersForm.status = 1;
                addUsers(this.usersForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showAddFlag = false;
                });
            });
        },
        updInfo() {
            this.$refs.usersForm.validate((valid) => {
                if (!valid) return;
                updUsers(this.usersForm).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                    this.showUpdFlag = false;
                });
            });
        },
        delInfo(id) {
            this.$confirm("即将删除该用户, 是否继续?", "确认", { type: "warning" }).then(() => {
                delUsers(id).then((resp) => {
                    this.$message.success(resp.msg);
                    this.getPageInfo(1, this.pageSize);
                });
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
