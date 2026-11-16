import { useEffect, useState } from "react";
import Editor from "@monaco-editor/react";
import "./App.css";

const STORAGE_KEY = "polyglotmesh-code-v1";

const languages = {
  Java: {
    language: "java",
    filename: "Main.java",
    icon: "☕",
    code: `public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Java");
    }
}`
  },
  Python: {
    language: "python",
    filename: "main.py",
    icon: "🐍",
    code: `def greet():
    print("Hello from Python")

greet()`
  },
  JavaScript: {
    language: "javascript",
    filename: "main.js",
    icon: "🟨",
    code: `function greet() {
    console.log("Hello from JavaScript");
}

greet();`
  }
};

function loadSavedCodes() {
  try {
    const savedCodes = localStorage.getItem(STORAGE_KEY);

    if (savedCodes) {
      const parsedCodes = JSON.parse(savedCodes);

      return {
        Java: parsedCodes.Java ?? languages.Java.code,
        Python: parsedCodes.Python ?? languages.Python.code,
        JavaScript: parsedCodes.JavaScript ?? languages.JavaScript.code
      };
    }
  } catch (error) {
    console.error("Unable to load saved code:", error);
  }

  return {
    Java: languages.Java.code,
    Python: languages.Python.code,
    JavaScript: languages.JavaScript.code
  };
}

function App() {
  const [activeTab, setActiveTab] = useState("Java");
  const [codes, setCodes] = useState(loadSavedCodes);

  const [output, setOutput] = useState(
    "Welcome to PolyglotMesh.\nSelect a language and write your code."
  );

  const [isConsoleOpen, setIsConsoleOpen] = useState(true);

  const currentLanguage = languages[activeTab];

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(codes));
    } catch (error) {
      console.error("Unable to save code:", error);
    }
  }, [codes]);

  const handleEditorChange = (value) => {
    setCodes((previous) => ({
      ...previous,
      [activeTab]: value ?? ""
    }));
  };

  const handleRun = () => {
    setOutput(
      `Preparing ${activeTab} execution...\n\n` +
      "Execution API is not connected yet.\n" +
      "Your code is ready to be sent to the GraalVM backend."
    );

    setIsConsoleOpen(true);
  };

  return (
    <div className="app">
      <header className="header">
        <div className="brand">
          <div className="brand-icon">P</div>

          <div>
            <h1>PolyglotMesh</h1>
            <p>GraalVM Multi-Language Runtime Sandbox</p>
          </div>
        </div>

        <div className="header-status">
          <span className="status-dot"></span>
          Development Environment
        </div>
      </header>

      <main className="workspace">
        <div className="workspace-heading">
          <div>
            <h2>Code Workspace</h2>
            <p>Write and manage multi-language programs</p>
          </div>

          <span className="workspace-label">
            LOCAL EDITOR
          </span>
        </div>

        <section className="editor-card">
          <div className="toolbar">
            <div className="tabs">
              {Object.keys(languages).map((language) => (
                <button
                  key={language}
                  className={
                    activeTab === language
                      ? "tab active"
                      : "tab"
                  }
                  onClick={() => setActiveTab(language)}
                >
                  <span>{languages[language].icon}</span>
                  {language}
                </button>
              ))}
            </div>

            <div className="toolbar-actions">
              <button
                className="console-toggle"
                onClick={() =>
                  setIsConsoleOpen(!isConsoleOpen)
                }
              >
                Console
              </button>

              <button
                className="run-button"
                onClick={handleRun}
              >
                ▶ Run Code
              </button>
            </div>
          </div>

          <div className="file-bar">
            <span>
              {currentLanguage.icon} {currentLanguage.filename}
            </span>

            <span className="file-language">
              {activeTab}
            </span>
          </div>

          <div className="editor-container">
            <Editor
              height="480px"
              language={currentLanguage.language}
              theme="vs-dark"
              value={codes[activeTab]}
              onChange={handleEditorChange}
              options={{
                fontSize: 15,
                minimap: {
                  enabled: false
                },
                automaticLayout: true,
                wordWrap: "on",
                scrollBeyondLastLine: false,
                tabSize: 4,
                padding: {
                  top: 16
                },
                lineNumbers: "on",
                folding: true,
                suggestOnTriggerCharacters: true
              }}
            />
          </div>

          {isConsoleOpen && (
            <div className="console">
              <div className="console-header">
                <span>
                  TERMINAL / OUTPUT
                </span>

                <button
                  onClick={() => setOutput("")}
                >
                  Clear
                </button>
              </div>

              <pre className="console-output">
                {output || "No output available."}
              </pre>
            </div>
          )}

          <footer className="editor-footer">
            <span>
              Language: {activeTab}
            </span>

            <span>
              Monaco Editor
            </span>

            <span>
              UTF-8
            </span>
          </footer>
        </section>
      </main>
    </div>
  );
}

export default App;