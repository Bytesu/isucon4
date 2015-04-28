/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Manabu Matsuzaki
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

package net.isucon.isucon4.repository;

import me.geso.tinyorm.TinyORM;
import net.isucon.isucon4.row.LoginLog;
import net.isucon.isucon4.row.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class LoginRepository {

    @Inject
    private TinyORM orm;

    public Optional<User> findUserByLogin(String login) {
        return orm.singleBySQL(User.class,
                "SELECT id, login, password_hash AS passwordHash, salt" +
                        " FROM users WHERE login = ?",
                Collections.singletonList(login));
    }

    public long countBannedIp(String ip) {
        return orm.queryForLong(
                "SELECT COUNT(1) AS failures FROM login_log" +
                        " WHERE ip = ? AND id > IFNULL((select id from login_log where ip = ? AND succeeded = 1 ORDER BY id DESC LIMIT 1), 0)",
                Arrays.asList(ip, ip))
                .orElse(0L);
    }

    public long countLockedUser(int userId) {
        return orm.queryForLong(
                "SELECT COUNT(1) AS failures FROM login_log" +
                        " WHERE user_id = ? AND id > IFNULL((select id from login_log where user_id = ? AND succeeded = 1 ORDER BY id DESC LIMIT 1), 0)",
                Arrays.asList(userId, userId))
                .orElse(0L);
    }

    public LoginLog findLoginLogByUserId(int userId) {
        List<LoginLog> loginLogs = orm.searchBySQL(
                LoginLog.class,
                "SELECT id, created_at AS createdAt, user_id AS userId, login, ip, succeeded" +
                        " FROM login_log WHERE succeeded = 1 AND user_id = ? ORDER BY id DESC LIMIT 2",
                Collections.singletonList(userId));

        return loginLogs.get(loginLogs.size() - 1);
    }
}
