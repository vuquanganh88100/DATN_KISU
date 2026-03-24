import React, { useState, useMemo } from 'react';
import { Tabs, Table, Tag, Card, Empty, Modal, Button } from 'antd';
import { PlayCircleOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { useEffect } from "react";
import './ResultPanel.scss';


const VERDICT_MAP = {
  ACCEPTED: { color: '#52c41a', label: 'Accepted', className: 'verdict-accepted' },
  WRONG_ANSWER: { color: '#f5222d', label: 'Wrong Answer', className: 'verdict-wa' },
  TIME_LIMIT_EXCEEDED: { color: '#faad14', label: 'Time Limit Exceeded', className: 'verdict-tle' },
  MEMORY_LIMIT_EXCEEDED: { color: '#faad14', label: 'Memory Limit Exceeded', className: 'verdict-mle' },
  COMPILE_ERROR: { color: '#ff4d4f', label: 'Compile Error', className: 'verdict-ce' },
  RUNTIME_ERROR: { color: '#ff4d4f', label: 'Runtime Error', className: 'verdict-re' },
  AC: { color: '#52c41a', label: 'Accepted', className: 'verdict-accepted' },
  WA: { color: '#f5222d', label: 'Wrong Answer', className: 'verdict-wa' },
  TLE: { color: '#faad14', label: 'Time Limit', className: 'verdict-tle' },
};


const ResultPanel = ({ submissionResults = null, testResults = [], testCases, isRunning, isSubmitting }) => {
  const [activeCase, setActiveCase] = useState(0);
  const [activeTab, setActiveTab] = useState('testcase');
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [selectedTestResult, setSelectedTestResult] = useState(null);

  React.useEffect(() => {
    if (isRunning || isSubmitting || submissionResults || (testResults && testResults.length > 0)) {
      setActiveTab('result');
    }
  }, [isRunning, isSubmitting, submissionResults, testResults]);

  const columns = useMemo(
    () => [
      { title: '#', dataIndex: 'id', width: 60, render: (_, __, index) => index + 1 },
      {
        title: 'Verdict',
        dataIndex: 'status',
        render: (s) => {
          const v = VERDICT_MAP[s] || { label: s };
          return <Tag color={v.color}>{v.label}</Tag>;
        },
      },
      { title: 'Time', dataIndex: 'time', align: 'right' },
      { title: 'Memory', dataIndex: 'memory', align: 'right' },
      {
        title: 'Action',
        key: 'action',
        width: 100,
        align: 'center',
        render: (_, record) => {
          const hasOutput = record.stdout || record.stderr || record.compile_output;
          return hasOutput ? (
            <Button 
              type="link" 
              size="small"
              onClick={() => {
                setSelectedTestResult(record);
                setDetailModalOpen(true);
              }}
            >
              View
            </Button>
          ) : null;
        },
      },
    ],
    []
  );
  useEffect(()=>{
  console.log('submissionResults:', submissionResults);
  console.log('verdict:', submissionResults?.verdict);
  console.log('testResults:', testResults);
},[submissionResults, testResults])

  /* ---------- TEST CASE TAB ---------- */
  const renderTestCase = () => {
    if (!testCases || !testCases.length) {
      return <Empty description="No test cases" />;
    }

    const currentCase = testCases[activeCase];

    return (
      <div className="testcase-panel">
        <div className="testcase-selector">
          {testCases.map((_, i) => (
            <div
              key={i}
              className={`testcase-item ${i === activeCase ? 'active' : ''}`}
              onClick={() => setActiveCase(i)}
            >
              Case {i + 1}
            </div>
          ))}
        </div>

        <div className="testcase-content">
          <div>
            <div className="section-title">Input</div>
            <pre className="code-display">
              {currentCase?.input || ''}
            </pre>
          </div>

          <div>
            <div className="section-title">Expected Output</div>
            <pre className="code-display output">
              {currentCase?.expectedOutput || ''}
            </pre>
          </div>
        </div>
      </div>
    );
  };


  /* ---------- RESULT TAB ---------- */
  const renderResult = () => {
    if (isRunning || isSubmitting) {
      return (
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>{isRunning ? 'Running code...' : 'Submitting solution...'}</p>
        </div>
      );
    }

    if (submissionResults && submissionResults.verdict) {
      const verdictInfo = VERDICT_MAP[submissionResults.verdict] || { label: submissionResults.verdict, color: '#999' };
      const isAccepted = submissionResults.verdict === 'ACCEPTED';
      
      const formatTime = (ms) => {
        if (ms === undefined || ms === null) return '0';
        return typeof ms === 'number' ? ms.toFixed(3) : ms;
      };

      const formatMemory = (kb) => {
        if (kb === undefined || kb === null) return '0';
        return (kb / 1024).toFixed(2);
      };

      const formatDate = (dateStr) => {
        if (!dateStr) return 'N/A';
        const date = new Date(dateStr);
        return isNaN(date.getTime()) ? dateStr : date.toLocaleString();
      };

      return (
        <div className="submission-result">
          <div className={`result-header ${verdictInfo.className}`}>
            <h2 style={{ color: verdictInfo.color }}>{verdictInfo.label}</h2>
            <div className="submission-meta">
              <span>Submitted at: {formatDate(submissionResults.submittedAt)}</span>
              {submissionResults.language && <span>Language: {submissionResults.language}</span>}
            </div>
          </div>

          <div className="result-stats">
            <div className="stat-card">
              <div className="stat-label">Runtime</div>
              <div className="stat-value">{formatTime(submissionResults.runtimeMs)} ms</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Memory</div>
              <div className="stat-value">{formatMemory(submissionResults.memoryKb)} MB</div>
            </div>
          </div>

          <Card className="testcase-summary">
            <div className="summary-info">
              <span className="label">Testcases passed: </span>
              <span className="value">
                {submissionResults.passedTestcases ?? 0} / {submissionResults.totalTestcases ?? 0}
              </span>
            </div>
            <div className="progress-bar-container">
              <div 
                className="progress-bar" 
                style={{ 
                  width: `${((submissionResults.passedTestcases || 0) / (submissionResults.totalTestcases || 1)) * 100}%`,
                  backgroundColor: isAccepted ? '#52c41a' : '#ff4d4f'
                }}
              ></div>
            </div>
          </Card>

          {submissionResults.testCases && submissionResults.testCases.length > 0 && (
            <Table
              size="small"
              columns={columns}
              dataSource={submissionResults.testCases}
              pagination={false}
              className="result-table"
            />
          )}
        </div>
      );
    }

    if (testResults && testResults.length > 0) {
      return (
        <div className="run-results">
          <h3>Test Results</h3>
          <Table
            size="small"
            columns={columns}
            dataSource={testResults}
            pagination={false}
          />
        </div>
      );
    }

    return <Empty description="No results yet. Run or Submit your code to see results here." />;
  };

  return (
    <div className="result-panel-container">
      {/* Detail Modal */}
      <Modal
        title={`Test Case #${selectedTestResult?.id} Details`}
        open={detailModalOpen}
        onCancel={() => setDetailModalOpen(false)}
        footer={[
          <Button key="close" onClick={() => setDetailModalOpen(false)}>
            Close
          </Button>,
        ]}
        width={1000}
      >
        {selectedTestResult && (
          <div style={{ display: 'grid', gridTemplateColumns: selectedTestResult.stderr ? '1fr 1fr 1fr' : '1fr 1fr', gap: '16px' }}>
            <div>
              <h4 style={{ marginBottom: '8px', color: '#52c41a' }}>Output</h4>
              <pre style={{ 
                backgroundColor: '#f5f5f5', 
                padding: '12px', 
                borderRadius: '4px',
                maxHeight: '300px',
                overflow: 'auto',
                fontFamily: 'monospace'
              }}>
                {selectedTestResult.stdout || '(empty)'}
              </pre>
            </div>
            {selectedTestResult.stderr && (
              <div>
                <h4 style={{ marginBottom: '8px', color: '#ff4d4f' }}>Stderr</h4>
                <pre style={{ 
                  backgroundColor: '#fff1f0', 
                  padding: '12px', 
                  borderRadius: '4px',
                  maxHeight: '300px',
                  overflow: 'auto',
                  fontFamily: 'monospace',
                  color: '#ff4d4f'
                }}>
                  {selectedTestResult.stderr}
                </pre>
              </div>
            )}
            {selectedTestResult.compile_output && (
              <div>
                <h4 style={{ marginBottom: '8px', color: '#ff4d4f' }}>Compile Error</h4>
                <pre style={{ 
                  backgroundColor: '#fff1f0', 
                  padding: '12px', 
                  borderRadius: '4px',
                  maxHeight: '300px',
                  overflow: 'auto',
                  fontFamily: 'monospace',
                  color: '#ff4d4f'
                }}>
                  {selectedTestResult.compile_output}
                </pre>
              </div>
            )}
          </div>
        )}
      </Modal>

      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: 'testcase',
            label: (
              <>
                <PlayCircleOutlined /> Test Case
              </>
            ),
            children: renderTestCase(),
          },
          {
            key: 'result',
            label: (
              <>
                <CheckCircleOutlined /> Results
              </>
            ),
            children: <div className="result-panel">{renderResult()}</div>,
          },
        ]}
      />
    </div>
  );
};

export default ResultPanel;
