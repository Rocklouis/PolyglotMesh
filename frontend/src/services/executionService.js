const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";

export async function executeCode(language, code) {
  if (!code.trim()) {
    return {
      success: false,
      stdout: "",
      stderr: "",
      value: "",
      error: "Cannot execute empty code."
    };
  }

  try {
    const response = await fetch(
      `${API_BASE_URL}/api/execute`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          language,
          code
        })
      }
    );

    let data;

    try {
      data = await response.json();
    } catch {
      return {
        success: false,
        stdout: "",
        stderr: "",
        value: "",
        error:
          `Backend returned an invalid response (${response.status}).`
      };
    }

    if (!response.ok) {
      return {
        success: false,
        stdout: data.stdout ?? "",
        stderr: data.stderr ?? "",
        value: data.value ?? "",
        error:
          data.error ??
          `Execution request failed (${response.status}).`
      };
    }

    return {
      success: data.success ?? true,
      stdout: data.stdout ?? "",
      stderr: data.stderr ?? "",
      value: data.value ?? "",
      error: data.error ?? null
    };

  } catch (error) {
    return {
      success: false,
      stdout: "",
      stderr: "",
      value: "",
      error:
        `Backend unavailable.\n\n` +
        `Unable to connect to ${API_BASE_URL}/api/execute.\n\n` +
        `Make sure the execution backend is running.`
    };
  }
}