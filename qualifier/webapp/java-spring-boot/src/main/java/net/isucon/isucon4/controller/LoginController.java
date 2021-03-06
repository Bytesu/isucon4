/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Manabu Matsuzaki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.isucon.isucon4.controller;

import com.google.common.base.Strings;
import net.isucon.isucon4.exception.BusinessException;
import net.isucon.isucon4.model.Session;
import net.isucon.isucon4.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class LoginController {

    @Autowired
    Session session;

    @Autowired
    LoginService loginService;

    @RequestMapping(method = RequestMethod.GET)
    String index(Model model) {
        return "index";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    String login(@RequestParam String login,
                 @RequestParam String password,
                 RedirectAttributes attributes,
                 HttpServletRequest request) {

        String ip = getRemoteIp(request);

        try {
            session.setLoginLog(loginService.attemptLogin(login, password, ip));
            return "redirect:/mypage";
        } catch (BusinessException e) {
            attributes.addFlashAttribute("login", login);
            attributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:/";
        }
    }

    String getRemoteIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (!Strings.isNullOrEmpty(xForwardedFor)) {
            return xForwardedFor;
        } else {
            return request.getRemoteAddr();
        }
    }
}
