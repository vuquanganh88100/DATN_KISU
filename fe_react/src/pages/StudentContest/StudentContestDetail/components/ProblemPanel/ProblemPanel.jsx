
import React, { useState } from 'react';
import { Tabs, Tag, Card, Divider, Button, Space, Copy } from 'antd';
import { CopyOutlined, CheckCircleOutlined } from '@ant-design/icons';
import ReactMarkdown from 'react-markdown';
import SolutionTab from './SolutionTab';
import './ProblemPanel.scss';

const ProblemPanel = ({ problem = {}, userPermissions = {} }) => {
  const [copiedExample, setCopiedExample] = useState(null);

  const getDifficultyText = (level) => {
    const levelMap = {
      easy: 'Dễ',
      medium: 'Trung bình',
      hard: 'Khó',
    };

    return levelMap[level] || level;
  };

  const getDifficultyColor = (level) => {
    const levelMap = {
      'Dễ': '#22c55e',
      'Trung bình': '#eab308',
      'Khó': 'var(--hust-color)',
    };

    return levelMap[level] || '#8c8c8c';
  };
  const displayLevel = getDifficultyText(problem.level);

  const examples = problem.testCases
    ?.filter((tc) => tc.public !== false)
    .map((tc) => {
      // Check if backend returns multiple inputs (input1, input2)
      const inputs = [];
      if (tc.input1 && tc.input2) {
        inputs.push({ label: 'Input 1', value: tc.input1 });
        inputs.push({ label: 'Input 2', value: tc.input2 });
      } else if (tc.inputs && Array.isArray(tc.inputs)) {
        // Handle array of inputs
        tc.inputs.forEach((inp, idx) => {
          inputs.push({ label: `Input ${idx + 1}`, value: inp });
        });
      } else if (tc.input) {
        // Fallback to single input
        inputs.push({ label: 'Input', value: tc.input });
      }

      return {
        inputs: inputs,
        output: tc.expectedOutput,
        explanation: tc.explanation || '',
      };
    }) || [];

  // Tab items configuration
  const tabItems = [
    {
      key: 'description',
      label: 'Description',
      children: (
        <div className="problem-content">
          <div className="problem-header">
            <h1 className="problem-title">
              {problem.id}. {problem.title || 'Untitled Problem'}
            </h1>
            <div className="problem-metadata">
              <span
                className="difficulty"
                style={{ color: getDifficultyColor(displayLevel) }}
              >
                {displayLevel}
              </span>
              {problem.topicContestName && (
                <span className="topic-tag">
                  {problem.topicContestName}
                </span>
              )}
            </div>
          </div>

          <div className="description-content">
            {problem.description && (
              <div className="markdown-content">
                <ReactMarkdown>{problem.description}</ReactMarkdown>
              </div>
            )}

            {problem.inputFormat && (
              <div className="format-section">
                <p className="section-subtitle">Input Format:</p>
                <div className="markdown-content">
                  <ReactMarkdown>{problem.inputFormat}</ReactMarkdown>
                </div>
              </div>
            )}

            {problem.outputFormat && (
              <div className="format-section">
                <p className="section-subtitle">Output Format:</p>
                <div className="markdown-content">
                  <ReactMarkdown>{problem.outputFormat}</ReactMarkdown>
                </div>
              </div>
            )}

            {problem.constraints && (
              <div className="constraints-section">
                <p className="section-subtitle">Constraints:</p>
                <div className="markdown-content">
                  <ReactMarkdown>{problem.constraints}</ReactMarkdown>
                </div>
              </div>
            )}

          </div>
        </div>
      ),
    },
  ];

  // Solution tab - only show if user has permission
  if (userPermissions.canViewSolution) {
    tabItems.push({
      key: 'solution',
      label: 'Solution',
      children: (
        <SolutionTab 
          problemId={problem.id} 
          userInfo={userPermissions} 
        />
      ),
    });
  }

  // Discussion tab - placeholder for future
  tabItems.push({
    key: 'discussion',
    label: 'Discussion',
    children: (
      <div className="problem-content">
        <section className="section">
          <h2 className="section-title">Discussion</h2>
          <p className="no-content">Discussion feature coming soon...</p>
        </section>
      </div>
    ),
  });

  return (
    <div className="problem-panel">
      <Tabs
        items={tabItems}
        defaultActiveKey="description"
        className="problem-tabs"
      />
    </div>
  );
};

export default ProblemPanel;
