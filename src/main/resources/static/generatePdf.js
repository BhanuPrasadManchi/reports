const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

(async () => {
    const htmlPath = path.resolve('template.html');
    const htmlContent = fs.readFileSync(htmlPath, 'utf8');

    const browser = await puppeteer.launch({ headless: true });
    const page = await browser.newPage();
    await page.setContent(htmlContent, { waitUntil: 'networkidle0' });

    // Wait for jQuery to execute and manipulate the DOM
    await page.waitForFunction(() => document.querySelector('#message').textContent === 'This text was set by jQuery.');

    await page.pdf({ path: 'output.pdf', format: 'A4' });

    await browser.close();
})();