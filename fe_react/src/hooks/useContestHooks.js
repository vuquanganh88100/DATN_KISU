/**
 * Custom Hooks for Contest Detail Page
 *
 * useEditorState: Manage code editor state
 * useSubmissionState: Manage submission state and results
 * useContestData: Fetch and manage problem data
 */

import { useState, useCallback, useEffect } from 'react';

/**
 * useEditorState Hook
 * Manages code editor state and preferences
 */
export const useEditorState = (initialLanguage = 'cpp') => {
  const [code, setCode] = useState('// Write your solution here\n\n');
  const [language, setLanguage] = useState(initialLanguage);
  const [fontSize, setFontSize] = useState(14);
  const [theme, setTheme] = useState('dark');

  const resetCode = useCallback(() => {
    setCode('// Write your solution here\n\n');
  }, []);

  const toggleTheme = useCallback(() => {
    setTheme((prev) => (prev === 'dark' ? 'light' : 'dark'));
  }, []);

  return {
    code,
    setCode,
    language,
    setLanguage,
    fontSize,
    setFontSize,
    theme,
    toggleTheme,
    resetCode,
  };
};

/**
 * useSubmissionState Hook
 * Manages submission state and results
 */
export const useSubmissionState = () => {
  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [executionStatus, setExecutionStatus] = useState(null);
  const [testResults, setTestResults] = useState([]);
  const [submissionResults, setSubmissionResults] = useState(null);

  const startRunning = useCallback(() => {
    setIsRunning(true);
    setExecutionStatus('running');
  }, []);

  const endRunning = useCallback(() => {
    setIsRunning(false);
    setExecutionStatus('completed');
  }, []);

  const startSubmitting = useCallback(() => {
    setIsSubmitting(true);
    setExecutionStatus('submitting');
  }, []);

  const endSubmitting = useCallback(() => {
    setIsSubmitting(false);
    setExecutionStatus('submitted');
  }, []);

  const resetResults = useCallback(() => {
    setTestResults([]);
    setSubmissionResults(null);
    setExecutionStatus(null);
  }, []);

  return {
    isRunning,
    startRunning,
    endRunning,
    isSubmitting,
    startSubmitting,
    endSubmitting,
    executionStatus,
    testResults,
    setTestResults,
    submissionResults,
    setSubmissionResults,
    resetResults,
  };
};

/**
 * useContestData Hook
 * Fetch and manage problem data
 */
export const useContestData = (problemId, fetchFn) => {
  const [problem, setProblem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!problemId) {
      setLoading(false);
      return;
    }

    const fetchProblem = async () => {
      try {
        setLoading(true);
        setError(null);

        if (fetchFn && typeof fetchFn === 'function') {
          fetchFn(
            problemId,
            {},
            (data) => {
              setProblem(data);
              setLoading(false);
            },
            (err) => {
              setError(err);
              setLoading(false);
            }
          );
        }
      } catch (err) {
        setError(err);
        setLoading(false);
      }
    };

    fetchProblem();
  }, [problemId, fetchFn]);

  return { problem, loading, error };
};

/**
 * useChatState Hook
 * Manage chat assistant drawer state
 */
export const useChatState = () => {
  const [isChatOpen, setIsChatOpen] = useState(false);

  const toggleChat = useCallback(() => {
    setIsChatOpen((prev) => !prev);
  }, []);

  const openChat = useCallback(() => {
    setIsChatOpen(true);
  }, []);

  const closeChat = useCallback(() => {
    setIsChatOpen(false);
  }, []);

  return {
    isChatOpen,
    toggleChat,
    openChat,
    closeChat,
  };
};

export default {
  useEditorState,
  useSubmissionState,
  useContestData,
  useChatState,
};
