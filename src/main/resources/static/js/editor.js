/** Monaco editor loader (CDN) with textarea-compatible API. */

const MONACO_VERSION = '0.52.2';
const CDN_BASE = `https://cdn.jsdelivr.net/npm/monaco-editor@${MONACO_VERSION}/min`;

let loadPromise = null;

function currentTheme() {
  return document.documentElement.getAttribute('data-theme') === 'dark' ? 'vs-dark' : 'vs';
}

function loadMonaco() {
  if (window.monaco) return Promise.resolve(window.monaco);
  if (loadPromise) return loadPromise;

  loadPromise = new Promise((resolve, reject) => {
    const loaderScript = document.createElement('script');
    loaderScript.src = `${CDN_BASE}/vs/loader.js`;
    loaderScript.async = true;
    loaderScript.onerror = () => reject(new Error('Failed to load Monaco loader'));
    loaderScript.onload = () => {
      try {
        window.require.config({ paths: { vs: `${CDN_BASE}/vs` } });
        window.MonacoEnvironment = {
          getWorkerUrl() {
            const proxy = `
              self.MonacoEnvironment = { baseUrl: '${CDN_BASE}/' };
              importScripts('${CDN_BASE}/vs/base/worker/workerMain.js');
            `;
            return URL.createObjectURL(new Blob([proxy], { type: 'text/javascript' }));
          },
        };
        window.require(['vs/editor/editor.main'], () => {
          if (!window.monaco) {
            reject(new Error('Monaco failed to initialize'));
            return;
          }
          resolve(window.monaco);
        });
      } catch (err) {
        reject(err);
      }
    };
    document.head.appendChild(loaderScript);
  }).catch((err) => {
    loadPromise = null;
    throw err;
  });

  return loadPromise;
}

/**
 * @param {HTMLElement} container
 * @param {string} initialValue
 * @returns {Promise<{ getValue: () => string, setValue: (v: string) => void, dispose: () => void }>}
 */
export async function createEditor(container, initialValue) {
  const monaco = await loadMonaco();
  const editor = monaco.editor.create(container, {
    value: initialValue ?? '',
    language: 'java',
    theme: currentTheme(),
    automaticLayout: true,
    minimap: { enabled: false },
    fontFamily: 'IBM Plex Mono, ui-monospace, monospace',
    fontSize: 13,
    lineHeight: 20,
    tabSize: 4,
    scrollBeyondLastLine: false,
    wordWrap: 'on',
    renderLineHighlight: 'line',
    padding: { top: 8 },
  });

  const onTheme = () => {
    monaco.editor.setTheme(currentTheme());
  };
  const observer = new MutationObserver(onTheme);
  observer.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['data-theme'],
  });

  return {
    getValue: () => editor.getValue(),
    setValue: (v) => editor.setValue(v ?? ''),
    dispose: () => {
      observer.disconnect();
      editor.dispose();
    },
  };
}
