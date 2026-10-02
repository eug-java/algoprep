const { test, expect } = require('@playwright/test');

async function openApp(page, path = '/') {
  await page.addInitScript(() => {
    localStorage.setItem('algoprep.onboarded.v1', '1');
  });
  await page.goto(path);
  // Safety: if overlay still appears, dismiss it.
  const skip = page.locator('#onboard-skip');
  if (await skip.isVisible().catch(() => false)) {
    await skip.click();
  }
}

test.describe('AlgoPrep smoke', () => {
  test('home loads and shows brand', async ({ page }) => {
    await openApp(page, '/');
    await expect(page.locator('.brand-mark')).toContainText('AlgoPrep');
  });

  test('patterns page lists modules', async ({ page }) => {
    await openApp(page, '/#/patterns');
    await expect(page.locator('.pattern-row').first()).toBeVisible({ timeout: 15_000 });
  });

  test('quiz can submit an answer', async ({ page }) => {
    await openApp(page, '/#/quiz');
    await expect(page.locator('#onboard-overlay')).toBeHidden({ timeout: 5_000 }).catch(() => {});
    await expect(page.locator('.quiz-option').first()).toBeVisible({ timeout: 15_000 });
    await page.locator('.quiz-option').first().click();
    await page.locator('#quiz-submit').click();
    await expect(page.locator('#quiz-feedback .panel')).toBeVisible({ timeout: 10_000 });
  });

  test('judge template + run wrong solution fails', async ({ request }) => {
    const template = await request.get('/api/v1/judge/template/TWO_POINTERS/pair-with-target-sum');
    expect(template.ok()).toBeTruthy();
    const body = await template.json();
    expect(body.source).toContain('class Solution');

    const judge = await request.post('/api/v1/judge', {
      data: {
        patternId: 'TWO_POINTERS',
        problemId: 'pair-with-target-sum',
        source: body.source,
        lang: 'en',
      },
    });
    expect(judge.ok()).toBeTruthy();
    const result = await judge.json();
    expect(result.ok).toBeFalsy();
  });

  test('sync key round-trip', async ({ request }) => {
    const created = await request.post('/api/v1/sync/keys');
    expect(created.ok()).toBeTruthy();
    const { syncKey } = await created.json();
    const push = await request.put(`/api/v1/sync/${syncKey}`, {
      data: { version: 1, progress: { 'demo:a': 1 }, meta: {}, clientId: 'e2e' },
    });
    expect(push.ok()).toBeTruthy();
    const pull = await request.get(`/api/v1/sync/${syncKey}`);
    const remote = await pull.json();
    expect(remote.progress['demo:a']).toBe(1);
  });

  test('problem page stages hints and shows an example', async ({ page }) => {
    await openApp(page, '/#/patterns/HASH_MAPS/problems/two-sum');
    await expect(page.locator('#hint-box li')).toHaveCount(1, { timeout: 15_000 });
    await expect(page.locator('#hint-box .hint-stage').first()).toHaveText('Question');
    await page.locator('#btn-next-hint').click();
    await expect(page.locator('#hint-box li')).toHaveCount(2);
    await expect(page.locator('#hint-box .hint-stage').nth(1)).toHaveText('Invariant');
    await page.locator('#btn-next-hint').click();
    await expect(page.locator('#hint-box li')).toHaveCount(3);
    await expect(page.locator('#hint-box .hint-stage').nth(2)).toHaveText('Move');
    await expect(page.locator('#btn-next-hint')).toHaveCount(0);
    await expect(page.locator('pre.ascii-walk').first()).toContainText('[2, 7, 11, 15]');
  });

  test('review and mock routes render', async ({ page }) => {
    await openApp(page, '/#/review');
    await expect(page.getByRole('heading').first()).toBeVisible({ timeout: 15_000 });
    await openApp(page, '/#/mock');
    await expect(page.getByRole('heading').first()).toBeVisible({ timeout: 15_000 });
  });

  test('spring discussion problem has no run button', async ({ page }) => {
    await openApp(page, '/#/patterns/SPRING_BOOT_INTERVIEW/problems/idempotency-store');
    await expect(page.getByRole('heading').first()).toBeVisible({ timeout: 15_000 });
    await expect(page.locator('#btn-run')).toHaveCount(0);
  });

  test('plans and report routes render', async ({ page }) => {
    await openApp(page, '/#/plans');
    await expect(page.getByRole('heading').first()).toBeVisible({ timeout: 15_000 });
    await openApp(page, '/#/report');
    await expect(page.getByRole('heading').first()).toBeVisible({ timeout: 15_000 });
  });
});
