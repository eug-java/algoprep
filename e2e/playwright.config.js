// @ts-check
const { defineConfig } = require('@playwright/test');

const baseURL = process.env.ALGOPREP_BASE_URL || 'http://127.0.0.1:18080';

module.exports = defineConfig({
  testDir: './tests',
  timeout: 60_000,
  retries: 0,
  use: {
    baseURL,
    headless: true,
  },
  webServer: process.env.ALGOPREP_E2E_NO_SERVER
    ? undefined
    : {
        command: 'cd .. && JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64} ./mvnw -q -DskipTests spring-boot:run',
        url: baseURL + '/actuator/health',
        reuseExistingServer: true,
        timeout: 180_000,
      },
});
