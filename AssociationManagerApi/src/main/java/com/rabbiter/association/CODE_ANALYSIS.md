# 社团管理系统 (Association Manager) 代码分析文档

## 项目概述

**项目名称**：社团管理系统后端 API

**技术栈**：Spring Boot + MyBatis-Plus + MySQL + EhCache

**包结构**：`com.rabbiter.association`

---

## 一、entity 包 - 数据实体类

负责定义数据库表对应的 Java 实体类，每个类对应一张数据库表。

| 类名 | 对应表名 | 功能描述 |
|------|----------|----------|
| `Users` | users | 系统用户表：存储用户账号、密码、姓名、性别、年龄、电话、地址、用户类型（0-管理员/1-社团管理员/2-普通成员） |
| `Teams` | teams | 社团信息表：存储社团名称、建立时间、人数、团长ID、社团类型ID |
| `TeamTypes` | team_types | 社团类型表：存储社团分类信息（如：学术类、艺术类、体育类等） |
| `Members` | members | 社团成员表：存储用户与社团的关联关系（谁加入了哪个社团） |
| `Activities` | activities | 活动信息表：存储活动名称、概述、详情、要求、报名人数、活动时间、发布社团 |
| `ActiveLogs` | active_logs | 活动报名记录表：记录用户报名活动的信息 |
| `Notices` | notices | 通知公告表：存储社团发布的通知标题、详情、发布时间 |
| `ApplyLogs` | apply_logs | 入团申请记录表：记录用户申请加入社团的信息及审批状态 |
| `PayLogs` | pay_logs | 缴费记录表：记录用户向社团缴纳费用的情况 |

**实体类特性**：
- 使用 `@TableName` 指定对应的数据库表名
- 使用 `@TableId` 定义主键字段
- 使用 `@TableField` 映射数据库列
- 实现 `Serializable` 接口支持序列化

---

## 二、dao 包 - 数据访问层

负责数据库操作，继承 MyBatis-Plus 的 `BaseMapper` 接口。

| 类名 | 对应实体 | 功能描述 |
|------|----------|----------|
| `UsersDao` | Users | 用户数据访问，提供增删改查基础操作 |
| `TeamsDao` | Teams | 社团数据访问 |
| `TeamTypesDao` | TeamTypes | 社团类型数据访问 |
| `MembersDao` | Members | 社团成员数据访问 |
| `ActivitiesDao` | Activities | 活动数据访问 |
| `ActiveLogsDao` | ActiveLogs | 活动报名记录数据访问 |
| `NoticesDao` | Notices | 通知公告数据访问 |
| `ApplyLogsDao` | ApplyLogs | 入团申请记录数据访问 |
| `PayLogsDao` | PayLogs | 缴费记录数据访问 |

**特点**：
- 每个 DAO 接口继承 `BaseMapper<T>`，自动获得基础 CRUD 方法
- 使用 `@Repository` 注解标记为 Spring Bean
- 通过 `@MapperScan` 配置自动扫描

---

## 三、service 包 - 业务逻辑层

负责实现业务逻辑，分为接口和实现类两部分。

### 3.1 BaseService 抽象接口

定义所有 Service 需要实现的通用方法：

```java
public interface BaseService<T, Pk> {
    void add(T t);      // 添加数据
    void update(T t);   // 更新数据
    void delete(T t);   // 删除数据
    T getOne(Pk id);    // 根据ID查询
}
```

### 3.2 各模块 Service 接口

| 接口名 | 实现类 | 功能描述 |
|--------|--------|----------|
| `UsersService` | UsersServiceImpl | 用户管理：用户CRUD、用户名查询、删除前关联检查、分页查询 |
| `TeamsService` | TeamsServiceImpl | 社团管理：社团CRUD、获取全部社团、根据团长ID查询 |
| `TeamTypesService` | TeamTypesServiceImpl | 社团类型管理：类型CRUD、分页查询、删除前关联检查 |
| `MembersService` | MembersServiceImpl | 成员管理：成员CRUD、分页查询（管理员/全部）、判断是否团长 |
| `ActivitiesService` | ActivitiesServiceImpl | 活动管理：活动CRUD、分页查询（全部/按用户） |
| `ActiveLogsService` | ActiveLogsServiceImpl | 报名记录管理：报名CRUD、根据活动ID查询、判断是否重复报名 |
| `NoticesService` | NoticesServiceImpl | 通知管理：通知CRUD、分页查询（全部/按用户） |
| `ApplyLogsService` | ApplyLogsServiceImpl | 申请记录管理：申请CRUD、分页查询（全部/管理员/用户）、判断是否重复申请 |
| `PayLogsService` | PayLogsServiceImpl | 缴费记录管理：缴费CRUD、分页查询（全部/管理员/用户） |

