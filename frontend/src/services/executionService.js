const mockOutputs = {
  Java: "Hello from Java",
  Python: "Hello from Python",
  JavaScript: "Hello from JavaScript"
};

export async function executeCode(language, code) {
  await new Promise((resolve) => setTimeout(resolve, 1000));

  if (!code.trim()) {
    return {
      success: false,
      output: "",
      error: "Cannot execute empty code."
    };
  }

  return {
    success: true,
    output: mockOutputs[language] ?? `Mock execution completed for ${language}.`,
    error: null
  };
}