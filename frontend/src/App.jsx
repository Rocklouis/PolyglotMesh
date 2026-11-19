import { useEffect, useState } from "react";
import Editor from "@monaco-editor/react";
import { executeCode } from "./services/executionService";
import "./App.css";

const STORAGE_KEY = "polyglotmesh-code-v1";

const languages = {
  Java: {
    language: "java",
    filename: "Main.java",
    icon: "J",
    code: `public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from Java");
    }
}`
  },
  Python: {
    language: "python",
    filename: "main.py",
    icon: "Py",
    code: `def greet():
    print("Hello from Python")

greet()`
  },
  JavaScript: {
    language: "javascript",
    filename: "main.js",
    icon: "JS",
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
  const [saveStatus, setSaveStatus] = useState("Saved locally");
  const [isRunning, setIsRunning] = useState(false);
  const [executionStatus, setExecutionStatus] = useState("Ready");

  const currentLanguage = languages[activeTab];

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(codes));
      setSaveStatus("Saved locally");
    } catch (error) {
      console.error("Unable to save code:", error);
      setSaveStatus("Save failed");
    }
  }, [codes]);

  const handleEditorChange = (value) => {
    setCodes((previous) => ({
      ...previous,
      [activeTab]: value ?? ""
    }));

    setSaveStatus("Saving...");
  };

const handleRun = async () => {
  if (!codes[activeTab].trim()) {
    setOutput("Error: Cannot execute empty code.");
    setExecutionStatus("Error");
    setIsConsoleOpen(true);
    return;
  }

  setIsRunning(true);
  setExecutionStatus("Running");
  setIsConsoleOpen(true);

  setOutput(
    `Starting ${activeTab} execution...\n\n` +
    `File: ${currentLanguage.filename}\n` +
    `Language: ${activeTab}\n\n` +
    "Executing code..."
  );

  try {
    const result = await executeCode(
      activeTab,
      codes[activeTab]
    );

    if (result.success) {
      setOutput(
        `Execution completed successfully.\n\n` +
        `Output:\n${result.output}`
      );

      setExecutionStatus("Success");
    } else {
      setOutput(
        `Execution failed.\n\n` +
        `Error:\n${result.error}`
      );

      setExecutionStatus("Error");
    }
  } catch (error) {
    setOutput(
      `Execution failed.\n\n` +
      `Error:\n${error.message}`
    );

    setExecutionStatus("Error");
  } finally {
    setIsRunning(false);
  }
};

  const handleReset = () => {
    const confirmed = window.confirm(
      `Restore default ${activeTab} code?`
    );

    if (!confirmed) return;

    setCodes((previous) => ({
      ...previous,
      [activeTab]: languages[activeTab].code
    }));

    setOutput(`${activeTab} code restored to default.`);
    setExecutionStatus("Ready");
    setIsConsoleOpen(true);
  };

  const handleClearOutput = () => {
    setOutput("");
    setExecutionStatus("Ready");
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
            <p>
              Write, manage and prepare multi-language programs
            </p>
          </div>

          <span className="workspace-label">
            LOCAL IDE
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
                  <span className="language-icon">
                    {languages[language].icon}
                  </span>

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
                {isConsoleOpen ? "Hide Console" : "Show Console"}
              </button>

              <button
                className="console-toggle"
                onClick={handleReset}
              >
                Reset Code
              </button>

              <button
                className="run-button"
                onClick={handleRun}
                disabled={isRunning}
              >
                {isRunning ? "Running..." : "▶ Run Code"}
              </button>

            </div>
          </div>

          <div className="file-bar">

            <div className="file-info">
              <span className="file-icon">
                {currentLanguage.icon}
              </span>

              <span>
                {currentLanguage.filename}
              </span>
            </div>

            <div className="file-meta">
              <span className="file-language">
                {activeTab}
              </span>

              <span className="execution-status">
                {executionStatus}
              </span>
            </div>

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
                suggestOnTriggerCharacters: true,
                smoothScrolling: true,
                cursorSmoothCaretAnimation: "on"
              }}
            />

          </div>

          {isConsoleOpen && (
            <div className="console">

              <div className="console-header">

                <div className="console-title">
                  <span className="terminal-dot"></span>
                  TERMINAL / OUTPUT
                </div>

                <button
                  onClick={handleClearOutput}
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
              {saveStatus}
            </span>

          </footer>

        </section>

      </main>

    </div>
  );
}

export default App;