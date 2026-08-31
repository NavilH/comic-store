import { test, expect } from '@playwright/test';

const COMICS = [
  {
    id: 1,
    title: 'The Amazing Spider-Man',
    issueNumber: 1,
    publisher: { id: 1, name: 'Marvel Comics', country: 'USA' },
    authors: [],
    genre: 'SUPERHERO',
    price: 4.99,
  },
  {
    id: 4,
    title: 'Watchmen',
    issueNumber: 1,
    publisher: { id: 2, name: 'DC Comics', country: 'USA' },
    authors: [],
    genre: 'ACTION',
    price: 5.99,
  },
];

test('admin can sign in, record a sale, and see it in the sales list', async ({ page }) => {
  const sales: Array<Record<string, unknown>> = [
    {
      id: 1,
      saleDate: '2026-04-20T14:30:00',
      totalAmount: 15.97,
      items: [{ id: 1, comic: COMICS[0], quantity: 2, unitPrice: 4.99 }],
    },
  ];

  await page.route('**/api/auth/login', async route => {
    await route.fulfill({
      json: { token: 'e2e-fake-token', username: 'e2e-admin', role: 'ADMIN' },
    });
  });

  await page.route('**/api/comics?**', async route => {
    await route.fulfill({
      json: { content: COMICS, page: 0, size: 1000, totalElements: COMICS.length, totalPages: 1 },
    });
  });

  await page.route('**/api/publishers', async route => {
    await route.fulfill({ json: [COMICS[0].publisher, COMICS[1].publisher] });
  });

  await page.route('**/api/inventory', async route => {
    await route.fulfill({ json: [] });
  });

  await page.route('**/api/sales**', async route => {
    const request = route.request();
    if (request.method() === 'POST') {
      const body = request.postDataJSON() as { items: { comicId: number; quantity: number }[] };
      const items = body.items.map((item, i) => {
        const comic = COMICS.find(c => c.id === item.comicId)!;
        return { id: 900 + i, comic, quantity: item.quantity, unitPrice: comic.price };
      });
      const totalAmount = items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);
      const sale = { id: 999, saleDate: new Date().toISOString(), totalAmount, items };
      sales.unshift(sale);
      await route.fulfill({ json: sale });
      return;
    }
    await route.fulfill({
      json: { content: sales, page: 0, size: 20, totalElements: sales.length, totalPages: 1 },
    });
  });

  await page.goto('/login');
  await page.getByLabel('Username').fill('e2e-admin');
  await page.getByLabel('Password').fill('password123');
  await page.getByRole('button', { name: 'Sign In' }).click();
  await expect(page).toHaveURL(/\/dashboard$/);

  await page.getByRole('link', { name: 'Sales' }).click();
  await expect(page).toHaveURL(/\/sales$/);
  await expect(page.getByRole('row')).toHaveCount(2);

  await page.getByRole('button', { name: 'New Sale' }).click();
  await expect(page.getByRole('heading', { name: 'New Sale' })).toBeVisible();

  await page.locator('mat-select[formcontrolname="comicId"]').click();
  await page.getByRole('option', { name: 'Watchmen' }).click();
  await page.locator('input[formcontrolname="quantity"]').fill('3');

  await expect(page.getByRole('button', { name: 'Complete Sale' })).toBeEnabled();
  await page.getByRole('button', { name: 'Complete Sale' }).click();

  await expect(page.getByRole('heading', { name: 'New Sale' })).not.toBeVisible();
  await expect(page.getByRole('row')).toHaveCount(3);
  await expect(page.getByRole('cell', { name: '$17.97' })).toBeVisible();
});
