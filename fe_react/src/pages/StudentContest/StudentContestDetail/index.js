/**
 * Index file for StudentContestDetail
 * Enables clean imports from the directory
 */

// Main Component
export { default as StudentContestDetailRefactored } from './StudentContestDetailRefactored';

// Sub-components
export { default as ProblemPanel } from './components/ProblemPanel/ProblemPanel';
export { default as CodeEditorPanel } from './components/CodeEditorPanel/CodeEditorPanel';
export { default as ResultPanel } from './components/ResultPanel/ResultPanel';
export { default as ChatAssistantDrawer } from './components/ChatAssistantDrawer/ChatAssistantDrawer';

// Hooks
export {
  useEditorState,
  useSubmissionState,
  useContestData,
  useChatState,
} from '../../../hooks/useContestHooks';

// Services
export { runCode, submitSolution, saveDraft, getSubmissionHistory } from '../../../services/studentContestService';

// Usage:
// import { StudentContestDetailRefactored } from './StudentContestDetail';
// or
// import StudentContestDetailRefactored from './StudentContestDetail/StudentContestDetailRefactored';
