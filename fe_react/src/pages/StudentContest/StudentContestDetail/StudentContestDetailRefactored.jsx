import React, { useState, useEffect } from 'react';
import { Spin, message, Button, Layout, FloatButton } from 'antd';
import { RobotOutlined } from '@ant-design/icons';
import { useParams, useSearchParams } from 'react-router-dom';

import { getContestDetail } from '../../../services/problemContestService';
import ProblemPanel from './components/ProblemPanel/ProblemPanel';
import CodeEditorPanel from './components/CodeEditorPanel/CodeEditorPanel';
import ChatAssistantDrawer from './components/ChatAssistantDrawer/ChatAssistantDrawer';

import {
  useEditorState,
  useSubmissionState,
  useContestData,
  useChatState,
} from '../../../hooks/useContestHooks';

import './StudentContestDetailRefactored.scss';
import { useSelector } from "react-redux";
import { submitProblemContest, getSubmissionDetail, runCode } from '../../../services/studentContestService';

const { Content } = Layout;

const StudentContestDetailRefactored = () => {
  const { id } = useParams(); // problemId
  const [searchParams] = useSearchParams();
  const submissionIdParam = searchParams.get('submissionId');

  const editorState = useEditorState('cpp');
  const submissionState = useSubmissionState();
  const chatState = useChatState();

  // State for view mode (viewing a past submission)
  const [isViewMode, setIsViewMode] = useState(!!submissionIdParam);
  const [submissionData, setSubmissionData] = useState(null);
  const [submissionLoading, setSubmissionLoading] = useState(!!submissionIdParam);

  // State for current submission (after submit for AI debug)
  const [currentSubmission, setCurrentSubmission] = useState(null);

  const { problem, loading, error } = useContestData(id, getContestDetail);

  const { userId } = useSelector((state) => state.userReducer);

  // Track latest submission ID for chat
  const [latestSubmissionId, setLatestSubmissionId] = React.useState(null);

  // maping language
  const LANGUAGE_ID_MAP = {
    c: 50,
    cpp: 54,
    java: 62,
    python: 71,
    csharp: 51,
  };

  // Effect to load submission data if submissionId is provided
  useEffect(() => {
    if (!submissionIdParam) {
      setIsViewMode(false);
      setSubmissionData(null);
      setSubmissionLoading(false);
      return;
    }

    // Load submission detail
    setSubmissionLoading(true);
    getSubmissionDetail(
      submissionIdParam,
      (response) => {
        // Handle response structure - backend may return data in response.data or directly
        const submissionDetail = response?.data || response;
        setIsViewMode(true);
        setSubmissionData(submissionDetail);
        setSubmissionLoading(false);
      },
      (error) => {
        console.error('Error loading submission:', error);
        message.error('Không thể tải chi tiết bài nộp');
        setSubmissionLoading(false);
      }
    );
  }, [submissionIdParam]);

  /* ===================== HANDLERS ===================== */

  const handleRun = async ({ code, language, problemId }) => {
    return new Promise((resolve) => {
      try {
        submissionState.startRunning();

        // ===== LOG RUN DATA =====
        console.group('▶️ CODE RUN (SAMPLE TEST)');
        const finalProblemId = problemId || problem?.id;
        console.log('🔹 Run Data:', { code, language, problemId: finalProblemId });
        console.log('🔹 Code Length:', code?.length || 0, 'characters');
        console.log('🔹 Language:', language);
        console.groupEnd();

        // ===== CALL BACKEND API =====
        const runParams = {
          source_code: code,
          language_id: LANGUAGE_ID_MAP[language],
          problemId: finalProblemId,
        };

        runCode(
          runParams,
          (response) => {
            const runResults = response?.data || response;
            
            // ===== TRANSFORM API RESPONSE =====
            // API response format: [{ stdout, stderr, time, memory, status: { id, description }, ... }]
            const transformedResults = Array.isArray(runResults) ? runResults.map((result, idx) => {
              // Use API description if available (more reliable), fallback to hardcoded mapping
              const statusMap = {
                1: 'ACCEPTED',
                2: 'WRONG_ANSWER',
                3: 'TIME_LIMIT_EXCEEDED',
                4: 'WRONG_ANSWER',
                5: 'MEMORY_LIMIT_EXCEEDED',
                6: 'RUNTIME_ERROR',
                7: 'COMPILE_ERROR',
              };

              // Normalize status description from API (Accepted → ACCEPTED, Wrong Answer → WRONG_ANSWER, etc.)
              const normalizeStatus = (desc) => {
                if (!desc) return 'UNKNOWN';
                return desc.toUpperCase().replace(/ /g, '_');
              };

              return {
                id: idx + 1,
                status: result.status?.description ? normalizeStatus(result.status.description) : (statusMap[result.status?.id] || 'UNKNOWN'),
                time: `${parseFloat(result.time || 0).toFixed(3)}s`,
                memory: `${(result.memory || 0) / 1024}MB`,
                stdout: result.stdout || '',
                stderr: result.stderr || '',
                compile_output: result.compile_output || '',
              };
            }) : [];

            // ===== LOG RUN RESULT =====
            console.group('📊 RUN RESULT');
            console.log('✅ Test Results:', transformedResults);
            console.groupEnd();

            submissionState.setTestResults(transformedResults);
            submissionState.endRunning();
            message.success('✅ Run success');
            resolve(transformedResults);
          },
          (error) => {
            console.error('❌ RUN ERROR:', error);
            submissionState.endRunning();
            message.error(error?.response?.data?.message || 'Run failed');
            resolve([]);
          }
        );
      } catch (e) {
        console.error('❌ UNEXPECTED ERROR:', e);
        submissionState.endRunning();
        message.error('An unexpected error occurred');
        resolve([]);
      }
    });
  };

  const handleSubmit = ({ code, language, problemId }) => {
    return new Promise((resolve, reject) => {
      submissionState.startSubmitting();

      const submissionData = {
        source_code: code,
        language_id: LANGUAGE_ID_MAP[language],
        studentId: userId,
        problemId: problemId || problem?.id,
      };

      submitProblemContest(
        submissionData,
        (response) => {
          submissionState.endSubmitting();
          const submitResultData = response?.data || response;
          submissionState.setSubmissionResults(submitResultData);
          
          // Save current submission for AI debug
          setCurrentSubmission(submitResultData);
          
          // Update latest submission ID for chat
          if (submitResultData?.submissionId) {
            setLatestSubmissionId(submitResultData.submissionId);
          }
          
          console.log('📥 BACKEND RESPONSE:', submitResultData);
          message.success('✅ Đã nộp bài thành công!');
          resolve(submitResultData);
        },
        (error) => {
          submissionState.endSubmitting();
          console.error('❌ SUBMIT ERROR:', error);
          message.error('Submit failed');
          reject(error);
        }
      );
    });
  };


  /* ===================== STATES ===================== */

  if (submissionLoading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
      </div>
    );
  }

  if (loading) {
    return (
      <div className="page-loading">
        <Spin size="large" />
      </div>
    );
  }

  if (isViewMode && !submissionData) {
    return (
      <div className="page-error">
        <p>Không thể tải chi tiết bài nộp</p>
        <Button onClick={() => window.location.reload()}>Tải lại</Button>
      </div>
    );
  }

  if (error || !problem) {
    return (
      <div className="page-error">
        <p>Failed to load problem</p>
        <Button onClick={() => window.location.reload()}>Reload</Button>
      </div>
    );
  }

  const userPermissions = {
    canViewSolution: true,
    canAccessCode: false,
  };

  /* ===================== RENDER ===================== */

  return (
    <Layout className="leetcode-layout">
      {/* ===== BODY ===== */}
      <Content className="leetcode-body">
        <div className="leetcode-split">
          {/* LEFT */}
          <div className="panel left">
            <ProblemPanel
              problem={problem}
              userPermissions={userPermissions}
            />
          </div>

          {/* RIGHT */}
          <div className="panel right">
            <CodeEditorPanel
              problem={problem}
              onRun={handleRun}
              onSubmit={handleSubmit}
              isViewMode={isViewMode}
              submissionData={submissionData}
            />
          </div>
        </div>
      </Content>

      {/* CHAT */}
      {!isViewMode && (
        <ChatAssistantDrawer
          isOpen={chatState.isChatOpen}
          onClose={chatState.closeChat}
          problem={problem}
          submissionId={currentSubmission?.submissionId || latestSubmissionId}
          currentSubmission={currentSubmission}
          onSubmissionUpdate={(submissionId) => {
            setLatestSubmissionId(submissionId);
            if (submissionId) {
              // Optionally update currentSubmission if needed
            }
          }}
        />
      )}

      {!isViewMode && (
        <FloatButton
          icon={<RobotOutlined />}
          type="primary"
          onClick={chatState.toggleChat}
        />
      )}
    </Layout>
  );
};

export default StudentContestDetailRefactored;
