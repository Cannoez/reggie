package com.zeal.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zeal.reggie.common.R;
import com.zeal.reggie.model.pojo.User;
import com.zeal.reggie.service.UserService;
import com.zeal.reggie.util.SMSUtils;
import com.zeal.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @version: java version 1.8
 * @author: zeal
 * @description:
 * @date: 2022-06-27 0:19
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody  User user,HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //需要将生成的验证码保存到session
            //session.setAttribute(phone,code);

            //将生成的验证码缓存到redis中,有效期五分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机短信发送成功");
        }
        return R.error("手机短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //Session中获取保存的验证码
        //Object codeInSession = session.getAttribute(phone);
        //Redis中获取验证码
        Object codeInRedis = redisTemplate.opsForValue().get(phone);
        //进行验证码的比对
        if (codeInRedis!=null&&codeInRedis.equals(code)){
            //如果能够比对成功,说明登录成功

            //判断当前手机号对应的用户是否为新用户,如果是就自动完成注册
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //如果用户登录成功,删除Redis缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败");
    }

    /**
     * 移动端用户退出登录
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        session.invalidate();
        return R.success("退出成功");
    }
}
