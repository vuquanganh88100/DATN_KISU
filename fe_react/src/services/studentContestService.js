

import { apiPath } from "../config/apiPath";
import { getRequest, postRequest } from "../api/apiCaller";

// const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// /**
//  * Run code against sample test cases
//  */
// export const runCode = async (problemId, code, language) => {
//   try {
//     const response = await axios.post(`${API_BASE}/contests/run`, {
//       problemId,
//       code,
//       language,
//     });
//     return response.data;
//   } catch (error) {
//     throw error;
//   }
// };

// export const submitSolution = async (problemId, code, language) => {
//   try {
//     const response = await axios.post(`${API_BASE}/contests/submit`, {
//       problemId,
//       code,
//       language,
//     });
//     return response.data;
//   } catch (error) {
//     throw error;
//   }
// };

// /**
//  * Save code draft
//  */
// export const saveDraft = async (problemId, code, language) => {
//   try {
//     const response = await axios.post(`${API_BASE}/contests/draft`, {
//       problemId,
//       code,
//       language,
//     });
//     return response.data;
//   } catch (error) {
//     throw error;
//   }
// };

// /**
//  * Get submission history
//  */
// export const getSubmissionHistory = async (problemId) => {
//   try {
//     const response = await axios.get(
//       `${API_BASE}/contests/${problemId}/submissions`
//     );
//     return response.data;
//   } catch (error) {
//     throw error;
//   }
// };

// export default {
//   runCode,
//   submitSolution,
//   saveDraft,
//   getSubmissionHistory,
// };
export const submitProblemContest = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.submitCode}`,
        params,
        successCallback,
        errorCallback
    );
};
export const sendMessagatoChatbot = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.chatBot}`,
        params,
        successCallback,
        errorCallback
    );
};
export const getSubmissionByStudent = async (
    params,
    successCallback,
    errorCallback
) => {
    await getRequest(
        `${apiPath.getSubmissionsByStudent}`,
        params,
        successCallback,
        errorCallback
    );
};

export const getSubmissionDetail = async (
    submissionId,
    successCallback,
    errorCallback
) => {
    await getRequest(
        `${apiPath.getSubmissionsByStudent}/${submissionId}`,
        {},
        successCallback,
        errorCallback
    );
};
export const runCode = async (
    params,
    successCallback,
    errorCallback
) => {
    await postRequest(
        `${apiPath.runCode}`,
        params,
        successCallback,
        errorCallback
    );
};