package com.coolbeevip.java.lock.optimistic.aba;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 并发编程中 ABA 问题的理论背景
 * <p>
 * 例如：
 * 假设一个活动读取一些共享内存 (A)，以准备更新它。
 * 然后，另一个活动临时修改该共享内存 (B)，然后将其恢复 (A)。
 * 之后，一旦第一个活动执行了比较和交换，它将看起来好像没有进行任何更改，从而使检查的完整性无效。
 * 虽然在许多情况下这不会导致问题，但有时，A 并不像我们想象的那样等于 A。让我们看看这在实践中的表现
 * <p>
 * 为了通过一个实际例子来演示这个问题，让我们考虑一个简单的银行账户类，其中一个整数变量保存实际余额的金额。我们还有两种功能：一种用于取款，一种用于存款。这些操作使用 CAS 来减少和增加账户余额。
 * <p>
 * 过程：
 * 当线程 1 想要提取一些钱时，它会读取实际余额以使用该值来比较 CAS 操作中的金额。然而，出于某种原因，线程 1 有点慢——也许它被阻塞了。
 * 同时，线程 2 在线程 1 挂起时使用相同的机制对帐户执行两个操作。首先，它更改已被线程 1 读取的原始值，但随后又将其更改回原始值。
 * 一旦线程1恢复，就好像什么都没变，CAS就成功了（这就产生了 ABA 问题）
 * <p>
 * https://www.baeldung.com/cs/aba-concurrency
 */

public class AccountTest {

  private Account account;

  @Before
  public void setUp() {
    account = new Account();
  }

  /**
   * 账户初始化测试
   */
  @Test
  public void zeroBalanceInitializationTest() {
    assertThat(0, Matchers.is(account.getBalance()));
    assertThat(0, Matchers.is(account.getTransactionCount()));
    assertThat(0, Matchers.is(account.getCurrentThreadCASFailureCount()));
  }

  /**
   * 账户存款测试
   */
  @Test
  public void depositTest() {
    final int moneyToDeposit = 50;
    assertThat(account.deposit(moneyToDeposit), Matchers.is(true));
    assertThat(moneyToDeposit, Matchers.is(account.getBalance()));
  }

  /**
   * 账户取款测试
   */
  @Test
  public void withdrawTest() throws InterruptedException {
    final int defaultBalance = 50;
    final int moneyToWithdraw = 20;
    account.deposit(defaultBalance);
    assertThat(account.withdraw(moneyToWithdraw), Matchers.is(true));
    assertThat(defaultBalance - moneyToWithdraw, Matchers.is(account.getBalance()));
  }

  /**
   * 账户 ABA 问题测试
   */
  @Test
  public void abaProblemTest() throws InterruptedException {
    final int defaultBalance = 10;

    /**
     * 存入 50
     */
    assertThat(0, Matchers.is(account.getTransactionCount()));
    assertThat(0, Matchers.is(account.getCurrentThreadCASFailureCount()));
    account.deposit(defaultBalance);
    assertThat(1, Matchers.is(account.getTransactionCount())); // 交易计数器 1

    Thread thread1 = new Thread(() -> {
      // 此取现方法会先获取账户余额 10 元，然后等待thread 2 执行完 -10 和 +10 后在执行比较更新
      // 此时会失败，因为当前账户的 10 元已经不是最初的 10 元了
      assertThat(account.withdraw(10), Matchers.is(false));
      /**
       * 取现成功，产生 ABA 问题
       * 此时应该取现失败，因为此时账户余额中的 10 已经并不是最初的 10，而是线程2完成了（10-10+10）后的 10
       */
      assertThat(1, Matchers.is(account.getCurrentThreadCASFailureCount()));
      assertThat(3, Matchers.is(account.getTransactionCount())); // 交易计数器依旧是 3，因为本次支取失败

    }, "thread1");

    Thread thread2 = new Thread(() -> {
      // 线程2 取出 10 元后在存入 10 元，账户余额恢复为 10 元
      assertThat(account.withdraw(10), Matchers.is(true));
      assertThat(account.deposit(10), Matchers.is(true));
      assertThat(3, Matchers.is(account.getTransactionCount())); // 交易计数器 3
      assertThat(defaultBalance, Matchers.is(account.getBalance()));
      assertThat(0, Matchers.is(account.getCurrentThreadCASFailureCount()));
    }, "thread2");

    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();

    /**
     * 线程1取款失败（比较更新失败）
     */
    assertThat(defaultBalance, Matchers.is(account.getBalance()));

    /**
     * 实际交易次数是正确的如下 3 次
     * 1. 初始存储 10，修改交易次数=1
     * 2. 线程1 获取余额=10，获取交易次数=1
     * 3. 线程2 取出 10，修改交易次数=2，余额=0
     * 4. 线程2 存入 10，修改交易次数=3，余额=10
     * 5. 线程1 取出 10 失败，因为此时的交易次数不等于1，在第2，5步之间线程2修改了账户余额。
     */
    assertThat(3, Matchers.is(account.getTransactionCount()));
  }
}