package ee.smkv.leader;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2023 Andrei Samkov
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class LeaderElectionProperties {
    @Value("${leader-election-jdbc.table-name:LEADER}")
    private String leaderTableName = "LEADER";
    @Value("${leader-election-jdbc.field-name:NAME}")
    private String leaderNameFiled = "NAME";
    @Value("${leader-election-jdbc.field-until:UNTIL}")
    private String leaderUntilFiled = "UNTIL";
    @Value("${leader-election-jdbc.name:}")
    private String leaderName = "";
    @Value("${leader-election-jdbc.pool-interval:5000}")
    private long poolInterval = 5000;
    @Value("${leader-election-jdbc.lock-interval:6000}")
    private long lockInterval = 5000;


    public String getLeaderTableName() {
        return leaderTableName;
    }

    public String getLeaderNameFiled() {
        return leaderNameFiled;
    }

    public String getLeaderUntilFiled() {
        return leaderUntilFiled;
    }

    public String getLeaderName() {
        if (leaderName == null || leaderName.isEmpty()) {
            leaderName = UUID.randomUUID().toString();
        }
        return leaderName;
    }

    public long getPoolInterval() {
        return poolInterval;
    }

    public long getLockInterval() {
        return lockInterval;
    }
}
