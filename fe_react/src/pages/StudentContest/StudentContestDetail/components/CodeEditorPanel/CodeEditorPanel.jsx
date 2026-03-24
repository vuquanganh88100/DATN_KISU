import React, { useEffect, useRef, useState } from 'react';
import { Button, Select, Space, message } from 'antd';
import * as monaco from 'monaco-editor';
import ResultPanel from '../ResultPanel/ResultPanel';
import './CodeEditorPanel.scss';

const LANGUAGES = {
  c: {
    label: 'C',
    monaco: 'c',
    template: `#include <stdio.h>

int main() {
    return 0;
}`
  },
  cpp: {
    label: 'C++',
    monaco: 'cpp',
    template: `#include <bits/stdc++.h>
using namespace std;

int main() {
    return 0;
}`
  },
  java: {
    label: 'Java',
    monaco: 'java',
    template: `class Solution {
    public int thirdMax(int[] nums) {
        
    }
}`
  },
  python: {
    label: 'Python',
    monaco: 'python',
    template: `class Solution:
    def thirdMax(self, nums):
        pass`
  },
  csharp: {
    label: 'C#',
    monaco: 'csharp',
    template: `public class Solution {
    public int ThirdMax(int[] nums) {
        
    }
}`
  }
};

// Language mapping from backend to frontend
const LANGUAGE_MAP = {
  'c': 'c',
  'cpp': 'cpp',
  'c++': 'cpp',
  'java': 'java',
  'python': 'python',
  'python3': 'python',
  'csharp': 'csharp',
  'c#': 'csharp',
  'unknown': 'cpp', // Default fallback
  '': 'cpp',
};

// Function to normalize language from backend
const getNormalizedLanguage = (backendLanguage) => {
  if (!backendLanguage) return 'cpp';
  const normalized = backendLanguage.toLowerCase().trim();
  return LANGUAGE_MAP[normalized] || 'cpp';
};

const CodeEditorPanel = ({ problem, onRun, onSubmit, isViewMode = false, submissionData = null }) => {
  const editorRef = useRef(null);
  const editor = useRef(null);

  // Normalize language from backend
  const normalizedLanguage = isViewMode && submissionData 
    ? getNormalizedLanguage(submissionData.language)
    : 'java';

  const [language, setLanguage] = useState(normalizedLanguage);
  const [theme, setTheme] = useState('dark');

  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [runResult, setRunResult] = useState(null);
  const [submitResult, setSubmitResult] = useState(null);

  useEffect(() => {
    // Ensure language is valid, default to cpp if not
    const validLanguage = LANGUAGES[language] ? language : 'cpp';
    let sourceCode = LANGUAGES[validLanguage].template;
    
    // Check for sourceCoude (typo from backend), sourceCode, and source_code
    if (isViewMode && submissionData?.sourceCoude) {
      sourceCode = submissionData.sourceCoude;
    } else if (isViewMode && submissionData?.sourceCode) {
      sourceCode = submissionData.sourceCode;
    } else if (isViewMode && submissionData?.source_code) {
      sourceCode = submissionData.source_code;
    }

    editor.current = monaco.editor.create(editorRef.current, {
      value: sourceCode,
      language: LANGUAGES[validLanguage].monaco,
      theme: theme === 'dark' ? 'vs-dark' : 'vs',
      fontSize: 14,
      minimap: { enabled: false },
      automaticLayout: true,
      readOnly: isViewMode,  // Set read-only mode when viewing submission
    });

    return () => editor.current?.dispose();
  }, []);

  const handleLanguageChange = (lang) => {
    if (isViewMode) return; // Prevent language change in view mode
    
    if (!LANGUAGES[lang]) return; // Safety check
    
    setLanguage(lang);

    monaco.editor.setModelLanguage(
      editor.current.getModel(),
      LANGUAGES[lang].monaco
    );

    editor.current.setValue(LANGUAGES[lang].template);
  };

  /* THEME */
  useEffect(() => {
    monaco.editor.setTheme(theme === 'dark' ? 'vs-dark' : 'vs');
  }, [theme]);

  const handleRun = async () => {
    const code = editor.current.getValue().trim();
    if (!code) return message.warning('Code is empty');

    setIsRunning(true);
    setRunResult(null);

    try {
      const res = await onRun({ 
        code, 
        language, 
        problemId: problem?.id 
      });
      setRunResult(res);
    } catch {
      message.error('Run failed');
    }

    setIsRunning(false);
  };

  /* SUBMIT */
  const handleSubmit = async () => {
    const code = editor.current.getValue().trim();
    if (!code) return message.warning('Code is empty');

    setIsSubmitting(true);
    setSubmitResult(null);

    try {
      const res = await onSubmit({ problem, language, code });
      setSubmitResult(res); // full test result
    } catch {
      message.error('Submit failed');
    }

    setIsSubmitting(false);
  };

  return (
    <div className="code-editor-panel">
      <div className="editor-toolbar">
        <Select
          value={language}
          onChange={handleLanguageChange}
          options={Object.entries(LANGUAGES).map(([k, v]) => ({
            value: k,
            label: v.label,
          }))}
          bordered={false}
          disabled={isViewMode}
        />

        <Button
          type="text"
          onClick={() => setTheme(t => (t === 'dark' ? 'light' : 'dark'))}
          disabled={isViewMode}
        >
          {theme === 'dark' ? '🌙' : '☀️'}
        </Button>
        
        {isViewMode && (
          <span style={{ marginLeft: 'auto', color: '#999', fontSize: '12px' }}>
            📖 Chế độ xem (Chỉ đọc)
          </span>
        )}
      </div>

      <div className="editor-container">
        <div ref={editorRef} className="editor-body" />
      </div>

      {!isViewMode && (
        <div className="editor-footer">
          <Space>
            <Button onClick={handleRun} loading={isRunning}>
              Run
            </Button>
            <Button
              type="primary"
              onClick={handleSubmit}
              loading={isSubmitting}
            >
              Submit
            </Button>
          </Space>
        </div>
      )}
      <ResultPanel
        testCases={problem?.testCases || []}
        testResults={isViewMode && submissionData ? (submissionData.testResults || []) : (runResult || [])}
        submissionResults={isViewMode && submissionData ? submissionData : submitResult}
        isRunning={isRunning}
        isSubmitting={isSubmitting}
      />
    </div>
  );
};

export default CodeEditorPanel;