**事务管理**：
- 使用 `@Transactional` 注解保证数据一致性
- `readOnly = true` 用于只读查询，提升性能

---

## 四、controller 包 - REST API 控制器

负责处理 HTTP 请求，调用 Service 层业务逻辑，返回 JSON 响应。

### 4.1 BaseController 抽象类

```java
public abstract class BaseController {
    // 提供获取/设置会话用户的方法
    // 全局异常处理方法
}
```

### 4.2 各模块 Controller

| Controller | 请求路径 | 主要功能 |
|------------|----------|----------|
| `IndexController` | `/` | 系统首页、登录、退出、个人信息修改、密码修改、通知列表获取 |
| `UsersController` | `/users` | 用户管理：用户信息查询、分页查询、添加、修改、删除 |
| `TeamsController` | `/teams` | 社团管理：社团信息查询、全部社团获取、按管理员查询、分页查询、添加、修改、删除 |
| `TeamTypesController` | `/teamTypes` | 社团类型管理：类型信息查询、全部类型获取、分页查询、添加、修改、删除 |
| `MembersController` | `/members` | 成员管理：成员信息查询、分页查询（按权限）、添加、修改、删除 |
| `ActivitiesController` | `/activities` | 活动管理：活动信息查询、分页查询（按权限）、添加、修改、删除 |
| `ActiveLogsController` | `/activeLogs` | 报名记录管理：记录查询、按活动ID获取列表、报名、修改、删除 |
| `NoticesController` | `/notices` | 通知管理：通知信息查询、分页查询（按权限）、添加、修改、删除 |
| `ApplyLogsController` | `/applyLogs` | 申请记录管理：申请信息查询、分页查询（按权限）、申请入团、修改、删除 |
| `PayLogsController` | `/payLogs` | 缴费记录管理：缴费信息查询、分页查询（按权限）、添加、修改、删除 |

### 4.3 API 请求方法规范

| HTTP方法 | 功能 | 示例 |
|----------|------|------|
| `GET` | 查询 | `/users/info?id=xxx` / `/users/page?pageIndex=1&pageSize=10` |
| `POST` | 添加/修改 | `/users/add` / `/users/upd` |
| `POST` | 删除 | `/users/del?id=xxx` |

### 4.4 用户权限类型

| type值 | 角色 | 权限说明 |
|--------|------|----------|
| 0 | 系统管理员 | 可查看和管理所有数据 |
| 1 | 社团管理员 | 可管理自己创建的社团相关数据 |
| 2 | 普通成员 | 仅可查看和操作自己的数据 |

---

## 五、handle 包 - 处理器/拦截器

### 5.1 CacheHandle 缓存处理类

负责用户登录信息的缓存管理：

```java
@Component
public class CacheHandle {
    // 获取用户缓存
    Cache getUserCache();

    // 存储登录用户信息 (token -> userId)
    void addUserCache(String key, Object val);

    // 移除缓存登录用户信息 (退出登录)
    void removeUserCache(String key);

    // 获取缓存的登录用户ID
    String getUserInfoCache(String key);
}
```

**功能说明**：
- 使用 Spring Cache 机制管理用户会话
- 用户登录时生成 token 存入缓存
- 通过 token 可获取对应用户信息
- 退出登录时清除缓存

### 5.2 GlobalExceptionHandler 全局异常处理器

捕获并处理系统异常，返回友好的错误信息：

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // 处理数据库连接异常
    // 处理SQL语法异常
    // 处理表不存在异常
    // 处理数据库不存在异常
}
```

**异常处理策略**：
- 将技术性异常转换为友好的错误提示
- 避免直接暴露系统内部错误信息

---

## 六、msg 包 - 消息封装类

### 6.1 R 响应结果类

统一的 API 响应格式，继承 `HashMap<String, Object>`：

```java
public class R extends HashMap<String, Object> {
    // 响应码常量
    public static final Integer SUCCESS_CODE = 0;  // 成功
    public static final Integer WARN_CODE = 1;     // 警告
    public static final Integer ERROR_CODE = 2;    // 错误

