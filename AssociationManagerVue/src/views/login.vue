<template>
    <div class="login-container">
        <div class="login-bg-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
            <div class="shape shape-3"></div>
        </div>
        <div class="login-box">
            <div class="login-header">
                <div class="login-icon">🎓</div>
                <h1 class="login-title">社团管理系统</h1>
                <p class="login-subtitle">Club Management System</p>
            </div>
            <el-form :model="loginForm" :rules="rules" ref="loginForm" class="login-form">
                <el-form-item prop="userName">
                    <el-input
                        v-model="loginForm.userName"
                        prefix-icon="el-icon-user"
                        placeholder="请输入账号"
                        size="large"
                    ></el-input>
                </el-form-item>
                <el-form-item prop="passWord">
                    <el-input
                        v-model="loginForm.passWord"
                        prefix-icon="el-icon-lock"
                        placeholder="请输入密码"
                        show-password
                        size="large"
                        @keyup.enter.native="submitForm('loginForm')"
                    ></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitForm('loginForm')" class="login-btn" size="large" :loading="loading">
                        登 录
                    </el-button>
                </el-form-item>
                <el-form-item>
                    <el-button @click="showAddWin()" class="register-btn" size="large">
                        注 册 新 账 号
                    </el-button>
                </el-form-item>
            </el-form>
        </div>

        <el-dialog title="用户注册" width="520px" :visible.sync="showAddFlag" :close-on-click-modal="false">
            <el-form label-width="80px" :model="usersForm" :rules="registerRules" ref="usersForm" status-icon>
                <el-form-item label="账号" prop="userName">
                    <el-input v-model="usersForm.userName" placeholder="请输入账号（4-32位）"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="passWord">
                    <el-input v-model="usersForm.passWord" type="password" placeholder="请输入密码（至少6位）" show-password></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="checkPwd">
                    <el-input v-model="usersForm.checkPwd" type="password" placeholder="请再次输入密码" show-password></el-input>
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="usersForm.name" placeholder="请输入真实姓名"></el-input>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                    <el-input v-model="usersForm.age" placeholder="请输入年龄（0-120）"></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="usersForm.gender">
                        <el-radio label="男">男</el-radio>
                        <el-radio label="女">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                    <el-input v-model="usersForm.phone" placeholder="请输入11位手机号"></el-input>
                </el-form-item>
                <el-form-item label="地址">
                    <el-input v-model="usersForm.address" placeholder="请输入联系地址"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="showAddFlag = false">取 消</el-button>
                <el-button type="primary" @click="addInfo()" :loading="regLoading">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<style scoped>
.login-container {
    width: 100%;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    position: relative;
    overflow: hidden;
}

.login-bg-shapes .shape {
    position: absolute;
    border-radius: 50%;
    opacity: 0.1;
    background: #fff;
}
.shape-1 { width: 400px; height: 400px; top: -100px; right: -100px; }
.shape-2 { width: 300px; height: 300px; bottom: -80px; left: -80px; }
.shape-3 { width: 200px; height: 200px; top: 50%; left: 60%; }

.login-box {
    width: 420px;
    padding: 48px 44px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    position: relative;
    z-index: 1;
}

.login-header {
    text-align: center;
    margin-bottom: 36px;
}

.login-icon {
    font-size: 48px;
    margin-bottom: 12px;
}

.login-title {
    font-size: 26px;
    font-weight: 700;
    color: #303133;
    margin: 0 0 6px 0;
    letter-spacing: 2px;
}

.login-subtitle {
    font-size: 12px;
    color: #909399;
    margin: 0;
    letter-spacing: 1px;
    text-transform: uppercase;
}

