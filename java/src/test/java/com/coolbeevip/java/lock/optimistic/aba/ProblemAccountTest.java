package com.coolbeevip.java.lock.optimistic.aba;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

/**
 * 并发编程中 ABA 问题的理论背景
 *
 * 例如：
 * 假设一个活动读取一些共享内存 (A)，以准备更新它。
 * 然后，另一个活动临时修改该共享内存 (B)，然后将其恢复 (A)。
 * 之后，一旦第一个活动执行了比较和交换，它将看起来好像没有进行任何更改，从而使检查的完整性无效。
 * 虽然在许多情况下这不会导致问题，但有时，A 并不像我们想象的那样等于 A。让我们看看这在实践中的表现
 *
 * 为了通过一个实际例子来演示这个问题，让我们考虑一个简单的银行账户类，其中一个整数变量保存实际余额的金额。我们还有两种功能：一种用于取款，一种用于存款。这些操作使用 CAS 来减少和增加账户余额。
 *
 * 过程：
 * 当线程 1 想要提取一些钱时，它会读取实际余额以使用该值来比较 CAS 操作中的金额。然而，出于某种原因，线程 1 有点慢——也许它被阻塞了。
 * 同时，线程 2 在线程 1 挂起时使用相同的机制对帐户执行两个操作。首先，它更改已被线程 1 读取的原始值，但随后又将其更改回原始值。
 * 一旦线程1恢复，就好像什么都没变，CAS就成功了（这就产生了 ABA 问题）
 *
 * https://www.baeldung.com/cs/aba-concurrency
 */
public class ProblemAccountTest {

  private ProblemAccount account;

  @Before
  public void setUp() {
    account = new ProblemAccount();
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
    final int defaultBalance = 50;

    final int amountToWithdrawByThread1 = 20;
    final int amountToWithdrawByThread2 = 10;
    final int amountToDepositByThread2 = 10;

    /**
     * 存入 50
     */
    assertThat(0, Matchers.is(account.getTransactionCount()));
    assertThat(0, Matchers.is(account.getCurrentThreadCASFailureCount()));
    account.deposit(defaultBalance);
    assertThat(1, Matchers.is(account.getTransactionCount())); // 交易计数器 1

    Thread thread1 = new Thread(() -> {
      // 此取现方法在获取用户余额 20 后将等待线程2先执行完
      assertThat(account.withdraw(amountToWithdrawByThread1), Matchers.is(true));
      /**
       * 取现成功，产生 ABA 问题
       * 此时应该取现失败，因为此时账户余额中的 20 已经并不是最初的 20，而是线程2完成了（20+10-10）后的 20
       */
      assertThat(1, Matchers.not(account.getCurrentThreadCASFailureCount()));
      assertThat(4, Matchers.is(account.getTransactionCount())); // 交易计数器 4

    }, "thread1");

    Thread thread2 = new Thread(() -> {
      // 线程2 存入 10 元后，账户余额等于 30 元
      assertThat(account.deposit(amountToDepositByThread2), Matchers.is(true));
      assertThat(defaultBalance + amountToDepositByThread2, Matchers.is(account.getBalance()));
      assertThat(2, Matchers.is(account.getTransactionCount())); // 交易计数器 2

      // 此处人为控制，线程2此方法先执行，取现 10 元后，账户余额 20 元
      assertThat(account.withdraw(amountToWithdrawByThread2), Matchers.is(true));
      assertThat(3, Matchers.is(account.getTransactionCount())); // 交易计数器 3

      // 在线程1真正扣除余额前，账户余额又恢复到了 20 元
      assertThat(defaultBalance, Matchers.is(account.getBalance()));
      assertThat(0, Matchers.is(account.getCurrentThreadCASFailureCount()));
    }, "thread2");

    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();

    /**
     * 线程1取款操作成功（比较更新成功）
     */
    assertThat(defaultBalance - amountToWithdrawByThread1, Matchers.is(account.getBalance()));

    /**
     * 期望交易次数是正确的如下 3 次
     * 1. 存入 20
     * 2. 线程2 存入 10
     * 3. 线程2 取出 10
     */
    assertThat(3, Matchers.not(account.getTransactionCount()));

    /**
     * 实际交易次数是不正确的如下 4 次
     * 1. 存入 20
     * 2. 线程2 存入 10
     * 3. 线程2 取出 10
     * 4. 线程1 取出 10 （这一步应该失败）
     */
    assertThat(4, Matchers.is(account.getTransactionCount()));
  }
}