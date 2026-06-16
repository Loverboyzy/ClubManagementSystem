<template>
    <div class="index-container">
        <el-row :gutter="20">
            <el-col :xs="24" :sm="24" :md="8">
                <el-card class="user-card">
                    <div class="user-header">
                        <el-avatar :size="64" icon="el-icon-user"></el-avatar>
                        <h3 class="user-name">{{ loginUser.name || '未设置姓名' }}</h3>
                        <el-tag :type="getRoleType(loginUser.type)" size="small">
                            {{ getRoleText(loginUser.type) }}
                        </el-tag>
                    </div>
                    <el-divider></el-divider>
                    <div class="user-info">
                        <div class="info-item">
                            <span class="info-label">账号</span>
                            <span class="info-value">{{ loginUser.userName }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">性别</span>
                            <span class="info-value">{{ loginUser.gender || '-' }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">年龄</span>
                            <span class="info-value">{{ loginUser.age || '-' }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">电话</span>
                            <span class="info-value">{{ loginUser.phone || '-' }}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">地址</span>
                            <span class="info-value">{{ loginUser.address || '-' }}</span>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="24" :md="16">
                <el-card class="notice-card">
                    <template slot="header">
                        <span class="card-title"><i class="el-icon-bell"></i> 系统通知</span>
                    </template>
                    <div class="notice-list" v-if="sysNotices.length > 0">
                        <div class="notice-item" v-for="(item, index) in sysNotices" :key="index">
                            <div class="notice-time">{{ item.createTime }}</div>
                            <div class="notice-content">
                                <h4 class="notice-title">{{ item.title }}</h4>
                                <p class="notice-detail">{{ item.detail }}</p>
                            </div>
                        </div>
                    </div>
                    <el-empty v-else description="暂无通知" :image-size="80"></el-empty>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import { getLoginUser, getSysNoticeList } from "../../api";

export default {
    data() {
        return {
            loginUser: {},
            sysNotices: [],
        };
    },
    methods: {
        getRoleType(type) {
            const types = { 0: 'danger', 1: 'warning', 2: 'success' };
            return types[type] || 'info';
        },
        getRoleText(type) {
            const texts = { 0: '系统管理员', 1: '社团管理员', 2: '普通用户' };
            return texts[type] || '未知';
        },
    },
    mounted() {
        getLoginUser(this.$store.state.token).then((resp) => {
            this.loginUser = resp.data;
            this.$store.state.user = this.loginUser;
        });

        getSysNoticeList(this.$store.state.token).then((resp) => {
            this.sysNotices = resp.data;
        });
    },
};
</script>

<style scoped>
.index-container {
    padding: 10px;
}

.user-card {
    margin-bottom: 20px;
}

.user-header {
    text-align: center;
    padding: 10px 0;
}

.user-name {
    margin: 12px 0;
    font-size: 18px;
    color: #303133;
}

.user-info {
    padding: 0 10px;
}

.info-item {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
    border-bottom: none;
}

.info-label {
    color: #909399;
    font-size: 14px;
}

.info-value {
    color: #303133;
    font-size: 14px;
}

.notice-card {
    margin-bottom: 20px;
}

.card-title {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
}

.card-title i {
    margin-right: 8px;
    color: #409eff;
}

.notice-list {
    max-height: 500px;
    overflow-y: auto;
}

.notice-item {
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
}

.notice-item:last-child {
    border-bottom: none;
}

.notice-time {
    font-size: 12px;
    color: #909399;
    margin-bottom: 8px;
}

.notice-content {
    padding-left: 12px;
    border-left: 3px solid #409eff;
}

.notice-title {
    margin: 0 0 8px 0;
    font-size: 15px;
    color: #303133;
}

.notice-detail {
    margin: 0;
    font-size: 13px;
    color: #606266;
    line-height: 1.6;
}
</style>
