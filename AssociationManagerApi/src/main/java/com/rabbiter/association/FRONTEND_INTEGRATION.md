# 社团管理系统 - Spring Boot 与 Vue 前后端接入文档

## 一、项目架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端 (Vue.js)                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │   页面组件    │  │   API接口    │  │  HTTP工具    │           │
│  │  views/*.vue │  │  api/index.js│  │  utils/http.js│           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTP请求 (REST API)
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                       后端 (Spring Boot)                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │  Controller  │  │   Service    │  │     DAO      │           │
│  │   控制器     │  │   业务逻辑    │  │   数据访问    │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
└────────────────────────┬────────────────────────────────────────┘
                         │ MyBatis-Plus
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                       数据库 (MySQL)                             │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │
│  │  users   │ │  teams   │ │activities│ │  notices │           │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 二、后端 Spring Boot 配置

### 2.1 配置文件位置

```
AssociationManagerApi - idea/
├── src/main/resources/
│   └── application.yml          # 主配置文件
├── pom.xml                      # Maven依赖配置
└── src/main/java/com/rabbiter/association/
    └── AssociationManagerApplication.java  # 启动类
```

### 2.2 application.yml 配置说明

```yaml
server:
  port: 9211                                    # 后端服务端口
  servlet:
    context-path: /association                   # 应用上下文路径（API前缀）

spring:
  datasource:
    username: root                               # 数据库用户名
    password: 123456                             # 数据库密码
    url: jdbc:mysql://localhost:3306/association_manager?zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
    driver-class-name: com.mysql.cj.jdbc.Driver  # MySQL驱动

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志输出
```

**API基础路径**：`http://localhost:9211/association`

### 2.3 pom.xml 主要依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| `spring-boot-starter-parent` | 2.3.4.RELEASE | Spring Boot 父POM |
| `spring-boot-starter-web` | - | Web开发（内嵌Tomcat） |
| `mybatis-plus-boot-starter` | 3.4.2 | MyBatis-Plus ORM框架 |
| `mysql-connector-java` | 8.0.17 | MySQL数据库驱动 |
| `druid` | 1.2.1 | Alibaba连接池 |
| `ehcache` | 3.8.1 | 缓存框架（用户会话） |
| `fastjson` | 1.2.72 | JSON处理 |
| `spring-boot-devtools` | - | 热部署支持 |

### 2.4 启动类核心配置

```java
@SpringBootApplication
@MapperScan({"com.rabbiter.association.dao"})  # MyBatis扫描DAO包
@EnableCaching                                  # 启用缓存
public class AssociationManagerApplication implements WebMvcConfigurer {

    // JSON转换器配置
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() { ... }

    // MySQL分页插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() { ... }

    // CORS跨域配置
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
                        .maxAge(3600);
            }
        };
    }
}
```

**关键配置说明**：
- `@MapperScan` 自动扫描DAO接口
- `@EnableCaching` 启用EhCache缓存
- CORS配置允许前端跨域访问

---

## 三、前端 Vue 配置

### 3.1 配置文件位置

```
AssociationManagerVue/
├── vue.config.js              # Vue CLI配置
├── src/main.js                # Vue入口文件
├── src/utils/
│   ├── http.js               # HTTP请求封装
│   ├── menus.js              # 动态菜单配置
│   └── initialize.js         # 初始化配置
├── src/api/
│   └── index.js              # API接口定义
└── src/
    └── store/                # Vuex状态管理
```

### 3.2 vue.config.js 配置

```javascript
module.exports = {
  devServer: {
    port: 9212    // 前端开发服务器端口
  },
  lintOnSave: false
}
```

**前端访问地址**：`http://localhost:9212`

### 3.3 HTTP请求封装 (http.js)

```javascript
import axios from 'axios'
import qs from 'qs'
import { Message } from 'element-ui'

// 设置请求头
axios.defaults.headers['Content-Type'] = 'application/x-www-form-urlencoded';

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:9211/association',  // 后端API基础URL
  timeout: 15000                                 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(config => {
  if(config.method === "post"){
    config.data = qs.stringify(config.data, { indices: false });
  }
  return config;
}, error => {
  Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(
  resp => {
    if (resp.data.code != 2) {  // code=2表示系统错误
      return resp.data;
    } else {
      Message({
        message: resp.data.msg,
        type: 'error',
        center: true
      });
      return Promise.reject(resp);
    }
  },
  error => {
    // 错误处理
    return Promise.reject(error);
  }
)

export default service
```

### 3.4 API接口定义 (api/index.js)

```javascript
import http from "../utils/http.js";

// ==================== 登录认证 ====================

// 用户登录
export function login(param){
  return http.post('/login', param);
}

// 用户退出
export function exit(token){
  return http.get('/exit', {params: {token: token}});
}

// 获取当前登录用户信息
export function getLoginUser(token){
  return http.get('/info', {params: {token: token}});
}

// ==================== 用户管理 ====================

// 分页查询用户
export function getPageUsers(pageIndex, pageSize, userName, name, phone){
  return http.get('/users/page',
    {params: {pageIndex, pageSize, userName, name, phone}});
}

// 添加用户
export function addUsers(params){
  return http.post('/users/add', params);
}

// 修改用户
export function updUsers(params){
  return http.post('/users/upd', params);
}

// 删除用户
export function delUsers(id){
  return http.post('/users/del', {id: id});
}

// ==================== 社团管理 ====================

// 获取全部社团
export function getAllTeamList(){
  return http.get('/teams/all');
}

// 分页查询社团
export function getPageTeams(pageIndex, pageSize, token, name, typeId){
  return http.get('/teams/page',
    {params: {pageIndex, pageSize, token, name, typeId}});
}

// 添加社团
export function addTeams(params){
  return http.post('/teams/add', params);
}

// ... 其他接口类似
```

---

## 四、完整API接口列表

### 4.1 认证相关接口

| 方法 | 路径 | 参数 | 说明 |
|------|------|------|------|
| POST | `/login` | userName, passWord | 用户登录 |
| GET | `/exit` | token | 退出登录 |
| GET | `/info` | token | 获取当前用户信息 |
| POST | `/info` | 用户信息 | 修改个人信息 |
| POST | `/pwd` | token, password | 修改密码 |
| GET | `/checkPwd` | token, oldPwd | 验证原密码 |
| GET | `/sys/notices` | token | 获取通知列表 |

### 4.2 业务管理接口

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户 | GET | `/users/page` | 分页查询用户 |
| 用户 | POST | `/users/add` | 添加用户 |
| 用户 | POST | `/users/upd` | 修改用户 |
| 用户 | POST | `/users/del` | 删除用户 |
| 社团 | GET | `/teams/all` | 获取全部社团 |
| 社团 | GET | `/teams/page` | 分页查询社团 |
| 社团 | POST | `/teams/add` | 添加社团 |
| 社团 | POST | `/teams/upd` | 修改社团 |
| 社团 | POST | `/teams/del` | 删除社团 |
| 类型 | GET | `/teamTypes/all` | 获取全部类型 |
| 类型 | GET | `/teamTypes/page` | 分页查询类型 |
| 类型 | POST | `/teamTypes/add` | 添加类型 |
| 类型 | POST | `/teamTypes/upd` | 修改类型 |
| 类型 | POST | `/teamTypes/del` | 删除类型 |
| 成员 | GET | `/members/page` | 分页查询成员 |
| 成员 | POST | `/members/add` | 添加成员 |
| 成员 | POST | `/members/upd` | 修改成员 |
| 成员 | POST | `/members/del` | 删除成员 |
| 活动 | GET | `/activities/page` | 分页查询活动 |
| 活动 | POST | `/activities/add` | 发布活动 |
| 活动 | POST | `/activities/upd` | 修改活动 |
| 活动 | POST | `/activities/del` | 删除活动 |
| 报名 | GET | `/activeLogs/list` | 获取活动报名列表 |
| 报名 | POST | `/activeLogs/add` | 报名活动 |
| 通知 | GET | `/notices/page` | 分页查询通知 |
| 通知 | POST | `/notices/add` | 发布通知 |
| 通知 | POST | `/notices/upd` | 修改通知 |
| 通知 | POST | `/notices/del` | 删除通知 |
| 申请 | GET | `/applyLogs/page` | 分页查询申请 |
| 申请 | POST | `/applyLogs/add` | 提交入团申请 |
| 申请 | POST | `/applyLogs/upd` | 审批申请 |
| 申请 | POST | `/applyLogs/del` | 删除申请 |
| 缴费 | GET | `/payLogs/page` | 分页查询缴费记录 |
| 缴费 | POST | `/payLogs/add` | 添加缴费记录 |
| 缴费 | POST | `/payLogs/upd` | 修改缴费记录 |
| 缴费 | POST | `/payLogs/del` | 删除缴费记录 |

---

## 五、请求与响应格式

### 5.1 请求格式

**GET 请求**：
```
GET http://localhost:9211/association/users/page?pageIndex=1&pageSize=10&userName=admin
```

**POST 请求**（表单格式）：
```
POST http://localhost:9211/association/users/add
Content-Type: application/x-www-form-urlencoded

id=123&userName=test&name=测试用户
```

### 5.2 响应格式

**成功响应**：
```json
{
  "code": 0,
  "msg": "处理成功",
  "data": {
    "id": "123",
    "userName": "admin",
    "name": "管理员"
  }
}
```

**分页响应**：
```json
{
  "code": 0,
  "msg": "处理成功",
  "data": {
    "pageIndex": 1,
    "pageSize": 10,
    "pageTotal": 5,
    "count": 45,
    "data": [...]
  }
}
```

**警告响应**：
```json
{
  "code": 1,
  "msg": "用户账号已存在，请重新输入"
}
```

**错误响应**：
```json
{
  "code": 2,
  "msg": "系统异常"
}
```

### 5.3 响应码说明

| code | 类型 | 说明 |
|------|------|------|
| 0 | SUCCESS | 处理成功 |
| 1 | WARN | 业务警告/逻辑错误 |
| 2 | ERROR | 系统异常 |

---

## 六、用户认证机制

### 6.1 登录流程

```
┌─────────┐     POST /login      ┌─────────────┐
│  前端   │ ──────────────────▶  │   后端      │
│         │                      │             │
│         │ ◀─────────────────── │  生成token  │
└─────────┘    {code, msg, data} └─────────────┘
                           │
                           ▼
                    存储token到sessionStorage
```

### 6.2 Token使用

前端在每次请求中通过 `token` 参数传递用户凭证：

```javascript
// 示例：获取用户列表
export function getPageUsers(pageIndex, pageSize, userName, name, phone){
  return http.get('/users/page',
    {params: {pageIndex, pageSize, userName, name, phone}});
}

// 在业务组件中调用
import { getPageUsers } from '@/api';

export default {
  methods: {
    loadData() {
      getPageUsers(this.currentPage, this.pageSize, this.userName, this.name, this.phone)
        .then(res => {
          this.tableData = res.data.data.data;
        });
    }
  }
}
```

**注意**：token 存储在前端 `sessionStorage` 中，退出登录时清除。

### 6.3 用户权限类型

| type值 | 角色 | 说明 |
|--------|------|------|
| 0 | 系统管理员 | 拥有所有权限 |
| 1 | 社团管理员 | 管理自己创建的社团 |
| 2 | 普通成员 | 仅查看和操作自己的数据 |

---

## 七、前后端联调步骤

### 7.1 环境准备

**1. 启动MySQL数据库**
```bash
# 确保MySQL服务已启动，端口3306
# 创建数据库 association_manager
```

**2. 启动后端服务**
```bash
cd AssociationManagerApi - idea
mvn spring-boot:run
# 或在IDE中直接运行 AssociationManagerApplication.main()
```

**3. 启动前端服务**
```bash
cd AssociationManagerVue
npm run serve
```

### 7.2 联调检查清单

| 检查项 | 预期结果 |
|--------|----------|
| 后端服务启动 | 控制台显示 `Started AssociationManagerApplication in x seconds` |
| 前端服务启动 | 浏览器访问 `http://localhost:9212` 正常显示 |
| 登录功能 | 输入正确账号密码可登录 |
| 数据查询 | 可正常获取列表数据 |
| 数据新增 | 添加数据后数据库有记录 |
| 数据修改 | 修改后数据库数据更新 |
| 数据删除 | 删除后数据库记录移除 |

### 7.3 常见问题处理

**问题1：跨域错误**
```
Access to XMLHttpRequest at 'http://localhost:9211/association/login'
from origin 'http://localhost:9212' has been blocked by CORS policy
```
**解决**：后端已配置CORS，确保 `AssociationManagerApplication` 中的 `corsConfigurer` 生效

**问题2：数据库连接失败**
```
Failed to obtain JDBC Connection
```
**解决**：检查 application.yml 中的数据库配置是否正确，确认MySQL服务已启动

**问题3：token无效**
```
登录信息不存在，请重新登录
```
**解决**：检查前端 http.js 中的 baseURL 是否正确，确认token已存储

**问题4：请求超时**
```
timeout of 15000ms exceeded
```
**解决**：检查后端服务是否正常运行，或增加 timeout 时间

---

## 八、开发建议

### 8.1 后端开发

```java
// 1. 新增实体类
//    在 entity/ 目录下创建 Xxx.java
//    使用 @TableName, @TableId, @TableField 注解

// 2. 新增DAO接口
//    在 dao/ 目录下创建 XxxDao.java
//    继承 BaseMapper<Xxx>

// 3. 新增Service接口和实现
//    在 service/ 目录下创建 XxxService.java
//    在 service/impl/ 目录下创建 XxxServiceImpl.java

// 4. 新增Controller
//    在 controller/ 目录下创建 XxxController.java
//    定义REST API接口
```

### 8.2 前端开发

```javascript
// 1. 新增API接口
//    在 api/index.js 中添加导出函数
export function getXxxList(params) {
  return http.get('/xxx/page', {params});
}

// 2. 新增页面组件
//    在 views/pages/ 目录下创建 Xxx.vue

// 3. 配置菜单权限
//    在 utils/menus.js 中根据用户类型添加菜单
```

### 8.3 接口测试工具

推荐使用以下工具测试后端API：
- **Postman**：功能强大的API测试工具
- **Apifox**：国产API协作平台
- **curl**：命令行HTTP请求工具

**curl 示例**：
```bash
# 登录
curl -X POST http://localhost:9211/association/login \
  -d "userName=admin&passWord=123456"

# 查询用户列表
curl "http://localhost:9211/association/users/page?pageIndex=1&pageSize=10"
```

---

## 九、端口与地址汇总

| 应用 | 地址 | 端口 | 说明 |
|------|------|------|------|
| MySQL | localhost | 3306 | 数据库 |
| 后端API | localhost | 9211 | Spring Boot服务 |
| 前端页面 | localhost | 9212 | Vue开发服务器 |
| API基础路径 | localhost:9211/association | - | 所有API的前缀 |

---

## 十、项目技术栈总结

### 后端技术栈
- **框架**：Spring Boot 2.3.4
- **ORM**：MyBatis-Plus 3.4.2
- **数据库**：MySQL 8.0
- **连接池**：Druid 1.2.1
- **缓存**：Ehcache 3.8.1
- **JSON**：FastJSON 1.2.72
- **日志**：Log4j2
- **构建工具**：Maven

### 前端技术栈
- **框架**：Vue 2.x
- **UI库**：Element-UI
- **路由**：Vue Router
- **状态管理**：Vuex
- **HTTP客户端**：Axios
- **图标**：Font Awesome
- **构建工具**：Vue CLI