.login-btn {
    width: 100%;
    height: 46px;
    font-size: 16px;
    letter-spacing: 4px;
    border-radius: 8px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border: none;
}
.login-btn:hover {
    background: linear-gradient(135deg, #5a6fd6, #6a3f8f);
}

.register-btn {
    width: 100%;
    height: 46px;
    font-size: 15px;
    border-radius: 8px;
    color: #667eea;
    border-color: #667eea;
}
.register-btn:hover {
    color: #fff;
    background: #667eea;
    border-color: #667eea;
}

.dialog-footer {
    text-align: right;
}

/* Element UI 深度定制 */
.login-form ::v-deep .el-input__inner {
    border-radius: 8px;
}

.login-form ::v-deep .el-form-item {
    margin-bottom: 22px;
}
</style>

<script>
import initMenu from "../utils/menus.js";
import { login, addUsers } from "../api/index.js";
export default {
    data() {
        const validatePass = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请输入密码'));
            } else if (value.length < 6) {
                callback(new Error('密码长度至少6位'));
            } else {
                callback();
            }
        };
        const validateCheckPwd = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请再次输入密码'));
            } else if (value !== this.usersForm.passWord) {
                callback(new Error('两次输入密码不一致'));
            } else {
                callback();
            }
        };
        const validateAge = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请输入年龄'));
            } else if (!/^\d{1,3}$/.test(value) || parseInt(value) < 0 || parseInt(value) > 120) {
                callback(new Error('年龄范围0-120'));
            } else {
                callback();
            }
        };
        const validatePhone = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请输入手机号'));
            } else if (!/^1[3-9]\d{9}$/.test(value)) {
                callback(new Error('手机号格式不正确'));
            } else {
                callback();
            }
        };
        return {
            loading: false,
            regLoading: false,
            showAddFlag: false,
            usersForm: {
                id: "", userName: "", passWord: "", checkPwd: "",
                name: "", gender: "", age: "", phone: "", address: "",
                type: 2, status: 1,
            },
            loginForm: { userName: "", passWord: "" },
            rules: {
                userName: [{ required: true, message: "请输入账号", trigger: "blur" }],
                passWord: [{ required: true, message: "请输入密码", trigger: "blur" }],
            },
            registerRules: {
                userName: [
                    { required: true, message: "请输入账号", trigger: "blur" },
                    { min: 4, max: 32, message: "账号长度4-32位", trigger: "blur" }
                ],
                passWord: [{ validator: validatePass, trigger: "blur" }],
                checkPwd: [{ validator: validateCheckPwd, trigger: "blur" }],
                name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
                age: [{ validator: validateAge, trigger: "blur" }],
                gender: [{ required: true, message: "请选择性别", trigger: "change" }],
                phone: [{ validator: validatePhone, trigger: "blur" }],
            },
        };
    },
    methods: {
        showAddWin() {
            this.$refs.usersForm && this.$refs.usersForm.resetFields();
            this.usersForm = {
                id: "", userName: "", passWord: "", checkPwd: "",
                name: "", gender: "", age: "", phone: "", address: "",
                type: 2, status: 1,
            };
            this.showAddFlag = true;
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (!valid) return false;
                this.loading = true;
                login(this.loginForm)
                    .then((res) => {
                        this.$store.commit("setToken", res.data);
                        sessionStorage.setItem("token", res.data);
                        initMenu(this.$router, this.$store);
                        this.$router.push("/index");
                    })
                    .catch(() => {
                        this.loading = false;
                    })
                    .finally(() => {
                        this.loading = false;
                    });
            });
        },
        addInfo() {
            this.$refs.usersForm.validate((valid) => {
                if (!valid) return;
                this.regLoading = true;
                addUsers({
                    userName: this.usersForm.userName,
                    passWord: this.usersForm.passWord,
                    name: this.usersForm.name,
                    gender: this.usersForm.gender,
                    age: this.usersForm.age,
                    phone: this.usersForm.phone,
                    address: this.usersForm.address,
                    type: 2,
                    status: 1,
                }).then((resp) => {
                    this.regLoading = false;
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
                        }).catch(() => {});
                    } else {
                        this.$message.warning(resp.msg);
                    }
                }).catch(() => {
                    this.regLoading = false;
                });
            });
        },
    },
};
</script>
