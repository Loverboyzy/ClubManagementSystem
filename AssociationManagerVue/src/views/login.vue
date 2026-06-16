<template>
    <div class="login-container">
        <div class="login-box">
            <div class="login-header">
                <h1 class="login-title">社团管理系统</h1>
                <p class="login-subtitle">Association Management System</p>
            </div>
            <el-form :model="loginForm" :rules="rules" ref="loginForm" class="login-form">
                <el-form-item prop="userName">
                    <el-input
                        v-model="loginForm.userName"
                        prefix-icon="el-icon-user"
                        placeholder="请输入账号"
                    ></el-input>
                </el-form-item>
                <el-form-item prop="passWord">
                    <el-input
                        v-model="loginForm.passWord"
                        prefix-icon="el-icon-lock"
                        placeholder="请输入密码"
                        show-password
                    ></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm('loginForm')" class="login-btn">
                        登 录
                    </el-button>
                </el-form-item>
                <el-form-item>
                    <el-button @click="showAddWin()" class="register-btn">
                        注 册 新 账 号
                    </el-button>
                </el-form-item>
            </el-form>
        </div>

        <el-dialog title="用户注册" width="500px" :visible.sync="showAddFlag">
            <el-form label-width="80px" :model="usersForm" :rules="registerRules" ref="usersForm">
                <el-form-item label="账号" prop="userName">
                    <el-input v-model="usersForm.userName" placeholder="请输入账号"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="passWord">
                    <el-input v-model="usersForm.passWord" type="password" placeholder="请输入密码" show-password></el-input>
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="usersForm.name" placeholder="请输入姓名"></el-input>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                    <el-input v-model="usersForm.age" placeholder="请输入年龄"></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="usersForm.gender">
                        <el-radio label="男">男</el-radio>
                        <el-radio label="女">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                    <el-input v-model="usersForm.phone" placeholder="请输入联系电话"></el-input>
                </el-form-item>
                <el-form-item label="地址">
                    <el-input v-model="usersForm.address" placeholder="请输入联系地址"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo()">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<style>
.login-container {
    width: 100%;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f7fa;
}

.login-box {
    width: 400px;
    padding: 40px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-header {
    text-align: center;
    margin-bottom: 30px;
}

.login-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px 0;
}

.login-subtitle {
    font-size: 13px;
    color: #909399;
    margin: 0;
    letter-spacing: 1px;
}

.login-form .el-input__inner {
    height: 42px;
}

.login-btn,
.register-btn {
    width: 100%;
    height: 42px;
    font-size: 15px;
}

.login-btn {
    margin-top: 10px;
}

.dialog-footer {
    text-align: right;
}
</style>

<script>
import initMenu from "../utils/menus.js";
import { login, addUsers } from "../api/index.js";
export default {
    data() {
        return {
            showAddFlag: false,
            usersForm: {
                id: "",
                userName: "",
                passWord: "",
                name: "",
                gender: "",
                age: "",
                phone: "",
                address: "",
                type: 2,
                status: 1,
            },
            loginForm: {
                userName: "",
                passWord: "",
            },
            rules: {
                userName: [
                    { required: true, message: "请输入账号", trigger: "blur" },
                ],
                passWord: [
                    { required: true, message: "请输入密码", trigger: "blur" },
                ],
            },
            registerRules: {
                userName: [
                    { required: true, message: "请输入账号", trigger: "blur" },
                ],
                passWord: [
                    { required: true, message: "请输入密码", trigger: "blur" },
                ],
                name: [
                    { required: true, message: "请输入姓名", trigger: "blur" },
                ],
                age: [
                    { required: true, message: "请输入年龄", trigger: "blur" },
                ],
                gender: [
                    { required: true, message: "请选择性别", trigger: "change" },
                ],
                phone: [
                    { required: true, message: "请输入手机号", trigger: "blur" },
                ],
            },
        };
    },
    methods: {
        showAddWin() {
            this.$refs.usersForm && this.$refs.usersForm.resetFields();
            this.usersForm = {
                id: "",
                userName: "",
                passWord: "",
                name: "",
                gender: "",
                age: "",
                phone: "",
                address: "",
                type: 2,
                status: 1,
            };
            this.showAddFlag = true;
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    login(this.loginForm)
                        .then((res) => {
                            this.$store.commit("setToken", res.data);
                            sessionStorage.setItem("token", res.data);
                            initMenu(this.$router, this.$store);
                            this.$router.push("/index");
                        })
                } else {
                    return false;
                }
            });
        },
        addInfo() {
            this.$refs.usersForm.validate((valid) => {
                if (!valid) return;

                addUsers(this.usersForm).then((resp) => {
                    if (resp.code == 0) {
                        this.$confirm("注册成功, 立即登录?", "提示", {
                            confirmButtonText: "确定",
                            cancelButtonText: "取消",
                            type: "success",
                        }).then(() => {
                            login({
                                userName: this.usersForm.userName,
                                passWord: this.usersForm.passWord,
                            }).then((res) => {
                                this.$store.commit("setToken", res.data);
                                sessionStorage.setItem("token", res.data);
                                initMenu(this.$router, this.$store);
                                this.$router.push("/index");
                            });
                        });
                    } else {
                        this.$message.warning(resp.msg);
                    }
                });
            });
        },
    },
};
</script>