    // 工厂方法
    R.success();                    // 成功默认响应
    R.success(String msg, Object data);  // 成功带消息和数据
    R.successData(Object data);     // 成功仅返回数据
    R.warn();                       // 警告默认响应
    R.warn(String msg);             // 警告带消息
    R.error();                      // 错误默认响应
    R.error(String msg);            // 错误带消息
}
```

**响应格式示例**：
```json
{
    "code": 0,
    "msg": "处理成功",
    "data": {...}
}
```

### 6.2 PageData 分页数据类

封装分页查询结果：

```java
public class PageData {
    private Long pageIndex;   // 当前页码
    private Long pageSize;    // 每页数据量
    private Long pageTotal;   // 总页数
    private Long count;       // 总记录数
    private List<Map<String, Object>> data;  // 数据列表
}
```

**响应格式示例**：
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

---

## 七、utils 包 - 工具类

### 7.1 DateUtils 日期处理工具类

```java
public class DateUtils {
    // 日期格式常量
    DATETIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    YYYY_MM_DD = "yyyy-MM-dd";
    YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    // 格式化日期
    String formatDateTime(Date date, String format);

    // 解析日期
    Date parseDate(String date, String format);

    // 获取当前时间戳
    long getCurrent();

    // 获取当前系统时间（指定格式）
    String getNowDate(String format);

    // 获取当前系统时间（默认格式）
    String getNowDate();
}
```

### 7.2 IDUtils ID生成工具类

```java
public class IDUtils {
    // 使用 UUID 生成32位随机ID
    String makeIDByUUID();

    // 使用时间戳生成13位数字ID
    String makeIDByCurrent();
}
```

### 7.3 StringUtils 字符串处理工具类

```java
public class StringUtils {
    // 判断字符串是否为NULL
    Boolean isNull(String str);

    // 判断字符串是否不为NULL
    Boolean isNotNull(String str);

    // 判断字符串是否为NULL或空
    Boolean isNullOrEmpty(String str);

    // 判断字符串是否不为NULL或空
    Boolean isNotNullOrEmpty(String str);

    // 检查字符串是否包含指定子串
    boolean isExit(String str, String flag);

    // 获取字符串长度
    int length(String str);
}
```

### 7.4 DataSourceConfiguration 数据源配置类

```java
@Configuration
public class DataSourceConfiguration {
    // 配置 Druid 数据库连接池
    // 自定义异常处理器
}
```

---

## 八、系统功能模块总结

### 8.1 用户认证模块
- 用户登录：`POST /login`
- 用户退出：`GET /exit`
- 获取个人信息：`GET /info`
- 修改个人信息：`POST /info`
- 修改密码：`POST /pwd`
- 验证密码：`POST /checkPwd`

### 8.2 用户管理模块
- 分页查询用户列表
- 根据ID查询用户信息
- 添加新用户
- 修改用户信息
- 删除用户（需检查关联性）

### 8.3 社团管理模块
- 查看全部社团
- 查看自己管理的社团
- 分页查询社团信息
- 创建新社团
- 修改社团信息
- 删除社团

### 8.4 社团类型模块
- 查看全部类型
- 分页查询类型
- 添加新类型
- 修改类型信息
- 删除类型（需检查关联性）

### 8.5 成员管理模块
- 分页查询成员（按权限）
- 添加成员
- 修改成员信息
- 删除成员（团长不可删除）

### 8.6 活动管理模块
- 分页查询活动（按权限）
- 发布新活动
- 修改活动信息
- 删除活动

### 8.7 报名管理模块
- 查看活动报名列表
- 报名活动（防重复报名）
- 修改报名记录
- 删除报名记录

### 8.8 通知管理模块
- 分页查询通知（按权限）
- 发布新通知
- 修改通知信息
- 删除通知

### 8.9 申请管理模块
- 分页查询申请（按权限）
- 申请加入社团（防重复申请）
- 修改申请状态
- 删除申请

### 8.10 缴费管理模块
- 分页查询缴费记录（按权限）
- 添加缴费记录
- 修改缴费记录
- 删除缴费记录

---

## 九、数据流图

```
客户端请求
    ↓
Controller (接收HTTP请求)
    ↓
Service (业务逻辑处理)
    ↓
DAO (数据库操作)
    ↓
MySQL 数据库
```

**响应流程**：
```
数据库结果
    ↓
DAO → Service → Controller
    ↓
R/PageData 封装
    ↓
JSON 响应给客户端
```

---

## 十、注意事项

1. **用户身份验证**：除登录接口外，其他接口均需通过 `token` 参数验证用户身份
2. **权限控制**：不同用户类型（0-管理员/1-社团管理员/2-普通成员）可见数据范围不同
3. **缓存机制**：用户登录信息存储在 EhCache 中，退出时清除
4. **异常处理**：全局异常处理器捕获并转换数据库异常为友好提示
5. **ID生成**：用户token使用UUID，数据库记录ID使用时间戳
6. **分页查询**：所有列表查询均支持分页，使用 MyBatis-Plus 分页插件
