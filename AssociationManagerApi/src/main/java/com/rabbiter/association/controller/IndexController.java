package com.rabbiter.association.controller;

import com.rabbiter.association.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import com.rabbiter.association.entity.Notices;
import com.rabbiter.association.handle.CacheHandle;
import com.rabbiter.association.msg.R;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.service.NoticesService;
import com.rabbiter.association.utils.IDUtils;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/")
public class IndexController extends BaseController {

    private static final Logger Log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UsersService usersService;

    @Autowired
    private CacheHandle cacheHandle;

    @Autowired
    private NoticesService noticesService;

    // 登录频率限制：每个 IP 最多尝试 5 次，锁定 15 分钟
    private static final Map<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();
    private static final Map<String, Long> lockedIps = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME = 15 * 60 * 1000; // 15 分钟

    @GetMapping("/sys/notices")
    @ResponseBody
    public R getNoticeList(String token){

        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));
        if(ObjectUtils.isEmpty(user)) {
            return R.error("登录信息不存在，请重新登录");
        }

        if(user.getType() == 0){

            List<Notices> list = noticesService.getSysNotices();

            return R.successData(list);
        }else if(user.getType() == 1){

            List<Notices> list = noticesService.getManNotices(user.getId());

            return R.successData(list);
        }else{

            List<Notices> list = noticesService.getMemNotices(user.getId());

            return R.successData(list);
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public R login(String userName, String passWord, HttpSession session,
                   javax.servlet.http.HttpServletRequest request){

        // ---- 频率限制 ----
        String ip = request.getRemoteAddr();

        // 检查是否被锁定
        Long lockTime = lockedIps.get(ip);
        if (lockTime != null) {
            long remaining = LOCK_TIME - (System.currentTimeMillis() - lockTime);
            if (remaining > 0) {
                long minutes = remaining / 60000 + 1;
                return R.error("登录尝试次数过多，请 " + minutes + " 分钟后重试");
            }
            lockedIps.remove(ip);
            loginAttempts.remove(ip);
        }

        // 检查尝试次数
        AtomicInteger attempts = loginAttempts.computeIfAbsent(ip, k -> new AtomicInteger(0));
        if (attempts.incrementAndGet() > MAX_ATTEMPTS) {
            lockedIps.put(ip, System.currentTimeMillis());
            return R.error("登录尝试次数过多，请 15 分钟后重试");
        }

        Log.info("用户登录，用户名：{}", userName);

        Users user = usersService.getUserByUserName(userName);

        if(user == null) {
            return R.error("输入的用户名不存在");
        }else {
            // 密码验证
            if(!passWord.equals(user.getPassWord().trim())) {
                return R.error("输入的密码错误");
            }

            // 登录成功，清除频率限制记录
            loginAttempts.remove(ip);

            String token = IDUtils.makeIDByUUID();
            cacheHandle.addUserCache(token, user.getId());

            return R.success("登录成功", token);
        }
    }

    @RequestMapping("/exit")
    @ResponseBody
    public R exit(String token) {

        Log.info("用户退出系统并移除登录信息");

        cacheHandle.removeUserCache(token);

        return R.success();
    }

    @GetMapping("/info")
    @ResponseBody
    public R info(String token){

        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));

        return R.successData(user);
    }

    @PostMapping("/info")
    @ResponseBody
    public R info(Users user){

        Log.info("修改用户信息，{}", user);

        usersService.update(user);

        return R.success();
    }

    @RequestMapping("/checkPwd")
    @ResponseBody
    public R checkPwd(String oldPwd, String token) {

        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));

        if(oldPwd.equals(user.getPassWord())) {
            return R.success();
        }else {
            return R.warn("原始密码和输入密码不一致");
        }
    }

    @PostMapping("/pwd")
    @ResponseBody
    public R pwd(String token, String password) {

        Log.info("修改用户密码");

        Users user = usersService.getOne(cacheHandle.getUserInfoCache(token));

        user.setPassWord(password);
        usersService.update(user);

        return R.success();
    }
}
