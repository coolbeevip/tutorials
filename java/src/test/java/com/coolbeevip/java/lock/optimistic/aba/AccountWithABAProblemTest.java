package com.coolbeevip.java.lock.optimistic.aba;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class AccountWithABAProblemTest {

  private AccountWithABAProblem account;

  @Before
  public void setUp() {
    account = new AccountWithABAProblem();
  }

  @Test
  public void zeroBalanceInitializationTest() {
    assertThat(0, Matchers.is(account.getBalance()));
    assertThat(0, Matchers.is(account.getTransactionCount()));
    assertThat(0, Matchers.is(account.getCurrentThreadCASFailureCount()));
  }

  @Test
  public void depositTest() {
    final int moneyToDeposit = 50;
    assertThat(account.deposit(moneyToDeposit), Matchers.is(true));
    assertThat(moneyToDeposit, Matchers.is(account.getBalance()));
  }

  @Test
  public void withdrawTest() throws InterruptedException {
    final int defaultBalance = 50;
    final int moneyToWithdraw = 20;
    account.deposit(defaultBalance);
    assertThat(account.withdraw(moneyToWithdraw), Matchers.is(true));
    assertThat(defaultBalance - moneyToWithdraw, Matchers.is(account.getBalance()));
  }

  @Test
  public void abaProblemTest() throws InterruptedException {
    final int defaultBalance = 50;

    final int amountToWithdrawByThread1 = 20;
    final int amountToWithdrawByThread2 = 10;
    final int amountToDepositByThread2 = 10;

    assertThat(0, Matchers.is(account.getTransactionCount()));
    assertThat(0, Matchers.is(account.getCurrentThreadCASFailureCount()));
    account.deposit(defaultBalance);
    assertThat(1, Matchers.is(account.getTransactionCount()));

    Thread thread1 = new Thread(() -> {

      // this will take longer due to the name of the thread
      assertThat(account.withdraw(amountToWithdrawByThread1), Matchers.is(true));

      // thread 1 fails to capture ABA problem
      assertThat(1, Matchers.not(account.getCurrentThreadCASFailureCount()));

    }, "thread1");

    Thread thread2 = new Thread(() -> {

      assertThat(account.deposit(amountToDepositByThread2), Matchers.is(true));
      assertThat(defaultBalance + amountToDepositByThread2, Matchers.is(account.getBalance()));

      // this will be fast due to the name of the thread
      assertThat(account.withdraw(amountToWithdrawByThread2), Matchers.is(true));

      // thread 1 didn't finish yet, so the original value will be in place for it
      assertThat(defaultBalance, Matchers.is(account.getBalance()));

      assertThat(0, Matchers.is(account.getCurrentThreadCASFailureCount()));
    }, "thread2");

    thread1.start();
    thread2.start();
    thread1.join();
    thread2.join();

    // compareAndSet operation succeeds for thread 1
    assertThat(defaultBalance - amountToWithdrawByThread1, Matchers.is(account.getBalance()));

    //but there are other transactions
    assertThat(2, Matchers.not(account.getTransactionCount()));

    // thread 2 did two modifications as well
    assertThat(4, Matchers.is(account.getTransactionCount()));
  }
}